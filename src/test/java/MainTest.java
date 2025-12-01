import org.example.*;
import Users.*;
import planes.*;
import airlines.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

public class MainTest {

    private FlightRepository flightRepo;
    private FlightSearch flightSearch;
    private ManagerUser manager;
    private AdminUser admin;
    private Airline airline;
    private Plane plane;

    private Airport cityA;
    private Airport cityB;
    private Airport cityC;
    private Airport cityD;
    private Airport cityE;
    private Airport cityF;
    private Airport cityG;
    private Airport cityH;
    private Airport cityI;
    private Airport cityJ;
    private Airport cityK;
    private Airport cityL;
    private Airport cityM;
    private Airport cityN;
    private Airport cityO;
    private Airport cityP;
    private Airport cityQ;
    private Airport cityR;
    private Airport cityX;
    private Airport cityY;//безумие это повторение одного и того же действия раз за разом...

    @BeforeEach
    void setup() {
        flightRepo = new FlightRepository();
        flightRepo.clear(); // очищаем репозиторий перед каждым тестом
        flightSearch = new FlightSearch(flightRepo);

        manager = new ManagerUser("m1", "LogM", "pass");
        admin = new AdminUser("a1", "LogA", "pass");

        airline = new TestAirline();
        plane = new TestPlane("REG-123");

        // создаём аэропорты
        cityA = new Airport("Аэропорт A", "AAA", "CityA", true);
        cityB = new Airport("Аэропорт B", "BBB", "CityB", true);
        cityC = new Airport("Аэропорт C", "CCC", "CityC", true);
        cityD = new Airport("Аэропорт D", "DDD", "CityD", true);
        cityE = new Airport("Аэропорт E", "EEE", "CityE", true);
        cityF = new Airport("Аэропорт F", "FFF", "CityF", true);
        cityG = new Airport("Аэропорт G", "GGG", "CityG", true);
        cityH = new Airport("Аэропорт H", "HHH", "CityH", true);
        cityI = new Airport("Аэропорт I", "III", "CityI", true);
        cityJ = new Airport("Аэропорт J", "JJJ", "CityJ", true);
        cityK = new Airport("Аэропорт K", "KKK", "CityK", true);
        cityL = new Airport("Аэропорт L", "LLL", "CityL", true);
        cityM = new Airport("Аэропорт M", "MMM", "CityM", true);
        cityN = new Airport("Аэропорт N", "NNN", "CityN", true);
        cityO = new Airport("Аэропорт O", "OOO", "CityO", true);
        cityP = new Airport("Аэропорт P", "PPP", "CityP", true);
        cityQ = new Airport("Аэропорт Q", "QQQ", "CityQ", true);
        cityR = new Airport("Аэропорт R", "RRR", "CityR", true);
        cityX = new Airport("Аэропорт X", "XXX", "CityX", true);
        cityY = new Airport("Аэропорт Y", "YYY", "CityY", true);
    }

    @Test
    void testManagerCreatesRequests() {//создание заявок на обработку менеджером
        System.out.println("тест1");

        Flight f1 = new Flight(1, cityA, cityB,
                LocalDateTime.of(2025, 10, 27, 10, 5),
                LocalDateTime.of(2025, 10, 27, 12, 5),
                airline, plane);

        Flight f2 = new Flight(2, cityC, cityD,
                LocalDateTime.of(2025, 10, 28, 10, 5),
                LocalDateTime.of(2025, 10, 28, 12, 5),
                airline, plane);

        manager.createFlightRequest(f1);
        System.out.println("менеджер создал заявку на рейс: " + f1.getFlightInfoShort());

        manager.createFlightRequest(f2);
        System.out.println("менеджер создал заявку на рейс: " + f2.getFlightInfoShort());

        assertEquals(2, manager.getRequests().size());
    }

    @Test
    void testAdminApprovesRequest() {// принятие заявки
        System.out.println("тест2");

        Flight f = new Flight(3, cityE, cityF,
                LocalDateTime.of(2025, 10, 29, 10, 0),
                LocalDateTime.of(2025, 10, 29, 12, 0),
                airline, plane);

        manager.createFlightRequest(f);
        System.out.println("менеджер создал заявку на рейс: " + f.getFlightInfoShort());

        FlightRequest req = manager.getRequests().get(0);
        admin.approveRequest(flightRepo, req);
        System.out.println("админ одобрил заявку на рейс: " + f.getFlightInfoShort());

        assertTrue(flightRepo.getAllFlights().contains(f));
    }

