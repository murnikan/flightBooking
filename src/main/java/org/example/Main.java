package org.example;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class Main {

    private static final List<Booking> bookings = new ArrayList<>();

    public static String createBooking(Person person, Flight flight) {
        if (person == null || flight == null) return "ошибка создания брони \n";

        synchronized (bookings) {
            long bookedSeats = bookings.stream()
                    .filter(b -> b.getFlight().getFlightid().equals(flight.getFlightid()))
                    .count();

            if (bookedSeats >= flight.getPass_capacity()) {
                return "ошибка! нет мест для " + person.getName();
            }

            String bookingId = "BKGN" + (bookings.size() + 1);
            Booking booking = new Booking(bookingId, person, flight);
            bookings.add(booking);
            return "бронь создана: " + bookingId + " для " + person.getName();
        }
    }
    //служебные функции для тестов
    public static void clearBookings() {
        synchronized (bookings) {
            bookings.clear();
        }
    }

    public static void printAllBookings() {
        if (bookings.isEmpty()) {
            System.out.println("нет бронирований");
            return;
        }
        for (Booking b : bookings) {
            System.out.println(b.getBookingId() + " -> " + b.getPassenger().getName()
                    + " " + b.getPassenger().getSurname()
                    + " | данные о рейсе: " + b.getFlight().getFlightInfo());
        }
    }

    public static Booking getBookingByPassengerName(String name) {
        synchronized (bookings) {
            return bookings.stream()
                    .filter(b -> b.getPassenger().getName().equals(name))
                    .findFirst()
                    .orElse(null);
        }
    }

    public static boolean deleteBookingByPassengerName(String name) {
        synchronized (bookings) {
            return bookings.removeIf(b -> b.getPassenger().getName().equals(name));
        }
    }
    //
     // осн логика
     //

    public static void main(String[] args) throws InterruptedException {
        System.out.println("====================");
//создание  аэропортов
        Airport sheremetyevo = new Airport();
        sheremetyevo.setAirportName("Шереметьево");
        sheremetyevo.setAirportCode("SVO");
        sheremetyevo.setAvaibleFlight(true);

        Airport strigino = new Airport();
        strigino.setAirportName("Стригино");
        strigino.setAirportCode("GOJ");
        strigino.setAvaibleFlight(true);
//2 разных самолета
        Plane boeing = new Plane();
        boeing.setModel("Boeing 737");
        boeing.setPlaneCode("B737-800");
        boeing.setMaxPassengersAmount(2); // специально 2 места, чтобы одна из попыток брони была отклонена

        Plane airbus = new Plane();
        airbus.setModel("Airbus A320");
        airbus.setPlaneCode("A320");
        airbus.setMaxPassengersAmount(180);
//по рейсу на каждый самолет
        Flight flight1 = new Flight();
        flight1.setFlightid(101);
        flight1.setDepartAirport(sheremetyevo);
        flight1.setDestAirport(strigino);
        flight1.setPass_capacity(boeing.getMaxPassengersAmount());

        Flight flight2 = new Flight();
        flight2.setFlightid(202);
        flight2.setDepartAirport(strigino);
        flight2.setDestAirport(sheremetyevo);
        flight2.setPass_capacity(airbus.getMaxPassengersAmount());
        //первые 3 на один рейс, все остальные на другой
        Person p1 = new Person(); p1.setName("name1"); p1.setSurname("surname1"); p1.setPassportData(123456);
        Person p2 = new Person(); p2.setName("name2"); p2.setSurname("surname2"); p2.setPassportData(654321);
        Person p3 = new Person(); p3.setName("name3"); p3.setSurname("surname3"); p3.setPassportData(987654);
        Person p4 = new Person(); p4.setName("name4"); p4.setSurname("surname4"); p4.setPassportData(112233);
        Person p5 = new Person(); p5.setName("name5"); p5.setSurname("surname5"); p5.setPassportData(445566);
        Person p6 = new Person(); p6.setName("name6"); p6.setSurname("surname6"); p6.setPassportData(778899);
        Person p7 = new Person(); p7.setName("name7"); p7.setSurname("surname7"); p7.setPassportData(334455);

        ExecutorService executor = Executors.newFixedThreadPool(4);
        List<Future<String>> futures = new ArrayList<>();

        Person[] passengers = {p1,p2,p3,p4,p5,p6,p7};
        Flight[] flights = {flight1, flight1, flight1, flight2, flight2, flight2, flight2};

        for (int i = 0; i < passengers.length; i++) {
            Person person = passengers[i];
            Flight flight = flights[i];

            futures.add(executor.submit(() -> {
                int delay = ThreadLocalRandom.current().nextInt(0, 1000);
                Thread.sleep(delay);
                return createBooking(person, flight) + " (задержка поступления: " + delay + " ms)";// задержка для потоков 0-1с чтобы добавить вид "симуляции" работы
            }));
        }

        System.out.println("\nНачало работы\n");
        for (Future<String> f : futures) {
            try {
                System.out.println(f.get());
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }

        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.SECONDS);

        System.out.println("\nВся информация о созданных бронированиях: \n");
        printAllBookings();
    }
}
