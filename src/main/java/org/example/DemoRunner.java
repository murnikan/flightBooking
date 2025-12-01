package org.example;

import Users.*;
import airlines.AirlineRepository;
import airports.*;
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

        // подгрузка тестового самолета для работы
        var testPlane = planeRepository.findAll().stream()
                .filter(p -> "TestPlane".equalsIgnoreCase(p.getModel()))

                .findFirst()
                .orElse(null);

        if (testPlane == null) {
            System.out.println("[ERROR] Самолет TestPlane не найден в БД");
            return;
        }

        System.out.println("[INFO] Найден самолет TestPlane, capacity = " + testPlane.getCapacity());

        // роли

        List<ManagerUser> managers = userRepository.findByRole(UserRole.MANAGER).stream()
                .filter(u -> u instanceof ManagerUser)
                .map(u -> (ManagerUser) u)
                .toList();
        ManagerUser manager = managers.isEmpty() ? null : managers.get(0);

        List<AdminUser> admins = userRepository.findByRole(UserRole.ADMIN).stream()
                .filter(u -> u instanceof AdminUser)
                .map(u -> (AdminUser) u)
                .toList();
        AdminUser admin = admins.isEmpty() ? null : admins.get(0);
        List<CustomerUser> customers = customerUserRepository.findAll();
        if (customers.size() < 3) {
            System.out.println("[ERROR] Нужно минимум 3 CustomerUser в БД для теста");// 3 тк в самолете 2 места всего
            return;
        }

        //менеджер реализует свой функционал
        Users.FlightRequest fr = null;
        if (manager != null) {
            var airports = airportRepository.findAll();
            var departure = airports.isEmpty() ? null : airports.get(0);
            var arrival = (airports.size() > 1) ? airports.get(1) : departure;

            Flight requestedFlight = new Flight();
            requestedFlight.setDepartureAirportCode(departure != null ? departure.getCode() : "XXX");
            requestedFlight.setArrivalAirportCode(arrival != null ? arrival.getCode() : "YYY");
            requestedFlight.setPlaneRegistration(testPlane.getRegistrationNumber());
            requestedFlight.setAirlineCode(airlineRepository.findAll().stream().findFirst().map(a -> a.getIataCode()).orElse("TS"));
            requestedFlight.setDepartureTime(LocalDateTime.now().plusDays(1));
            requestedFlight.setArrivalTime(LocalDateTime.now().plusDays(1).plusHours(2));
            requestedFlight.setAvailableSeats(testPlane.getCapacity());

            fr = new Users.FlightRequest(requestedFlight, Users.FlightRequest.RequestType.CREATE);

            System.out.println("[Manager] создал FlightRequest для TestPlane, seats = " + requestedFlight.getAvailableSeats());
        } else {
            System.out.println("[ERROR] Менеджер отсутствует — нельзя создать запрос");
            return;
        }

        //админ работает с реквестом
        Flight approvedFlight;
        if (admin != null && fr != null) {
            approvedFlight = flightRepository.save(fr.getFlight());
            System.out.println("[Admin] подтвердил запрос, создан рейс ID=" + approvedFlight.getFlightId()
                    + ", seats=" + approvedFlight.getAvailableSeats());
        } else {
            System.out.println("[ERROR] Админ или запрос отсутствует");
            return;
        }

        //многопоточка
        int threads = 3;
        ExecutorService ex = Executors.newFixedThreadPool(threads);
        List<Future<Void>> futures = new ArrayList<>();

        for (int i = 0; i < 3; i++) {
            final CustomerUser customer = customers.get(i);
            futures.add(ex.submit(() -> {
                int delay = ThreadLocalRandom.current().nextInt(50, 400);
                Thread.sleep(delay);

                Long flightId = approvedFlight.getFlightId();
                Object lock = flightLocks.computeIfAbsent(flightId, k -> new Object());

                synchronized (lock) {
                    try {
                        Optional<Flight> opt = flightRepository.findById(flightId);
                        if (opt.isEmpty()) {
                            System.out.println("[ошибка бронирования] Данные пользователя=" +
                                    customer.getLogin() + " | рейс не найден");
                            return null;
                        }
                        Flight flightNow = opt.get();

                        int seatsLeft = flightNow.getAvailableSeats();
                        if (seatsLeft <= 0) {
                            System.out.println("[Бронь отклонена, нет мест] Данные пользователя=" + customer.getLogin()
                                    + " | flightId=" + flightId);
                            return null;
                        }

                        //в случае успешного бронирования количество свобожных мест уменьшается на 1
                        flightNow.setAvailableSeats(seatsLeft - 1);
                        flightRepository.save(flightNow);
                        Booking b = new Booking();
                        b.setPassenger(customer);
                        b.setFlight(flightNow);
                        b.setBookingTime(LocalDateTime.now());
                        Booking saved = bookingRepository.save(b);

                        System.out.println(String.format("[Бронь создана] user=%s | bookingId=%d | flightId=%d | delay=%dms | seatsLeft=%d",
                                customer.getLogin(),
                                saved.getBookingId(),
                                flightId,
                                delay,
                                flightNow.getAvailableSeats()));

                    } catch (Exception exx) {
                        System.out.println("[ошибка бронирования] Данные пользователя=" + customer.getLogin() + " |ошибка:=" + exx.getMessage());
                    }
                }

                return null;
            }));
        }


        ex.shutdown();
        ex.awaitTermination(10, TimeUnit.SECONDS);

        // итоговая сводка по рейсам и броням в бд

        System.out.println("\nКоличество оставшихся мест для бронирования на каждый рейс:");
        flightRepository.findAll().forEach(f ->
                System.out.println(" - flight id=" + f.getFlightId() + ", число мест:" + f.getAvailableSeats())
        );

        System.out.println("\nСозданные брони");
        bookingRepository.findAll().forEach(b -> {
            CustomerUser passenger = b.getPassenger();
            Flight flight = b.getFlight();
            System.out.println(String.format(" - bookingId=%d | passenger=%s %s | flightId=%d | departure=%s | arrival=%s",
                    b.getBookingId(),
                    passenger.getFirstName(),
                    passenger.getLastName(),
                    flight.getFlightId(),
                    flight.getDepartureAirportCode(),
                    flight.getArrivalAirportCode()
            ));
        });

    }
}