    @Test// не принятие заявки
    void testAdminRejectsRequest() {
        System.out.println("тест3");

        manager.getRequests().clear();
        flightRepo.clear();

        Flight f = new Flight(4, cityG, cityH,
                LocalDateTime.of(2025, 10, 30, 10, 0),
                LocalDateTime.of(2025, 10, 30, 12, 0),
                airline, new TestPlane("TP-01"));

        manager.createFlightRequest(f);
        System.out.println("менеджер создал заявку на рейс: " + f.getFlightInfoShort());

        FlightRequest req = manager.getRequests().get(0);
        admin.rejectRequest(manager, req);
        System.out.println("админ отклонил заявку на рейс: " + f.getFlightInfoShort());

        assertFalse(manager.getRequests().contains(req));
    }

    @Test//создание рейса админом
    void testAdminAddsFlight() {
        System.out.println("тест4");

        Flight f = new Flight(5, cityI, cityJ,
                LocalDateTime.of(2025, 11, 1, 10, 0),
                LocalDateTime.of(2025, 11, 1, 12, 0),
                airline, plane);

        admin.addFlight(flightRepo, f);
        System.out.println("админ добавил рейс: " + f.getFlightInfoShort());

        assertTrue(flightRepo.getAllFlights().contains(f));
    }

    @Test
    void testAdminSearchesSpecificFlight() {//тест поиска
        System.out.println("тест5");

        Flight f = new Flight(6, cityK, cityL,
                LocalDateTime.of(2025, 11, 2, 10, 0),
                LocalDateTime.of(2025, 11, 2, 12, 0),
                airline, plane);

        manager.createFlightRequest(f);
        FlightRequest req = manager.getRequests().get(0);
        admin.approveRequest(flightRepo, req);
        System.out.println("админ одобрил заявку на рейс: " + f.getFlightInfoShort());

        List<Flight> found = admin.searchFlights(flightSearch, "CityK", "CityL", LocalDate.of(2025, 11, 2));
        System.out.println("админ выполнил поиск рейсов и нашел: " + found.size() + " рейса(ов)");
        for (Flight fl : found) {
            System.out.println("найден рейс: " + fl.getFlightInfoShort());
        }

        assertEquals(1, found.size());
    }

    @Test
    void testCustomerSearchByCities() {//второй тест поиска(по направлению)
        System.out.println("тест6");

        Flight flight1 = new Flight(10, cityX, cityY,
                LocalDateTime.of(2025, 12, 1, 10, 0),
                LocalDateTime.of(2025, 12, 1, 12, 0),
                airline, plane);

        Flight flight2 = new Flight(11, cityX, cityY,
                LocalDateTime.of(2025, 12, 2, 14, 0),
                LocalDateTime.of(2025, 12, 2, 16, 0),
                airline, plane);

        admin.addFlight(flightRepo, flight1);
        System.out.println("админ добавил рейс: " + flight1.getFlightInfoShort());
        admin.addFlight(flightRepo, flight2);
        System.out.println("админ добавил рейс: " + flight2.getFlightInfoShort());

        CustomerUser customer = new CustomerUser("C3", "Log3", "pass", "name3", "surname3", 3333);
        List<Flight> results = flightSearch.searchByCities("CityX", "CityY");
        System.out.println("результат общего поиска : " + results.size() + " рейса(ов)");
        for (Flight fl : results) {
            System.out.println("найден рейс: " + fl.getFlightInfoShort());
        }

        assertEquals(2, results.size());
    }

    @Test
    void testCustomerBooking() {//создание брони
        System.out.println("тест7");

        Flight f = new Flight(7, cityM, cityN,
                LocalDateTime.of(2025, 11, 3, 10, 0),
                LocalDateTime.of(2025, 11, 3, 12, 0),
                airline, plane);

        admin.addFlight(flightRepo, f);
        System.out.println("админ добавил рейс: " + f.getFlightInfoShort());

        CustomerUser customer = new CustomerUser("C1", "Log1", "pass", "name1", "surname1", 1111);
        String result = customer.bookFlight(f, new BookingRepository());
        System.out.println("бронь на рейс создана: " + f.getFlightInfoShort() + " Результат: " + result);

        assertTrue(result.contains("name1"));
    }

