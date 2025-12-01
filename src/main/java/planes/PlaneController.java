package planes;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/planes")
public class PlaneController {
    // создание, удаление ,чтение(ключ рег номер) обновление, удаление
    @Autowired
    private PlaneService service;

    @GetMapping
    public ResponseEntity<List<Plane>> getAllPlanes() {
        return ResponseEntity.ok(service.getAllPlanes());
    }

    @GetMapping("/{registrationNumber}")
    public ResponseEntity<Plane> getPlaneByRegistrationNumber(@PathVariable String registrationNumber) {
        return service.getPlaneByRegistrationNumber(registrationNumber)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Plane> createPlane(@RequestBody Plane plane) {
        Plane created = service.createPlane(plane);
        return ResponseEntity.created(URI.create("/api/planes/" + created.getRegistrationNumber())).body(created);
    }

    @DeleteMapping("/{registrationNumber}")
    public ResponseEntity<Void> deletePlane(@PathVariable String registrationNumber) {
        service.deletePlane(registrationNumber);
        return ResponseEntity.noContent().build();
    }
}
