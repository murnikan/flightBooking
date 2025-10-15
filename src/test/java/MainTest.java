package org.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

public class MainTest {

    private Flight flight1;
    private Person p1, p2, p3;

    @BeforeEach
    void setup() {
        // очистка списка броней
        Main.clearBookings();

        // тестовые данные, те же что и в мэйне
        Airport a1 = new Airport();
        a1.setAirportName("Шереметьево");
        a1.setAirportCode("SVO");
        a1.setAvaibleFlight(true);

        Airport a2 = new Airport();
        a2.setAirportName("Стригино");
        a2.setAirportCode("GOJ");
        a2.setAvaibleFlight(true);

        Plane plane = new Plane();
        plane.setModel("Boeing 737");
        plane.setPlaneCode("B737");
        plane.setMaxPassengersAmount(2);

        flight1 = new Flight();
        flight1.setFlightid(1);
        flight1.setDepartAirport(a1);
        flight1.setDestAirport(a2);
        flight1.setPass_capacity(plane.getMaxPassengersAmount());

        p1 = new Person(); p1.setName("name1"); p1.setSurname("surname1"); p1.setPassportData(111);
        p2 = new Person(); p2.setName("name2"); p2.setSurname("surname2"); p2.setPassportData(222);
        p3 = new Person(); p3.setName("name3"); p3.setSurname("surname3"); p3.setPassportData(333);
    }

    //тест создания брони
    @Test
    void testSuccessfulBooking() {
        String result = Main.createBooking(p1, flight1);
        assertTrue(result.contains("бронь создана"), "бронь не была создана");
    }

    //тест "пустых" данных
    @Test
    void testBookingNullInputs() {
        String r1 = Main.createBooking(null, flight1);
        String r2 = Main.createBooking(p1, null);

        assertTrue(r1.contains("ошибка"), "пассажир не указан");
        assertTrue(r2.contains("ошибка"), "рейс не указан");
    }

    //тест переполнения рейса
    @Test
    void testBookingCapacityLimit() {
        Main.createBooking(p1, flight1);
        Main.createBooking(p2, flight1);
        String result = Main.createBooking(p3, flight1);

        assertTrue(result.contains("нет мест"), "бронирование при заполненном рейсе");
    }

    //тест многопоточного бронирования
    @Test
    void testConcurrentBookingsNotExceedCapacity() throws InterruptedException {
        ExecutorService exec = Executors.newFixedThreadPool(3);
        List<Callable<String>> tasks = List.of(
                () -> Main.createBooking(p1, flight1),
                () -> Main.createBooking(p2, flight1),
                () -> Main.createBooking(p3, flight1)
        );

        List<Future<String>> results = exec.invokeAll(tasks);
        exec.shutdown();

        long created = results.stream().filter(f -> {
            try {
                return f.get().contains("бронь создана");
            } catch (Exception e) {
                return false;
            }
        }).count();

        assertEquals(2, created, "переполнение, броней больше чем мест");
    }

    //тест поиска по имени
    @Test
    void testSearchBookingByPassengerName() {
        Main.createBooking(p1, flight1);
        Main.createBooking(p2, flight1);

        Booking found = Main.getBookingByPassengerName("name1");
        assertNotNull(found, "бронь не существует");
        assertEquals(p1.getPassportData(), found.getPassenger().getPassportData());
    }

    //тест удаления брони
    @Test
    void testDeleteBooking() {
        Main.createBooking(p1, flight1);
        Main.createBooking(p2, flight1);

        boolean deleted = Main.deleteBookingByPassengerName("name1");
        assertTrue(deleted, "бронь для name1 не удалена");

        Booking found = Main.getBookingByPassengerName("name1");
        assertNull(found, "бронь name1 не null");
    }
}
