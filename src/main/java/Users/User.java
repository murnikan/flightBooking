package Users;

import org.example.Flight;
import org.example.FlightSearch;

import java.time.LocalDate;
import java.util.List;

public abstract class User {
    private String id;
    private String login;
    private String password;
    private UserRole role;

    public User(String id, String login, String password, UserRole role) {
        this.id = id;
        this.login = login;
        this.password = password;
        this.role = role;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getId() {
        return id;
    }
    public String getLogin() {
        return login;
    }
    public String getPassword() {
        return password;
    }
    public UserRole getRole() {

        return role;
    }

    public List<Flight> searchFlights(FlightSearch search, String from, String to, LocalDate date) {
        return search.search(from, to, date);
    }
}
