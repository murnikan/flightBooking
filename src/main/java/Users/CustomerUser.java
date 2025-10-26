package Users;

import org.example.Flight;
import org.example.FlightSearch;
import org.example.Booking;
import org.example.BookingRepository;

import java.time.LocalDate;
import java.util.List;
//в первом реквесте был класс person, сейчас вся осн логика из него слилась с customeruser
public class CustomerUser extends User {
    private String firstName;
    private String lastName;
    private int passportNumber;

    public CustomerUser(String id, String login, String password,
                        String firstName, String lastName, int passportNumber) {
        super(id, login, password, UserRole.CUSTOMER);
        this.firstName = firstName;
        this.lastName = lastName;
        this.passportNumber = passportNumber;
    }

    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public int getPassportNumber() { return passportNumber; }

    public List<Flight> searchFlights(FlightSearch search, String from, String to, LocalDate date) {
        return search.search(from, to, date);
    }
    public String bookFlight(Flight flight, BookingRepository bookingRepo) {
        Booking booking = bookingRepo.addBooking(this, flight);

        if (booking == null) {
            return "ошибка бронирования: мест нет на рейс "
                    + flight.getDepartureCity() + " -> " + flight.getArrivalCity();
        }

        return booking.getBookingReceipt();
    }

}
