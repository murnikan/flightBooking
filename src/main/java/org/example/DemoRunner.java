package org.example;

import Users.*;
import airlines.AirlineRepository;
import airports.*;
import exceptions.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
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
        System.out.println("(начало работы)");

        // подгрузка тестового самолета
        var testPlane = planeRepository.findAll().stream()
                .filter(p -> "TestPlane".equalsIgnoreCase(p.getModel()))
                .findFirst()
                .orElseThrow(() -> new PlaneNotFoundException("Самолет TestPlane не найден"));

        List<ManagerUser> managers = userRepository.findByRole(UserRole.MANAGER).stream()
                .filter(u -> u instanceof ManagerUser)
                .map(u -> (ManagerUser) u)
                .toList();
        ManagerUser manager = managers.isEmpty() ? null : managers.get(0);
        if (manager == null) throw new ManagerNotFoundException("В БД нет пользователей с ролью MANAGER");

        List<AdminUser> admins = userRepository.findByRole(UserRole.ADMIN).stream()
                .filter(u -> u instanceof AdminUser)
                .map(u -> (AdminUser) u)
                .toList();
        AdminUser admin = admins.isEmpty() ? null : admins.get(0);
        if (admin == null) throw new AdminNotFoundException("В БД нет пользователей с ролью ADMIN");

        List<CustomerUser> customers = customerUserRepository.findAll();
        if (customers.size() < 3) throw new NotEnoughCustomersException("Нужно минимум 3 CustomerUser для теста");

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
            System.out.println("[Manager] создал FlightRequest для TestPlane, seats = " + requestedFlight.getAvailableSeats());

        } catch (Exception e) {
            throw new FlightRequestException("Ошибка при создании FlightRequest менеджером: " + e.getMessage());
        }

        // админ подтверждает FlightRequest
        Flight approvedFlight;
        admin.approveRequest(manager, fr, flightRepository);
        approvedFlight = fr.getFlight();
        System.out.println("[Admin] подтвердил запрос менеджера, создан рейс ID="
                + approvedFlight.getFlightId() + ", seats=" + approvedFlight.getAvailableSeats());

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

                        System.out.println("[Бронь создана] user=" + customer.getLogin()
                                + " | flightId=" + flightNow.getFlightId()
                                + " | seatsLeft=" + flightNow.getAvailableSeats());
                    }

                } catch (FlightNotFoundException e) {
                    System.out.println("[Ошибка] " + e.getMessage() + " | проверьте рейс");
                } catch (NoSeatsAvailableException e) {
                    System.out.println("[Ошибка] " + e.getMessage() + " | Нет мест на рейс, попробуйте позже или выберите другой рейс");
                } catch (Exception e) {
                    System.out.println("[Ошибка] Неизвестная ошибка для пользователя=" + customer.getLogin()
                             + e.getMessage());
                }
            });
        }

        ex.shutdown();
        ex.awaitTermination(10, TimeUnit.SECONDS);

        // итоговая сводка по рейсам и броням
        flightRepository.findAll().forEach(f ->
                System.out.println(" - flight id=" + f.getFlightId() + ", число мест для бронирования:" + f.getAvailableSeats())
        );

        bookingRepository.findAll().forEach(b -> {
            CustomerUser passenger = b.getPassenger();
            Flight flight = b.getFlight();
            System.out.println(" - bookingId=" + b.getBookingId() + " | passenger="
                    + passenger.getFirstName() + " " + passenger.getLastName()
                    + " | flightId=" + flight.getFlightId()
                    + " | departure=" + flight.getDepartureAirportCode()
                    + " | arrival=" + flight.getArrivalAirportCode());
        });
    }
}
