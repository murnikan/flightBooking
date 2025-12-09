package Users;

import org.example.Flight;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admins")
public class AdminUserController {

    @Autowired
    private AdminUserService adminService;

    // работа с реквестами менеджеров
    @PostMapping("/{adminId}/approve")
    public ResponseEntity<Void> approveRequest(
            @PathVariable Long adminId,
            @RequestBody ApproveRequestDto dto
    ) {
        adminService.approveRequest(adminId, dto.getManager(), dto.getRequest());
        return ResponseEntity.ok().build();
    }
    @PostMapping("/{adminId}/reject")
    public ResponseEntity<Void> rejectRequest(
            @PathVariable Long adminId,
            @RequestBody ApproveRequestDto dto
    ) {
        adminService.rejectRequest(adminId, dto.getManager(), dto.getRequest());
        return ResponseEntity.ok().build();
    }

    // рейсы
    @PostMapping("/{adminId}/flights")
    public ResponseEntity<Void> addFlight(@PathVariable Long adminId, @RequestBody Flight flight) {
        adminService.addFlight(adminId, flight);
        return ResponseEntity.ok().build();
    }
    @DeleteMapping("/{adminId}/flights")
    public ResponseEntity<Void> removeFlight(@PathVariable Long adminId, @RequestBody Flight flight) {
        adminService.removeFlight(adminId, flight);
        return ResponseEntity.noContent().build();
    }

    // DTO для передачи ManagerUser + FlightRequest
    public static class ApproveRequestDto {
        private ManagerUser manager;
        private FlightRequest request;

        public ManagerUser getManager() { return manager; }
        public void setManager(ManagerUser manager) { this.manager = manager; }

        public FlightRequest getRequest() { return request; }
        public void setRequest(FlightRequest request) { this.request = request; }
    }
}
