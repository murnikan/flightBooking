package Users;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ManagerUserService {

    @Autowired
    private UserRepository userRepository;
    public ManagerUser getManagerById(Long id) {
        return (ManagerUser) userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Manager not found"));
    }

    // заяки (создание, получение, уаление)
    public void createFlightRequest(Long managerId, org.example.Flight flight) {
        ManagerUser manager = getManagerById(managerId);
        manager.createFlightRequest(flight);
    }
    public void deleteFlightRequest(Long managerId, org.example.Flight flight) {
        ManagerUser manager = getManagerById(managerId);
        manager.deleteFlightRequest(flight);
    }
    public List<FlightRequest> getRequests(Long managerId) {
        ManagerUser manager = getManagerById(managerId);
        return manager.getRequests();
    }
    public boolean removeRequest(Long managerId, FlightRequest request) {
        ManagerUser manager = getManagerById(managerId);
        return manager.removeRequest(request);
    }
}
