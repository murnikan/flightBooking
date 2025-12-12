package org.example;

import exceptions.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    @Autowired
    private BookingService bookingService;


    @PostMapping
    public ResponseEntity<?> createBooking(@RequestBody Booking booking) {

        Long passengerId = booking.getPassenger() != null ? booking.getPassenger().getId() : null;
        Long flightId = booking.getFlight() != null ? booking.getFlight().getFlightId() : null;

        try {
            Booking created = bookingService.createBooking(passengerId, flightId);
            return ResponseEntity.ok(created);

        } catch (PassengerNotFoundException e) {
            System.out.println("[Ошибка] " + e.getMessage());
            return ResponseEntity.status(404).body("Пассажир не найден");

        } catch (FlightNotFoundException e) {
            System.out.println("[Ошибка] " + e.getMessage());
            return ResponseEntity.status(404).body("Рейс не найден");

        } catch (NoSeatsAvailableException e) {
            System.out.println("[Ошибка] " + e.getMessage());
            return ResponseEntity.status(409).body("Нет свободных мест");

        } catch (BookingConflictException e) {
            System.out.println("[Ошибка] " + e.getMessage());
            return ResponseEntity.status(409).body("Конфликт бронирования, возможно данный пользователь уже имеет бронь на этот рейс");

        } catch (InvalidBookingDataException e) {
            System.out.println("[Ошибка] " + e.getMessage());
            return ResponseEntity.badRequest().body("Неверные данные");

        } catch (BookingServiceException e) {
            System.out.println("[Ошибка] " + e.getMessage());
            return ResponseEntity.status(500).body("Ошибка сервера бронирования");

        }
    }




    @GetMapping
    public List<Booking> getAllBookings() {
        return bookingService.getAllBookings();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getBookingById(@PathVariable Long id) {
        Optional<Booking> booking = bookingService.getBookingById(id);

        if (booking.isPresent()) {
            return ResponseEntity.ok(booking.get());
        } else {
            return ResponseEntity.status(404)
                    .body(Map.of("error", "Бронь не найдена"));
        }
    }


    @GetMapping("/search/passenger")
    public ResponseEntity<?> getBookingsByPassenger(@RequestParam Long passengerId) {
        try {
            return ResponseEntity.ok(bookingService.getBookingsByPassenger(passengerId));
        } catch (PassengerNotFoundException e) {
            return ResponseEntity.status(404).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/search/flight")
    public ResponseEntity<?> getBookingsByFlight(@RequestParam Long flightId) {
        try {
            return ResponseEntity.ok(bookingService.getBookingsByFlight(flightId));
        } catch (FlightNotFoundException e) {
            return ResponseEntity.status(404).body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBooking(@PathVariable Long id) {
        try {
            bookingService.deleteBooking(id);
            return ResponseEntity.noContent().build();

        } catch (BookingNotFoundException e) {
            System.out.println("[Ошибка] " + e.getMessage());
            return ResponseEntity.ok(Map.of("error", "Бронь не найдена"));
        }

    }

}
