package Users;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ManagerUserService {

    @Autowired
    private UserRepository userRepository;

    public ManagerUser save(ManagerUser manager) {
        return (ManagerUser) userRepository.save(manager);
    }

    public List<ManagerUser> getAll() {
        return userRepository.findAll()
                .stream()
                .filter(u -> u instanceof ManagerUser)
                .map(u -> (ManagerUser) u)
                .toList();
    }

    public java.util.Optional<ManagerUser> getById(Long id) {
        return userRepository.findById(id)
                .filter(u -> u instanceof ManagerUser)
                .map(u -> (ManagerUser) u);
    }

    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }

    public void createFlightRequest(Long managerId, org.example.Flight flight) {
        getById(managerId).ifPresent(m -> m.createFlightRequest(flight));
    }

    public void deleteFlightRequest(Long managerId, org.example.Flight flight) {
        getById(managerId).ifPresent(m -> m.deleteFlightRequest(flight));
    }

    public List<FlightRequest> getRequests(Long managerId) {
        return getById(managerId).map(ManagerUser::getRequests).orElse(List.of());
    }

    public boolean removeRequest(Long managerId, FlightRequest request) {
        return getById(managerId).map(m -> m.removeRequest(request)).orElse(false);
    }
}
