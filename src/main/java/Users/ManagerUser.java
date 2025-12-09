package Users;

import jakarta.persistence.*;
import lombok.*;
import org.example.Flight;

import java.util.ArrayList;
import java.util.List;

@Entity
@DiscriminatorValue("MANAGER")
@Getter
@Setter
@NoArgsConstructor
public class ManagerUser extends User {

    @Transient
    private final List<FlightRequest> requests = new ArrayList<>();

    public ManagerUser(String login, String password) {
        super(login, password, UserRole.MANAGER);
    }

    public void createFlightRequest(Flight flight) {
        requests.add(new FlightRequest(flight, FlightRequest.RequestType.CREATE));
    }

    public void deleteFlightRequest(Flight flight) {
        requests.add(new FlightRequest(flight, FlightRequest.RequestType.DELETE));
    }

    public List<FlightRequest> getRequests() {
        return new ArrayList<>(requests);
    }

    public boolean removeRequest(FlightRequest req) {
        return requests.remove(req);
    }
}
