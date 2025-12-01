import Users.*;
import airlines.Airline;
import airlines.AirlineRepository;
import airports.Airport;
import airports.AirportRepository;
import org.example.*;
import planes.Plane;
import planes.PlaneRepository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest(
        classes = Application.class,
        webEnvironment = SpringBootTest.WebEnvironment.NONE
)
@ActiveProfiles("test")
class MainTest {

    @MockBean
    private UserRepository userRepository;
    @MockBean
    private FlightRepository flightRepository;
    @MockBean
    private BookingRepository bookingRepository;
    @MockBean
    private CustomerUserRepository customerUserRepository;
    @MockBean
    private AdminUserRepository adminUserRepository;
    @MockBean
    private ManagerUserRepository managerUserRepository;
    @MockBean
    private FlightService flightService;
    @MockBean
    private ManagerUserService managerUserService;

    @MockBean
    private AirportRepository airportRepository;
    @MockBean
    private PlaneRepository planeRepository;
    @MockBean
    private AirlineRepository airlineRepository;

    @Autowired
    private AdminUserService adminUserService;

    @BeforeEach
    void setUp() {
        // моки для тестов
        AdminUser admin = new AdminUser("admin1", "pass");
        ManagerUser manager = new ManagerUser("manager1", "pass");
        CustomerUser cust1 = new CustomerUser("name1", "pass", "name1", "surname1", 1111);
        CustomerUser cust2 = new CustomerUser("name2", "pass", "name2", "surname2", 2222);
        CustomerUser cust3 = new CustomerUser("name3", "pass", "name3", "surname3", 3333);

        when(userRepository.findByRole(UserRole.ADMIN)).thenReturn(List.of(admin));
        when(userRepository.findByRole(UserRole.MANAGER)).thenReturn(List.of(manager));
        when(userRepository.findByRole(UserRole.CUSTOMER)).thenReturn(List.of(cust1, cust2, cust3));

        Airport dep = new Airport();
        dep.setCode("AAA");
        Airport arr = new Airport();
        arr.setCode("BBB");
        when(airportRepository.findAll()).thenReturn(List.of(dep, arr));

        Plane plane = new Plane();
        plane.setRegistrationNumber("REG123");
        plane.setModel("TestPlane");
        plane.setCapacity(2);
        when(planeRepository.findAll()).thenReturn(List.of(plane));

        Airline airline = new Airline();
        airline.setIataCode("AL1");
        when(airlineRepository.findAll()).thenReturn(List.of(airline));

        Flight flight = new Flight();
        flight.setFlightId(100L);
        flight.setDepartureAirportCode("AAA");
        flight.setArrivalAirportCode("BBB");
        flight.setDepartureTime(LocalDateTime.now());
        flight.setArrivalTime(LocalDateTime.now().plusHours(2));
        flight.setAirlineCode("AL1");
        flight.setPlaneRegistration("REG123");
        flight.setAvailableSeats(100);

        when(flightRepository.findAll()).thenReturn(List.of(flight));
        when(flightRepository.save(any(Flight.class))).thenAnswer(i -> i.getArguments()[0]);
        when(bookingRepository.save(any())).thenAnswer(i -> i.getArguments()[0]);
    }


    @Test
    void flightCreationTest() {
        Flight newFlight = new Flight();
        newFlight.setFlightId(999L);
        newFlight.setDepartureAirportCode("AAA");
        newFlight.setArrivalAirportCode("BBB");
        newFlight.setAirlineCode("AL1");
        newFlight.setPlaneRegistration("REG123");
        newFlight.setAvailableSeats(50);

        when(flightRepository.save(any())).thenReturn(newFlight);

        Flight saved = flightRepository.save(newFlight);

        Assertions.assertNotNull(saved);
        Assertions.assertEquals(999L, saved.getFlightId());
        Assertions.assertEquals("AAA", saved.getDepartureAirportCode());

        verify(flightRepository).save(any(Flight.class));
    }

    @Test
    void userRepositoryRolesTest() {
        List<User> admins = userRepository.findByRole(UserRole.ADMIN);
        List<User> customers = userRepository.findByRole(UserRole.CUSTOMER);

        Assertions.assertEquals(1, admins.size());
        Assertions.assertEquals(3, customers.size());

        verify(userRepository).findByRole(UserRole.ADMIN);
        verify(userRepository).findByRole(UserRole.CUSTOMER);
    }

    @Test
    void bookingCreationTest() {
        Flight flight = flightRepository.findAll().get(0);

        CustomerUser customer = new CustomerUser(
                "testLogin",
                "pass",
                "TestName",
                "TestSurname",
                12345
        );

        when(customerUserRepository.findByLogin("testLogin"))
                .thenReturn(Optional.of(customer));

        Booking booking = new Booking();
        booking.setBookingId(500L);
        booking.setPassenger(customer);
        booking.setFlight(flight);
        booking.setBookingTime(LocalDateTime.now());

        when(bookingRepository.save(any())).thenReturn(booking);

        Booking saved = bookingRepository.save(booking);

        Assertions.assertNotNull(saved);
        Assertions.assertEquals(500L, saved.getBookingId());
        Assertions.assertEquals("TestName", saved.getPassenger().getFirstName());
        Assertions.assertEquals("AAA", saved.getFlight().getDepartureAirportCode());

        verify(bookingRepository, atLeastOnce()).save(any());
    }

