package Users;

import org.example.Flight;
import org.example.FlightRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminUserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FlightRepository flightRepository;

    public AdminUser getAdminById(Long id) {
        return (AdminUser) userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Admin not found"));
    }

    // обработка заявки менеджера
    public void approveRequest(Long adminId, ManagerUser manager, FlightRequest request) {
        AdminUser admin = getAdminById(adminId);
        admin.approveRequest(manager, request, flightRepository);
    }
    public void rejectRequest(Long adminId, ManagerUser manager, FlightRequest request) {
        AdminUser admin = getAdminById(adminId);
        admin.rejectRequest(manager, request);
    }

    // работа с рейсами
    public void addFlight(Long adminId, Flight flight) {
        AdminUser admin = getAdminById(adminId);
        admin.addFlight(flightRepository, flight);
    }

    public void removeFlight(Long adminId, Flight flight) {
        AdminUser admin = getAdminById(adminId);
        admin.removeFlight(flightRepository, flight);
    }
}
