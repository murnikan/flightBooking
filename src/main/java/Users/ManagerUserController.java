package Users;

import org.example.Flight;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/managers")
public class ManagerUserController {

    @Autowired
    private ManagerUserService managerService;
    @PostMapping("/{id}/requests/add")
    public ResponseEntity<Void> createFlightRequest(@PathVariable Long id, @RequestBody Flight flight) {
        managerService.createFlightRequest(id, flight);
        return ResponseEntity.ok().build();
    }
    @PostMapping("/{id}/requests/delete")
    public ResponseEntity<Void> deleteFlightRequest(@PathVariable Long id, @RequestBody Flight flight) {
        managerService.deleteFlightRequest(id, flight);
        return ResponseEntity.ok().build();
    }
    @GetMapping("/{id}/requests")
    public ResponseEntity<List<FlightRequest>> getRequests(@PathVariable Long id) {
        return ResponseEntity.ok(managerService.getRequests(id));
    }
    @DeleteMapping("/{id}/requests")
    public ResponseEntity<Void> removeRequest(@PathVariable Long id, @RequestBody FlightRequest request) {
        managerService.removeRequest(id, request);
        return ResponseEntity.noContent().build();
    }
}
