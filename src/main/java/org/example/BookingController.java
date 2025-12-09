package org.example;

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

    // cоздание брони
    @PostMapping
    public ResponseEntity<?> createBooking(@RequestBody Booking booking) {
        Long passengerId = booking.getPassenger().getId();
        Long flightId = booking.getFlight().getFlightId();
//куча ошибок и того что может вылететь(нет мест, не те данные, другое) + начальный материал для лр3
        try {
            Booking created = bookingService.createBooking(passengerId, flightId);
            System.out.println("[бронь успешна] passengerId=" + passengerId + " flightId=" + flightId);
            return ResponseEntity.ok(created);

        } catch (IllegalStateException e) {
            System.out.println("[бронь отклонена ] passengerId=" + passengerId + " flightId=" + flightId + " | " + e.getMessage());
            return ResponseEntity.status(409)
                    .body(Map.of("error", e.getMessage()));

        } catch (IllegalArgumentException e) {

            System.out.println("[ошибка бронирования] passengerId=" + passengerId + " flightId=" + flightId + " | " + e.getMessage());
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));

        } catch (Exception e) {

            System.out.println("[BOOK EXCEPTION] passengerId=" + passengerId + " flightId=" + flightId + " | " + e.getMessage());
            return ResponseEntity.status(500)
                    .body(Map.of("error", "Internal server error", "details", e.getMessage()));
        }
    }

    // получение броней
    @GetMapping
    public List<Booking> getAllBookings() {
        return bookingService.getAllBookings();
    }
    @GetMapping("/{id}")
    public ResponseEntity<Booking> getBookingById(@PathVariable Long id) {
        Optional<Booking> booking = bookingService.getBookingById(id);
        return booking
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }


    @GetMapping("/search/passenger")
    public List<Booking> getBookingsByPassenger(@RequestParam Long passengerId) {
        return bookingService.getBookingsByPassenger(passengerId);
    }
    @GetMapping("/search/flight")
    public List<Booking> getBookingsByFlight(@RequestParam Long flightId) {
        return bookingService.getBookingsByFlight(flightId);
    }
    // удаление брони
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBooking(@PathVariable Long id) {
        bookingService.deleteBooking(id);
        System.out.println("[BOOKING DELETED] bookingId=" + id);
        return ResponseEntity.noContent().build();
    }
}
