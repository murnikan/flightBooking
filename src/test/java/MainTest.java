import Users.*;
import airlines.Airline;
import airlines.AirlineRepository;
import airports.Airport;
import airports.AirportRepository;
import org.example.*;
import org.springframework.http.ResponseEntity;
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
        classes = {Application.class, org.example.config.TestSecurityConfig.class},
        webEnvironment = SpringBootTest.WebEnvironment.NONE
)
@ActiveProfiles("test")
class MainTest {

    @MockBean private UserRepository userRepository;
    @MockBean private FlightRepository flightRepository;
    @MockBean private BookingRepository bookingRepository;
    @MockBean private CustomerUserRepository customerUserRepository;
    @MockBean private AdminUserRepository adminUserRepository;
    @MockBean private ManagerUserRepository managerUserRepository;
    @MockBean private FlightService flightService;
    @MockBean private ManagerUserService managerUserService;

    @MockBean private AirportRepository airportRepository;
    @MockBean private PlaneRepository planeRepository;
    @MockBean private AirlineRepository airlineRepository;

    @Autowired private AdminUserService adminUserService;

    private AdminUser admin;
    private ManagerUser manager;
    private CustomerUser cust1, cust2, cust3;
    private Flight flight;
    private Plane plane;
    private Airline airline;
    private Airport dep, arr;

    @BeforeEach
    void setUp() {
        admin = new AdminUser("admin1", "pass"); admin.setId(1L);
        manager = new ManagerUser("manager1", "pass"); manager.setId(2L);
        cust1 = new CustomerUser("name1", "pass", "name1", "surname1", 1111); cust1.setId(3L);
        cust2 = new CustomerUser("name2", "pass", "name2", "surname2", 2222); cust2.setId(4L);
        cust3 = new CustomerUser("name3", "pass", "name3", "surname3", 3333); cust3.setId(5L);

        when(userRepository.findByRole(UserRole.ADMIN)).thenReturn(List.of(admin));
        when(userRepository.findByRole(UserRole.MANAGER)).thenReturn(List.of(manager));
        when(userRepository.findByRole(UserRole.CUSTOMER)).thenReturn(List.of(cust1, cust2, cust3));
        when(userRepository.findById(1L)).thenReturn(Optional.of(admin));

        dep = new Airport(); dep.setCode("AAA");
        arr = new Airport(); arr.setCode("BBB");
        when(airportRepository.findAll()).thenReturn(List.of(dep, arr));

        plane = new Plane(); plane.setRegistrationNumber("REG123"); plane.setModel("TestPlane"); plane.setCapacity(2);
        when(planeRepository.findAll()).thenReturn(List.of(plane));

        airline = new Airline(); airline.setIataCode("AL1");
        when(airlineRepository.findAll()).thenReturn(List.of(airline));

        flight = new Flight();
        flight.setFlightId(100L);
        flight.setDepartureAirportCode(dep.getCode());
        flight.setArrivalAirportCode(arr.getCode());
        flight.setDepartureTime(LocalDateTime.now());
        flight.setArrivalTime(LocalDateTime.now().plusHours(2));
        flight.setAirlineCode(airline.getIataCode());
        flight.setPlaneRegistration(plane.getRegistrationNumber());
        flight.setAvailableSeats(plane.getCapacity());

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
        CustomerUser customer = new CustomerUser("testLogin", "pass", "TestName", "TestSurname", 12345);
        when(customerUserRepository.findByLogin("testLogin")).thenReturn(Optional.of(customer));

        Booking booking = new Booking();
        booking.setBookingId(500L);
        booking.setPassenger(customer);
        booking.setFlight(flight);
        booking.setBookingTime(LocalDateTime.now());

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
        Flight savedFlight = flightRepository.save(created);

        CustomerUser cust = new CustomerUser("u", "p", "n", "s", 123);

        Booking bk = new Booking();
        bk.setBookingId(7000L);
        bk.setFlight(savedFlight);
        bk.setPassenger(cust);

        Booking savedBooking = bookingRepository.save(bk);

        Assertions.assertEquals(777L, savedFlight.getFlightId());
        Assertions.assertEquals(7000L, savedBooking.getBookingId());
        Assertions.assertEquals(savedFlight, savedBooking.getFlight());
        Assertions.assertEquals(cust, savedBooking.getPassenger());

        verify(flightRepository, atLeastOnce()).save(any());
        verify(bookingRepository, atLeastOnce()).save(any());
    }