    @Test
    void testMultipleCustomersBooking() throws InterruptedException {//тест многопоточного бронирования
        System.out.println("тест8");

        Flight f = new Flight(9, cityQ, cityR,
                LocalDateTime.of(2025, 11, 5, 10, 0),
                LocalDateTime.of(2025, 11, 5, 12, 0),
                airline, plane);

        admin.addFlight(flightRepo, f);
        System.out.println("админ добавил рейс: " + f.getFlightInfoShort());

        CustomerUser[] customers = {
                new CustomerUser("C1", "Log1", "pass", "name1", "surname1", 1111),
                new CustomerUser("C2", "Log2", "pass", "name2", "surname2", 2222)
        };

        BookingRepository bookingRepo = new BookingRepository();
        ExecutorService executor = Executors.newFixedThreadPool(customers.length);
        List<Future<String>> futures = new ArrayList<>();

        for (CustomerUser c : customers) {
            futures.add(executor.submit(() -> c.bookFlight(f, bookingRepo)));
        }

        for (Future<String> fut : futures) {
            try {
                System.out.println("результат бронирования: " + fut.get());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.SECONDS);

        int seatsBooked = f.getPlane().getCapacity() - f.getAvailableSeats();
        System.out.println("всего забронировано мест: " + seatsBooked);
        assertEquals(2, seatsBooked);
    }

    @Test
    void testRemoveFlight() {//удаление рейса
        System.out.println("тест9");

        Flight f = new Flight(8, cityO, cityP,
                LocalDateTime.of(2025, 11, 4, 10, 0),
                LocalDateTime.of(2025, 11, 4, 12, 0),
                airline, plane);

        admin.addFlight(flightRepo, f);
        System.out.println("админ добавил рейс: " + f.getFlightInfoShort());

        admin.removeFlight(flightRepo, f);
        System.out.println("админ удалил рейс: " + f.getFlightInfoShort());

        assertFalse(flightRepo.getAllFlights().contains(f));
    }

    @Test
    void testCustomerSearchWithConnection() { //тест поиска #3
        System.out.println("test10");

        // 4 рейса, три из них участвуют в поиске пересадки, один "лишний" для проверки метода поиска
        Flight flight1 = new Flight(20, cityA, cityB,
                LocalDateTime.of(2025, 12, 5, 8, 0),
                LocalDateTime.of(2025, 12, 5, 10, 0),
                airline, plane);

        Flight flight2 = new Flight(21, cityB, cityC,
                LocalDateTime.of(2025, 12, 5, 12, 0),
                LocalDateTime.of(2025, 12, 5, 14, 0),
                airline, plane);

        Flight flight3 = new Flight(22, cityA, cityC,
                LocalDateTime.of(2025, 12, 5, 9, 0),
                LocalDateTime.of(2025, 12, 5, 13, 0),
                airline, plane);

        Flight flight4= new Flight(2, cityB, cityA,
                LocalDateTime.of(2025, 12, 5, 13, 0),
                LocalDateTime.of(2025, 12, 5, 15, 0),
                airline, plane);
             admin.addFlight(flightRepo, flight1);
        System.out.println("Admin добавил рейс: " + flight1.getFlightInfoShort());
             admin.addFlight(flightRepo, flight2);
        System.out.println("Admin добавил рейс: " + flight2.getFlightInfoShort());
             admin.addFlight(flightRepo, flight3);
        System.out.println("Admin добавил рейс: " + flight3.getFlightInfoShort());
         admin.addFlight(flightRepo, flight4);
        System.out.println("Admin добавил рейс: " + flight4.getFlightInfoShort());

          CustomerUser customer = new CustomerUser("C4", "Log4", "pass", "name4", "surname4", 4444);
        List<List<Flight>> connections = flightSearch.searchWithConnection("CityA", "CityC", LocalDate.of(2025, 12, 5));

        System.out.println(" Найдено маршрутов с пересадкой: " + connections.size());
        for (List<Flight> route : connections) {
            System.out.print("Маршрут: ");
            for (Flight f : route) {
                System.out.print(f.getFlightInfoShort() + " -> ");
            }
            System.out.println("конец");
        }
        assertTrue(connections.size() >= 1);
    }
}