    @Test
    void adminCreatesFlightCustomerBooksTest() {
        Flight created = new Flight();
        created.setFlightId(777L);
        created.setAvailableSeats(120);

        when(flightRepository.save(any())).thenReturn(created);

        Flight savedFlight = flightRepository.save(created);

        CustomerUser cust = new CustomerUser("u", "p", "n", "s", 123);

        Booking bk = new Booking();
        bk.setBookingId(7000L);
        bk.setFlight(savedFlight);
        bk.setPassenger(cust);

        when(bookingRepository.save(any())).thenReturn(bk);

        Booking savedBooking = bookingRepository.save(bk);

        Assertions.assertEquals(777L, savedFlight.getFlightId());
        Assertions.assertEquals(7000L, savedBooking.getBookingId());
        Assertions.assertEquals(savedFlight, savedBooking.getFlight());
        Assertions.assertEquals(cust, savedBooking.getPassenger());

        verify(flightRepository, atLeastOnce()).save(any());
        verify(bookingRepository, atLeastOnce()).save(any());
    }

    @Test
    void adminAddsFlightTest() {
        AdminUser admin = new AdminUser("admin1", "pass");
        admin.setId(1L);

        Flight flight = new Flight();
        flight.setFlightId(123L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(admin));
        when(flightRepository.save(any(Flight.class))).thenAnswer(i -> i.getArguments()[0]);
        adminUserService.addFlight(1L, flight);
        verify(flightRepository, times(1)).save(flight);
    }

    @Test
    void adminRemovesFlightTest() {
        AdminUser admin = new AdminUser("admin1", "pass");
        admin.setId(1L);

        Flight flight = new Flight();
        flight.setFlightId(456L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(admin));
        doNothing().when(flightRepository).deleteById(flight.getFlightId());

        adminUserService.removeFlight(1L, flight);
        verify(flightRepository, times(1)).deleteById(flight.getFlightId());
    }

    @Test
    void testSaveFlightService() {
        Flight flight = new Flight();
        flight.setFlightId(1L);
        flight.setDepartureAirportCode("GOJ");
        flight.setArrivalAirportCode("SVO");
        flight.setDepartureTime(LocalDateTime.of(2025, 11, 1, 9, 0));
        flight.setArrivalTime(LocalDateTime.of(2025, 11, 1, 11, 0));
        flight.setAirlineCode("SU");
        flight.setPlaneRegistration("REG-123");
        flight.setAvailableSeats(150);

        when(flightService.saveFlight(any(Flight.class))).thenReturn(flight);

        Flight created = flightService.saveFlight(flight);

        Assertions.assertNotNull(created);
        Assertions.assertEquals("REG-123", created.getPlaneRegistration());
        Assertions.assertEquals("SU", created.getAirlineCode());

        verify(flightService, times(1)).saveFlight(flight);
    }
    @Test
    void managerCreatesFlightRequestTest() {
     //менеджер реализует права
        ManagerUser manager = (ManagerUser) userRepository.findByRole(UserRole.MANAGER).get(0);
        List<Airport> airports = airportRepository.findAll();
        Airport departure = airports.get(0);
        Airport arrival = airports.get(1);

        Plane testPlane = planeRepository.findAll().get(0);
        Airline airline = airlineRepository.findAll().get(0);

        Flight requestedFlight = new Flight();
        requestedFlight.setDepartureAirportCode(departure.getCode());
        requestedFlight.setArrivalAirportCode(arrival.getCode());
        requestedFlight.setPlaneRegistration(testPlane.getRegistrationNumber());
        requestedFlight.setAirlineCode(airline.getIataCode());
        requestedFlight.setDepartureTime(LocalDateTime.now().plusDays(1));
        requestedFlight.setArrivalTime(LocalDateTime.now().plusDays(1).plusHours(2));
        requestedFlight.setAvailableSeats(testPlane.getCapacity());

        Users.FlightRequest flightRequest = new Users.FlightRequest(requestedFlight, Users.FlightRequest.RequestType.CREATE);

        // проверка данных
        Assertions.assertEquals(2, flightRequest.getFlight().getAvailableSeats());
        Assertions.assertEquals(Users.FlightRequest.RequestType.CREATE, flightRequest.getType());
        Flight saved = flightRepository.save(flightRequest.getFlight());

        Assertions.assertNotNull(saved);
        Assertions.assertEquals(testPlane.getRegistrationNumber(), saved.getPlaneRegistration());
        Assertions.assertEquals("AAA", saved.getDepartureAirportCode());

        verify(flightRepository, times(1)).save(any(Flight.class));
    }

}
