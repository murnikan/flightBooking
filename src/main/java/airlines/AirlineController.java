package airlines;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/airlines")
public class AirlineController {

    @Autowired
    private AirlineService airlineService;

    // создание,чтение(+по ключу) обновление, удаление, поиски
    @PostMapping
    public ResponseEntity<Airline> createAirline(@RequestBody Airline airline) {
        Airline created = airlineService.saveAirline(airline);
        return ResponseEntity.created(URI.create("/api/airlines/" + created.getIataCode())).body(created);
    }

    @GetMapping
    public ResponseEntity<List<Airline>> getAllAirlines() {
        return ResponseEntity.ok(airlineService.getAllAirlines());
    }

    @GetMapping("/{iataCode}")
    public ResponseEntity<Airline> getAirlineByIataCode(@PathVariable String iataCode) {
        Optional<Airline> airline = airlineService.getAirlineByIataCode(iataCode);
        return airline.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{iataCode}")
    public ResponseEntity<Airline> updateAirline(@PathVariable String iataCode, @RequestBody Airline airline) {
        Optional<Airline> existing = airlineService.getAirlineByIataCode(iataCode);
        if (existing.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        airline.setIataCode(existing.get().getIataCode());
        Airline updated = airlineService.saveAirline(airline);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{iataCode}")
    public ResponseEntity<Void> deleteAirline(@PathVariable String iataCode) {
        airlineService.deleteAirline(iataCode);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search/name")
    public ResponseEntity<Airline> getAirlineByName(@RequestParam String name) {
        Optional<Airline> airline = airlineService.getAirlineByName(name);
        return airline.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/search/country")
    public ResponseEntity<List<Airline>> getAirlinesByCountry(@RequestParam String country) {
        List<Airline> airlines = airlineService.getAirlinesByCountry(country);
        return ResponseEntity.ok(airlines);
    }
}
