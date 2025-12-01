package org.example;

import planes.*;
import Users.*;
import airlines.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.*;

public class ManualRunner {

    private static LocalDate readDate(Scanner scanner) {
        while (true) {
            System.out.print("введите дату (dd mm yyyy): ");
            String input = scanner.nextLine().trim();
            String[] parts = input.split("\\s+");

            if (parts.length != 3) {
                System.out.println("ошибка! нужно 3 числа через пробел (например: 01 11 2025)");
                continue;
            }

            try {
                int day = Integer.parseInt(parts[0]);
                int month = Integer.parseInt(parts[1]);
                int year = Integer.parseInt(parts[2]);

                if (String.valueOf(year).length() != 4) {
                    System.out.println("ошибка! год должен быть из 4 цифр!");
                    continue;
                }

                return LocalDate.of(year, month, day);

            } catch (NumberFormatException e) {
                System.out.println("ошибка: введите только числа.");
            } catch (Exception e) {
                System.out.println("ошибка: некорректная дата.");
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {

        Scanner scanner = new Scanner(System.in);

        // предзагруженная часть
        Airport nnGOJ = new Airport("Стригино", "GOJ", "Нижний Новгород", true);
        Airport moscowSVO = new Airport("Шереметьево", "SVO", "Москва", true);
        Airport spbLED = new Airport("Пулково", "LED", "Санкт-Петербург", true);
        Airport kazanKZN = new Airport("Казань", "KZN", "Казань", true);
        Airport sochiAER = new Airport("Сочи", "AER", "Сочи", true);

        Plane testPlane = new TestPlane("TP-001"); // 3 места
        Plane superjet = new SukhoiSuperjet("SSJ-100-001");
        Plane boeing = new Boeing737("B737-001");

        Airline aeroflot = new Aeroflot();
        Airline utair = new Utair();
        Airline s7 = new S7();

        FlightRepository flightRepo = new FlightRepository();
        BookingRepository bookingRepo = new BookingRepository();

        AdminUser admin = new AdminUser("A1", "admin1", "pass");

        Flight f1 = new Flight(101, nnGOJ, moscowSVO,
                LocalDateTime.of(2025, 11, 1, 9, 0),
                LocalDateTime.of(2025, 11, 1, 11, 0),
                utair, testPlane);

        Flight f2 = new Flight(102, moscowSVO, spbLED,
                LocalDateTime.of(2025, 11, 1, 13, 0),
                LocalDateTime.of(2025, 11, 1, 14, 30),
                aeroflot, superjet);

        Flight f3 = new Flight(103, spbLED, kazanKZN,
                LocalDateTime.of(2025, 11, 1, 16, 0),
                LocalDateTime.of(2025, 11, 1, 18, 30),
                s7, boeing);

        Flight f4 = new Flight(104, moscowSVO, sochiAER,
                LocalDateTime.of(2025, 11, 2, 10, 0),
                LocalDateTime.of(2025, 11, 2, 13, 0),
                aeroflot, superjet);

        Flight f5 = new Flight(105, kazanKZN, sochiAER,
                LocalDateTime.of(2025, 11, 2, 15, 0),
                LocalDateTime.of(2025, 11, 2, 17, 0),
                s7, boeing);

        Flight[] allFlights = {f1, f2, f3, f4, f5};
        for (Flight f : allFlights) {
            admin.addFlight(flightRepo, f);
            System.out.println("[Админ] Добавлен рейс: " + f.getFlightInfoShort());
        }
        CustomerUser c1 = new CustomerUser("C1", "customer1", "pass", "Иван", "Иванов", 1234);
        CustomerUser c2 = new CustomerUser("C2", "customer2", "pass", "Петр", "Петров", 2345);

        System.out.println("\n[Инициализация] Создаем начальные брони на рейс " + f1.getFlightInfoShort());
        System.out.println(c1.bookFlight(f1, bookingRepo));
        System.out.println(c2.bookFlight(f1, bookingRepo));

        CustomerUser manualCustomer = new CustomerUser("C3", "manual", "pass", "Клиент", "Ручной", 3456);

        FlightSearch flightSearch = new FlightSearch(flightRepo);

        // логика работы пользователя
        boolean exit = false;
        while (!exit) {
            System.out.println("\nВыберите действие:");
            System.out.println("1. Найти рейсы");
            System.out.println("2. Забронировать рейс");
            System.out.println("3. Информация об авиакомпании");
            System.out.println("4. Выход");

            String choice = scanner.nextLine();
            switch (choice) {
                case "1" -> {
                    boolean subExit = false;
                    while (!subExit) {
                        System.out.println("\nВыберите тип поиска:");
                        System.out.println("1. Прямые рейсы (по дате)");
                        System.out.println("2. Рейсы с пересадкой (по дате)");
                        System.out.println("3) Рейсы по направлению (без даты)");
                        System.out.println("4. Все рейсы");
                        System.out.println("5. Назад");

                        String subChoice = scanner.nextLine();
                        switch (subChoice) {
                            case "1" -> {
                                System.out.print("введите город отправления: ");
                                String from = scanner.nextLine();
                                System.out.print("введите город прибытия: ");
                                String to = scanner.nextLine();

                                System.out.println("введите дату вылета:");
                                LocalDate date = readDate(scanner);

                                List<Flight> results = flightSearch.search(from, to, date);
                                System.out.println("найдено рейсов: " + results.size());
                                flightSearch.printFlightsShort(results);
                            }
                            case "2" -> {
                                System.out.print("введите город отправления: ");
                                String from = scanner.nextLine();
                                System.out.print("введите город прибытия: ");
                                String to = scanner.nextLine();

                                System.out.println("введите дату вылета:");
                                LocalDate date = readDate(scanner);

                                List<List<Flight>> results = flightSearch.searchWithConnection(from, to, date);
                                System.out.println("найдено маршрутов с пересадкой: " + results.size());
                                for (List<Flight> route : results) {
                                    System.out.println("маршрут(ы):");
                                    for (Flight f : route) {
                                        System.out.println("  " + f.getFlightInfoShort());
                                    }
                                }
                            }
                            case "3" -> { // рейсы по направлению без даты
                                System.out.print("введите город отправления: ");
                                String from = scanner.nextLine();
                                System.out.print("введите город прибытия: ");
                                String to = scanner.nextLine();

                                List<Flight> results = flightSearch.searchByCities(from, to);
                                System.out.println("найдено рейсов: " + results.size());
                                flightSearch.printFlightsShort(results);
                            }
                            case "4" -> flightRepo.printAllFlights();
                            case "5" -> subExit = true;
                            default -> System.out.println("неправильный ввод!");
                        }
                    }
                }
                case "2" -> {
                    System.out.println("доступные рейсы:");
                    flightRepo.printAllFlights();
                    System.out.print("введите ID рейса для брони: ");
                    try {
                        int flightId = Integer.parseInt(scanner.nextLine());
                        Flight selectedFlight = flightRepo.getById(flightId);
                        if (selectedFlight != null) {
                            String res = manualCustomer.bookFlight(selectedFlight, bookingRepo);
                            System.out.println(res);
                        } else {
                            System.out.println(" ошибка! рейс с таким ID не найден");
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("ошибка! неправильный ввод ID");
                    }
                }
                case "3" -> {
                    System.out.print("введите название авиакомпании: ");
                    String name = scanner.nextLine();
                    boolean found = false;
                    for (Flight f : flightRepo.getAllFlights()) {
                        if (f.getAirline().getName().equalsIgnoreCase(name)) {

                            System.out.println(f.getAirline().getFullInfo());
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        System.out.println("авиакомпания не найдена");
                        break;
                    }
                    System.out.println("\nрейсы этой компании:");
                    for (Flight f : flightRepo.getAllFlights()) {
                        if (f.getAirline().getName().equalsIgnoreCase(name)) {
                            System.out.println(f.getFlightInfoShort());
                        }
                    }
                }
                case "4" -> exit = true;
                default -> System.out.println("ошибка ввода! ");
            }
        }

        System.out.println("конец");
        scanner.close();
    }
}
