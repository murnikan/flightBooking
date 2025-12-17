package org.example;

import Users.*;
import airlines.AirlineRepository;
import airports.*;
import exceptions.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import planes.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.*;

@Component
@Profile("!test")
@RequiredArgsConstructor
public class DemoRunner implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DemoRunner.class);

    private final FlightRepository flightRepository;
    private final BookingRepository bookingRepository;
    private final CustomerUserRepository customerUserRepository;
    private final UserRepository userRepository;

    private final AirportRepository airportRepository;
    private final PlaneRepository planeRepository;
    private final AirlineRepository airlineRepository;

    @PersistenceContext
    private final EntityManager em;

    private final ConcurrentMap<Long, Object> flightLocks = new ConcurrentHashMap<>();

    @Override
    public void run(String... args) throws Exception {
        log.info("(начало работы)");

        // подгрузка тестового самолета
        var testPlane = planeRepository.findAll().stream()
                .filter(p -> "TestPlane".equalsIgnoreCase(p.getModel()))
                .findFirst()
                .orElseThrow(() -> new PlaneNotFoundException("самолет TestPlane не найден"));

        log.info("найден тестовый самолет: model={}, registration={}",
                testPlane.getModel(), testPlane.getRegistrationNumber());

        List<ManagerUser> managers = userRepository.findByRole(UserRole.MANAGER).stream()
                .filter(u -> u instanceof ManagerUser)
                .map(u -> (ManagerUser) u)
                .toList();
        ManagerUser manager = managers.isEmpty() ? null : managers.get(0);
        if (manager == null) {
            log.error("в БД нет пользователей с ролью MANAGER");
            throw new ManagerNotFoundException("в БД нет пользователей с ролью MANAGER");
        }

        List<AdminUser> admins = userRepository.findByRole(UserRole.ADMIN).stream()
                .filter(u -> u instanceof AdminUser)
                .map(u -> (AdminUser) u)
                .toList();
        AdminUser admin = admins.isEmpty() ? null : admins.get(0);
        if (admin == null) {
            log.error("в БД нет пользователей с ролью ADMIN");
            throw new AdminNotFoundException("в БД нет пользователей с ролью ADMIN");
        }

        List<CustomerUser> customers = customerUserRepository.findAll();
        if (customers.size() < 3) {
            log.error("недостаточно CustomerUser для теста: {}", customers.size());
            throw new NotEnoughCustomersException("нужно минимум 3 CustomerUser для теста");
        }

        // менеджер создает FlightRequest
        Users.FlightRequest fr;
        try {
            var airports = airportRepository.findAll();
            var departure = airports.isEmpty() ? null : airports.get(0);
            var arrival = (airports.size() > 1) ? airports.get(1) : departure;

            Flight requestedFlight = new Flight();
            requestedFlight.setDepartureAirportCode(departure != null ? departure.getCode() : "XXX");
            requestedFlight.setArrivalAirportCode(arrival != null ? arrival.getCode() : "YYY");
            requestedFlight.setPlaneRegistration(testPlane.getRegistrationNumber());
            requestedFlight.setAirlineCode(airlineRepository.findAll().stream().findFirst()
                    .map(a -> a.getIataCode()).orElse("TS"));
            requestedFlight.setDepartureTime(LocalDateTime.now().plusDays(1));
            requestedFlight.setArrivalTime(LocalDateTime.now().plusDays(1).plusHours(2));
            requestedFlight.setAvailableSeats(testPlane.getCapacity());

            fr = new Users.FlightRequest(requestedFlight, Users.FlightRequest.RequestType.CREATE);

            log.info("[Manager] создал FlightRequest для TestPlane, seats={}",
                    requestedFlight.getAvailableSeats());

        } catch (Exception e) {
            log.error("ошибка при создании FlightRequest менеджером", e);
            throw new FlightRequestException("ошибка при создании FlightRequest менеджером: " + e.getMessage());
        }

        // админ подтверждает FlightRequest
        admin.approveRequest(manager, fr, flightRepository);
        Flight approvedFlight = fr.getFlight();

        log.info("[Admin] подтвердил запрос менеджера, создан рейс ID={}, seats={}",
                approvedFlight.getFlightId(), approvedFlight.getAvailableSeats());

        // многопоточное бронирование
        int threads = 3;
        ExecutorService ex = Executors.newFixedThreadPool(threads);

        for (int i = 0; i < 3; i++) {
            final CustomerUser customer = customers.get(i);
            ex.submit(() -> {
                int delay = ThreadLocalRandom.current().nextInt(50, 400);
                try {
                    Thread.sleep(delay);

                    Long flightId = approvedFlight.getFlightId();
                    Object lock = flightLocks.computeIfAbsent(flightId, k -> new Object());

                    synchronized (lock) {
                        Flight flightNow = flightRepository.findById(flightId)
                                .orElseThrow(() -> new FlightNotFoundException(
                                        "Рейс с ID=" + flightId + " не найден для пользователя " + customer.getLogin()));

                        if (flightNow.getAvailableSeats() <= 0) {
                            throw new NoSeatsAvailableException(
                                    "Нет мест на рейс ID=" + flightId + " для пользователя " + customer.getLogin());
                        }

                        flightNow.setAvailableSeats(flightNow.getAvailableSeats() - 1);
                        flightRepository.save(flightNow);

                        Booking b = new Booking();
                        b.setPassenger(customer);
                        b.setFlight(flightNow);
                        b.setBookingTime(LocalDateTime.now());
                        bookingRepository.save(b);

                        log.info("[Бронь создана] user={} | flightId={} | seatsLeft={}",
                                customer.getLogin(),
                                flightNow.getFlightId(),
                                flightNow.getAvailableSeats());
                    }

                } catch (FlightNotFoundException e) {
                    log.warn("[Ошибка] {} | проверьте рейс", e.getMessage());
                } catch (NoSeatsAvailableException e) {
                    log.warn("[Ошибка] {} | нет мест на рейс, попробуйте позже или выберите другой рейс",
                            e.getMessage());
                } catch (Exception e) {
                    log.error("[Ошибка] неизвестная ошибка для пользователя={}",
                            customer.getLogin(), e);
                }
            });
        }

        ex.shutdown();
        ex.awaitTermination(10, TimeUnit.SECONDS);

        // итоговая сводка по рейсам и броням
        flightRepository.findAll().forEach(f ->
                log.info(" - flight id={}, число мест для бронирования:{}",
                        f.getFlightId(), f.getAvailableSeats())
        );

        bookingRepository.findAll().forEach(b -> {
            CustomerUser passenger = b.getPassenger();
            Flight flight = b.getFlight();
            log.info(" - bookingId={} | passenger={} {} | flightId={} | departure={} | arrival={}",
                    b.getBookingId(),
                    passenger.getFirstName(),
                    passenger.getLastName(),
                    flight.getFlightId(),
                    flight.getDepartureAirportCode(),
                    flight.getArrivalAirportCode());
        });
    }
}
