package airports;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/airports")
public class AirportController {

    @Autowired
    private AirportService service;
    @GetMapping
    public ResponseEntity<List<Airport>> getAllAirports() {
        return ResponseEntity.ok(service.getAllAirports());
    }


    @GetMapping("/{code}")
    public ResponseEntity<Airport> getAirportByCode(@PathVariable String code) {
        Optional<Airport> airport = service.getAirportByCode(code);
        return airport.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
    @PostMapping
    public ResponseEntity<Airport> createAirport(@RequestBody Airport airport) {
        Airport created = service.createAirport(airport);
        return ResponseEntity.created(URI.create("/api/airports/" + created.getCode())).body(created);
    }
    @PutMapping("/{code}")
    public ResponseEntity<Airport> updateAirport(@PathVariable String code, @RequestBody Airport airport) {
        Optional<Airport> existing = service.getAirportByCode(code);
        if (existing.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        airport.setCode(existing.get().getCode());
        Airport updated = service.updateAirport(airport);
        return ResponseEntity.ok(updated);
    }
    @DeleteMapping("/{code}")
    public ResponseEntity<Void> deleteAirport(@PathVariable String code) {
        service.deleteAirport(code);
        return ResponseEntity.noContent().build();
    }
}
