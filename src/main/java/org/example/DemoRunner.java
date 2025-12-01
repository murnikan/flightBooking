package org.example;

import planes.*;
import Users.*;
import airlines.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.*;

public class DemoRunner {

    public static void main(String[] args) throws InterruptedException {

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

        // нач. данные необходимые для работы
        Airport nnGOJ = new Airport("Стригино", "GOJ", "Нижний Новгород", true);
        Airport moscowSVO = new Airport("Шереметьево", "SVO", "Москва", true);
        Airport spbLED = new Airport("Пулково", "LED", "Санкт-Петербург", true);

        Plane testPlane = new TestPlane("TP-001"); // тестовый самолет с 3 местами
        Plane superjet = new SukhoiSuperjet("SSJ-100-001");
        Plane boeing = new Boeing737("B737-001");

        Airline aeroflot = new Aeroflot();
        Airline utair = new Utair();

        FlightRepository flightRepo = new FlightRepository();
        BookingRepository bookingRepo = new BookingRepository();

        ManagerUser manager = new ManagerUser("M1", "manager1", "pass");
        AdminUser admin = new AdminUser("A1", "admin1", "pass");

        // менеджер реализует права(все действия менеджера по добавлению/исключению рейса требуют подтверждения админа
        Flight flightRequestFlight = new Flight(101, nnGOJ, moscowSVO,
                LocalDateTime.of(2025, 11, 1, 9, 0),
                LocalDateTime.of(2025, 11, 1, 11, 0),
                utair, testPlane);
        manager.createFlightRequest(flightRequestFlight);
        System.out.println("[Менеджер] создана заявка на рейс: " + flightRequestFlight.getFlightInfo());

        // админ реализует права
        Flight f1 = new Flight(201, moscowSVO, spbLED,
                LocalDateTime.of(2025, 11, 1, 16, 0),
                LocalDateTime.of(2025, 11, 1, 17, 30),
                aeroflot, superjet);
        Flight f2 = new Flight(202, moscowSVO, spbLED,
                LocalDateTime.of(2025, 11, 2, 10, 0),
                LocalDateTime.of(2025, 11, 2, 12, 0),
                aeroflot, boeing);

        admin.addFlight(flightRepo, f1);
        System.out.println("[Админ] добавлен рейс: " + f1.getFlightInfo());
        admin.addFlight(flightRepo, f2);
        System.out.println("[Админ]добавлен рейс: " + f2.getFlightInfo());
        FlightRequest request = manager.getRequests().get(0);
        admin.approveRequest(flightRepo, request);
        System.out.println("[Админ] подтверждена заявка менеджера на рейс: " + flightRequestFlight.getFlightInfo());

        System.out.println("\nВсе рейсы:\n");
        flightRepo.printAllFlights();
        CustomerUser c1 = new CustomerUser("C1", "name1", "pass", "name1", "surname1", 1111);
        CustomerUser c2 = new CustomerUser("C2", "name2", "pass", "name2", "surname2", 2222);
        CustomerUser c3 = new CustomerUser("C3", "name3", "pass", "name3", "surname3", 3333);
        CustomerUser c4 = new CustomerUser("C4", "name4", "pass", "name4", "surname4", 4444);
        CustomerUser[] customers = {c1, c2, c3, c4};
        Flight[] flightsToBook = {flightRequestFlight, flightRequestFlight, flightRequestFlight, f1};

        // 3 потока пытаюстся создать бронь
        ExecutorService executor = Executors.newFixedThreadPool(3);
        List<Future<String>> futures = new ArrayList<>();

        for (int i = 0; i < customers.length; i++) {
            CustomerUser customer = customers[i];
            Flight flight = flightsToBook[i];

            futures.add(executor.submit(() -> {
                int delay = ThreadLocalRandom.current().nextInt(0, 500);
                Thread.sleep(delay);
                String result = customer.bookFlight(flight, bookingRepo);
                return result + " (задержка: " + delay + " ms)";//служебное поле, показывает задержку потока
            }));
        }

        System.out.println("\nНачало работы потоков бронирования\n");
        for (Future<String> f : futures) {
            try {
                System.out.println(f.get());
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }

        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.SECONDS);

        bookingRepo.printAllBookingsByFlights(flightRepo.getAllFlights());
    }
}
