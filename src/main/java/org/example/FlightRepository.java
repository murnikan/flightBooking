package org.example;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FlightRepository {

    private final List<Flight> flights = new ArrayList<>();

    public void addFlight(Flight flight) {
        if (flight != null) {
            flights.add(flight);
        }
    }

    public List<Flight> getAllFlights() {
        return new ArrayList<>(flights);
    }

    public Flight getById(int id) {
        return flights.stream()
                .filter(f -> f.getFlightId() == id)
                .findFirst()
                .orElse(null);
    }
    //методы поиска рейсов
    public List<Flight> findFlights(String from, String to, LocalDate date) {
        return flights.stream()
                .filter(f -> f.getDepartureCity().equalsIgnoreCase(from))
                .filter(f -> f.getArrivalCity().equalsIgnoreCase(to))
                .filter(f -> f.getDepartureTime().toLocalDate().equals(date))
                .collect(Collectors.toList());
    }
    public boolean hasFlights(String from, String to, LocalDate date) {
        return !findFlights(from, to, date).isEmpty();
    }




    public boolean removeFlightById(int id) {
        return flights.removeIf(f -> f.getFlightId() == id);
    }
    public void printAllFlights() {
        if (flights.isEmpty()) {
            System.out.println("нет рейсов");
            return;
        }
        for (Flight f : flights) {
            System.out.println(f.getFlightInfo());
        }
    }
    public void clear() {
        flights.clear();
    }

}
