package Users;

import org.example.Flight;
import org.example.FlightRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AdminUserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FlightRepository flightRepository;

    public AdminUser save(AdminUser admin) {
        return (AdminUser) userRepository.save(admin);
    }

    public Optional<AdminUser> getById(Long id) {
        return userRepository.findById(id).map(user -> (AdminUser) user);
    }

    public List<AdminUser> getAll() {
        return userRepository.findAll()
                .stream()
                .filter(user -> user instanceof AdminUser)
                .map(user -> (AdminUser) user)
                .toList();
    }

    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }


    public void addFlight(Long adminId, Flight flight) {
        AdminUser admin = (AdminUser) userRepository.findById(adminId)
                .orElseThrow(() -> new RuntimeException("admin not found"));

        flightRepository.save(flight);
    }

    public void removeFlight(Long adminId, Flight flight) {
        AdminUser admin = (AdminUser) userRepository.findById(adminId)
                .orElseThrow(() -> new RuntimeException("admin not found"));

        flightRepository.deleteById(flight.getFlightId());
    }
}
