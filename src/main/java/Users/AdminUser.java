package Users;

import org.example.Flight;
import org.example.FlightRepository;
import org.example.FlightSearch;

import java.time.LocalDate;
import java.util.List;

public class AdminUser extends User {
    // админ имеет права на создание/удаление рейсов, а так же на рассмотрение заявок менеджера
    public AdminUser(String id, String login, String password) {
        super(id, login, password, UserRole.ADMIN);
    }

    // полный функционал админа
    public void addFlight(FlightRepository repo, Flight flight) {
        repo.addFlight(flight);
    }

    public void removeFlight(FlightRepository repo, Flight flight) {
        repo.removeFlightById(flight.getFlightId());
    }

    //работа с результатами деятельности менеджеров
    public void approveRequest(FlightRepository repo, FlightRequest request) {
        if (request.getType() == FlightRequest.RequestType.CREATE) {
            repo.addFlight(request.getFlight());
        } else if (request.getType() == FlightRequest.RequestType.DELETE) {
            repo.removeFlightById(request.getFlight().getFlightId());
        }
    }

    public void rejectRequest(ManagerUser manager, FlightRequest request) {
        manager.removeRequest(request);
    }



    public List<Flight> searchFlights(FlightSearch search, String from, String to, LocalDate date) {
        return super.searchFlights(search, from, to, date);
    }
}
