package org.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/flights")
public class FlightController {
    // создание,чтение(+по ключу) обновление, удаление, поиски
    @Autowired
    private FlightService flightService;
    @PostMapping
    public ResponseEntity<Flight> createFlight(@RequestBody Flight flight) {
        Flight created = flightService.saveFlight(flight);
        return ResponseEntity.ok(created);
    }

    @GetMapping
    public ResponseEntity<List<Flight>> getAllFlights() {
        return ResponseEntity.ok(flightService.getAllFlights());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Flight> getFlightById(@PathVariable Long id) {
        Flight flight = flightService.getFlightById(id);
        if (flight == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(flight);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFlight(@PathVariable Long id) {
        flightService.deleteFlight(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<Flight>> searchFlights(
            @RequestParam String from,
            @RequestParam String to,
            @RequestParam(required = false) String date // yyyy-MM-dd
    ) {
        if (date != null) {
            LocalDate localDate = LocalDate.parse(date);
            List<Flight> flights = flightService.searchFlights(from, to, localDate);
            return ResponseEntity.ok(flights);
        } else {
            List<Flight> flights = flightService.searchFlightsByCities(from, to);
            return ResponseEntity.ok(flights);
        }
    }
    @GetMapping("/search/connection")
    public ResponseEntity<List<List<Flight>>> searchFlightsWithConnection(
            @RequestParam String from,
            @RequestParam String to,
            @RequestParam String date
    ) {
        LocalDate localDate = LocalDate.parse(date);
        List<List<Flight>> connections = flightService.searchFlightsWithConnection(from, to, localDate);
        return ResponseEntity.ok(connections);
    }
}
