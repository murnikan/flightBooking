package org.example;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class FlightSearch {

    private final FlightRepository flightRepository;

    public FlightSearch(FlightRepository flightRepository) {
        this.flightRepository = flightRepository;
    }

    // точный поиск (по городам и дате)
    public List<Flight> search(String from, String to, LocalDate date) {
        List<Flight> result = new ArrayList<>();
        for (Flight f : flightRepository.getAllFlights()) {
            if (f.getDepartureCity().equalsIgnoreCase(from) &&
                    f.getArrivalCity().equalsIgnoreCase(to) &&
                    f.getDepartureTime().toLocalDate().equals(date)) {
                result.add(f);
            }
        }
        return result;
    }

    public boolean hasFlights(String from, String to, LocalDate date) {
        for (Flight f : flightRepository.getAllFlights()) {
            if (f.getDepartureCity().equalsIgnoreCase(from) &&
                    f.getArrivalCity().equalsIgnoreCase(to) &&
                    f.getDepartureTime().toLocalDate().equals(date)) {
                return true;
            }
        }
        return false;
    }

    //общий поиск только по городам
    public List<Flight> searchByCities(String from, String to) {
        List<Flight> result = new ArrayList<>();
        for (Flight f : flightRepository.getAllFlights()) {
            if (f.getDepartureCity().equalsIgnoreCase(from) &&
                    f.getArrivalCity().equalsIgnoreCase(to)) {
                result.add(f);
            }
        }
        return result;
    }
    //поиск с пересадкой
    public List<List<Flight>> searchWithConnection(String from, String to, LocalDate date) {
        List<List<Flight>> connections = new ArrayList<>();

        List<Flight> allFlights = flightRepository.getAllFlights();

        for (Flight first : allFlights) {
            if (!first.getDepartureCity().equalsIgnoreCase(from) ||
                    !first.getDepartureTime().toLocalDate().equals(date)) {
                continue;
            }

            for (Flight second : allFlights) {
                if (!second.getArrivalCity().equalsIgnoreCase(to)) continue;

                if (!first.getArrivalCity().equalsIgnoreCase(second.getDepartureCity())) continue;


                if (second.getDepartureTime().isAfter(first.getArrivalTime().plusHours(1))) {
                    List<Flight> route = new ArrayList<>();
                    route.add(first);
                    route.add(second);
                    connections.add(route);
                }
            }
        }

        return connections;
    }

  /*  // вся инфа о рейсах
    public void printFlights(List<Flight> flights) {
        if (flights.isEmpty()) {
            System.out.println("Рейсов не найдено!");
            return;
        }
        for (Flight f : flights) {
            System.out.println(f.getFlightInfo());
        }
    }*/

    // сокращенный вывод рейсов
    public void printFlightsShort(List<Flight> flights) {
        if (flights.isEmpty()) {
            System.out.println("Рейсов не найдено!");
            return;
        }
        for (Flight f : flights) {
            System.out.println(f.getFlightInfoShort());
        }
    }
    //пересадочный вывод
    public void printConnections(List<List<Flight>> connections) {
        if (connections.isEmpty()) {
            System.out.println("Маршрутов с пересадкой не найдено");
            return;
        }

        for (List<Flight> route : connections) {
            System.out.print("Маршрут: ");
            for (Flight f : route) {
                System.out.print(f.getFlightInfoShort() + " > ");
            }
            System.out.println(" ");
        }
    }

}
