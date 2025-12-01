package Users;

import org.example.Flight;
import org.example.FlightRepository;
import org.example.FlightSearch;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

// функционал менеджера - сделать заявку на добавление/удаление рейса(ее нужно рассмотреть админу)
public class ManagerUser extends User {

    private final List<FlightRequest> requests = new ArrayList<>();

    public ManagerUser(String id, String login, String password) {
        super(id, login, password, UserRole.MANAGER);
    }

    public void createFlightRequest(Flight flight) {
        FlightRequest request = new FlightRequest(flight, FlightRequest.RequestType.CREATE);
        requests.add(request);
    }
    public void deleteFlightRequest(Flight flight) {
        FlightRequest request = new FlightRequest(flight, FlightRequest.RequestType.DELETE);
        requests.add(request);
    }

    public List<FlightRequest> getRequests() {
        return new ArrayList<>(requests);
    }

    public List<Flight> searchFlights(FlightSearch search, String from, String to, LocalDate date) {
        return super.searchFlights(search, from, to, date);
    }
    /*  public void clearRequests() {
        requests.clear();
    }*/

    public boolean removeRequest(FlightRequest req) {
        return requests.remove(req);
    }

}