    @Test
    void managerCreatesFlightRequestTest() {
        ManagerUser managerUser = (ManagerUser) userRepository.findByRole(UserRole.MANAGER).get(0);

        Flight requestedFlight = new Flight();
        requestedFlight.setDepartureAirportCode(dep.getCode());
        requestedFlight.setArrivalAirportCode(arr.getCode());
        requestedFlight.setPlaneRegistration(plane.getRegistrationNumber());
        requestedFlight.setAirlineCode(airline.getIataCode());
        requestedFlight.setDepartureTime(LocalDateTime.now().plusDays(1));
        requestedFlight.setArrivalTime(LocalDateTime.now().plusDays(1).plusHours(2));
        requestedFlight.setAvailableSeats(plane.getCapacity());

        FlightRequest flightRequest = new FlightRequest(requestedFlight, FlightRequest.RequestType.CREATE);

        Assertions.assertEquals(2, flightRequest.getFlight().getAvailableSeats());
        Assertions.assertEquals(FlightRequest.RequestType.CREATE, flightRequest.getType());

        Flight saved = flightRepository.save(flightRequest.getFlight());
        Assertions.assertNotNull(saved);
        Assertions.assertEquals(plane.getRegistrationNumber(), saved.getPlaneRegistration());
        Assertions.assertEquals("AAA", saved.getDepartureAirportCode());

        verify(flightRepository, times(1)).save(any(Flight.class));
    }

    @Test
    void testSaveFlightService() {
        Flight flightToSave = new Flight();
        flightToSave.setFlightId(1L);
        flightToSave.setDepartureAirportCode("GOJ");
        flightToSave.setArrivalAirportCode("SVO");
        flightToSave.setDepartureTime(LocalDateTime.of(2025, 11, 1, 9, 0));
        flightToSave.setArrivalTime(LocalDateTime.of(2025, 11, 1, 11, 0));
        flightToSave.setAirlineCode("SU");
        flightToSave.setPlaneRegistration("REG-123");
        flightToSave.setAvailableSeats(150);

        when(flightService.saveFlight(any())).thenReturn(flightToSave);

        Flight created = flightService.saveFlight(flightToSave);
        Assertions.assertNotNull(created);
        Assertions.assertEquals("REG-123", created.getPlaneRegistration());
        Assertions.assertEquals("SU", created.getAirlineCode());

        verify(flightService, times(1)).saveFlight(flightToSave);
    }
    @Autowired
    private AdminUserController adminUserController;

    @MockBean
    private AdminUserService adminUserServiceController;

    private AdminUser controllerAdmin;

    @BeforeEach
    void setUpAdminController() {
        controllerAdmin = new AdminUser("adminCtrl", "passCtrl");
        controllerAdmin.setId(10L);
    }

    @Test
    void createAdminControllerTest_success() {
        when(adminUserServiceController.save(any(AdminUser.class))).thenReturn(controllerAdmin);

        AdminUserDTO dto = new AdminUserDTO();
        dto.setLogin("adminCtrl");
        dto.setPassword("passCtrl");

        ResponseEntity<AdminUser> response = adminUserController.createAdmin(dto);

        Assertions.assertEquals(201, response.getStatusCodeValue());
        Assertions.assertEquals("adminCtrl", response.getBody().getLogin());
        verify(adminUserServiceController, times(1)).save(any(AdminUser.class));
    }

    @Test
    void createAdminControllerTest_badRequest() {
        AdminUserDTO dto = new AdminUserDTO();
        ResponseEntity<AdminUser> response = adminUserController.createAdmin(dto);

        Assertions.assertEquals(400, response.getStatusCodeValue());
        verify(adminUserServiceController, never()).save(any());
    }

    @Test
    void updateAdminControllerTest_success() {
        when(adminUserServiceController.getById(10L)).thenReturn(Optional.of(controllerAdmin));
        when(adminUserServiceController.save(any(AdminUser.class))).thenReturn(controllerAdmin);

        AdminUserDTO dto = new AdminUserDTO();
        dto.setLogin("updatedLogin");
        dto.setPassword("updatedPass");

        ResponseEntity<AdminUser> response = adminUserController.updateAdmin(10L, dto);

        Assertions.assertEquals(200, response.getStatusCodeValue());
        Assertions.assertEquals("updatedLogin", response.getBody().getLogin());
        verify(adminUserServiceController, times(1)).getById(10L);
        verify(adminUserServiceController, times(1)).save(any(AdminUser.class));
    }

    @Test
    void updateAdminControllerTest_notFound() {
        when(adminUserServiceController.getById(20L)).thenReturn(Optional.empty());

        AdminUserDTO dto = new AdminUserDTO();
        ResponseEntity<AdminUser> response = adminUserController.updateAdmin(20L, dto);

        Assertions.assertEquals(404, response.getStatusCodeValue());
        verify(adminUserServiceController, times(1)).getById(20L);
        verify(adminUserServiceController, never()).save(any());
    }

    @Test
    void getAllAdminsControllerTest() {
        when(adminUserServiceController.getAll()).thenReturn(List.of(controllerAdmin));

        ResponseEntity<List<AdminUser>> response = adminUserController.getAllAdmins();

        Assertions.assertEquals(200, response.getStatusCodeValue());
        Assertions.assertEquals(1, response.getBody().size());
        verify(adminUserServiceController, times(1)).getAll();
    }

}
