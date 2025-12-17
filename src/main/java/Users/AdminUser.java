package Users;

import jakarta.persistence.*;
import lombok.*;
import org.example.Flight;
import org.example.FlightRepository;

@Entity
@DiscriminatorValue("ADMIN")
@Getter
@Setter
@NoArgsConstructor
public class AdminUser extends User {

    public AdminUser(String login, String password) {
        super(login, password, UserRole.ADMIN);
    }

    @Column(nullable = true)
    private String firstName;

    @Column(nullable = true)
    private String lastName;

    @Column(nullable = true)
    private Integer passportNumber;

    public void addFlight(FlightRepository repo, Flight flight) {
        if (flight != null) repo.save(flight);
    }

    public void removeFlight(FlightRepository repo, Flight flight) {
        if (flight != null && flight.getFlightId() != null) {
            repo.deleteById(flight.getFlightId());
        }
    }

    public void approveRequest(ManagerUser manager, FlightRequest request, FlightRepository repo) {
        if (manager == null || request == null) return;
        Flight flight = request.getFlight();
        if (request.getType() == FlightRequest.RequestType.CREATE) {
            addFlight(repo, flight);
        } else if (request.getType() == FlightRequest.RequestType.DELETE) {
            removeFlight(repo, flight);
        }
        manager.removeRequest(request);
    }

    public void rejectRequest(ManagerUser manager, FlightRequest request) {
        if (manager != null && request != null) {
            manager.removeRequest(request);
        }
    }
}
