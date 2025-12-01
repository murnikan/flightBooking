package org.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class FlightService {

    @Autowired
    private FlightRepository flightRepository;
    // операции с рейсом/рейсами
    public Flight saveFlight(Flight flight) {
        return flightRepository.save(flight);
    }


    public List<Flight> getAllFlights() {
        return flightRepository.findAll();
    }

    public Flight getFlightById(Long id) {
        return flightRepository.findById(id).orElse(null);
    }

    public void deleteFlight(Long id) {
        flightRepository.deleteById(id);
    }

// 3 поиска, по городам, дате, пересадке
    public List<Flight> searchFlights(String from, String to, LocalDate date) {
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.plusDays(1).atStartOfDay();
        return flightRepository.findFlightsByCityAndDate(from, to, start, end);
    }

    public boolean hasFlights(String from, String to, LocalDate date) {
        return !searchFlights(from, to, date).isEmpty();
    }

    public List<Flight> searchFlightsByCities(String from, String to) {
        return flightRepository.findFlightsByCity(from, to);
    }

    public List<List<Flight>> searchFlightsWithConnection(String from, String to, LocalDate date) {
        List<Flight> allFlights = flightRepository.findAll();
        List<List<Flight>> connections = new ArrayList<>();

        for (Flight first : allFlights) {
            if (!first.getDepartureAirportCode().equalsIgnoreCase(from)
                    || !first.getDepartureTime().toLocalDate().equals(date)) continue;

            for (Flight second : allFlights) {
                if (!second.getArrivalAirportCode().equalsIgnoreCase(to)) continue;
                if (!first.getArrivalAirportCode().equalsIgnoreCase(second.getDepartureAirportCode())) continue;

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
}