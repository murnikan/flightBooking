package org.example;

import exceptions.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/bookings")
@Tag(name = "бронирования")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private Users.CustomerUserRepository customerUserRepository;


    @Operation(summary = "создать бронь")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "бронь успешно создана",
                    content = @Content(schema = @Schema(implementation = Booking.class))),
            @ApiResponse(responseCode = "400", description = "неверные данные"),
            @ApiResponse(responseCode = "403", description = "нет прав!"),
            @ApiResponse(responseCode = "404", description = "указанный пассажир или рейс отсутствует"),
            @ApiResponse(responseCode = "409", description = "бронь не удалась, нет мест или уже существует бронь с указанными данными " +
                    "на этот рейс!"),
            @ApiResponse(responseCode = "500", description = "ошибка сервера")
    })
    @PostMapping
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('ADMIN')")
    public ResponseEntity<?> createBooking(
            @RequestBody CreateBookingRequest request
    ) {

        if (request == null || request.getPassengerId() == null || request.getFlightId() == null) {
            return ResponseEntity.badRequest().body("passengerId и flightId обязательны");
        }

        try {
            if (!isAdmin()) {
                Long currentPassengerId = customerUserRepository
                        .findByLogin(getCurrentUsername())
                        .map(Users.CustomerUser::getId)
                        .orElse(null);

                if (!request.getPassengerId().equals(currentPassengerId)) {
                    return ResponseEntity.status(403).body("нет прав! доступ запрещен");
                }
            }

            Booking created = bookingService.createBooking(
                    request.getPassengerId(),
                    request.getFlightId()
            );

            return ResponseEntity.ok(created);

        } catch (PassengerNotFoundException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        } catch (FlightNotFoundException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        } catch (NoSeatsAvailableException | BookingConflictException e) {
            return ResponseEntity.status(409).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("ошибка сервера");
        }
    }


    @Operation(
            summary = "получить все существующие бронирования",
            description = "админ получит все бронирования, человек с ролью CUSTOMER — только свои")
    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('CUSTOMER')")
    public ResponseEntity<List<Booking>> getAllBookings() {

        if (isAdmin()) {
            return ResponseEntity.ok(bookingService.getAllBookings());
        }

        Long passengerId = customerUserRepository.findByLogin(getCurrentUsername())
                .map(Users.CustomerUser::getId)
                .orElse(null);

        if (passengerId == null) {
            return ResponseEntity.ok(List.of());
        }

        try {
            return ResponseEntity.ok(
                    bookingService.getBookingsByPassenger(passengerId)
            );
        } catch (PassengerNotFoundException e) {
            return ResponseEntity.ok(List.of());
        }
    }

    @Operation(summary = "получить бронь по ID")
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('CUSTOMER')")
    public ResponseEntity<?> getBookingById(
            @Parameter(description = "ID брони")
            @PathVariable Long id
    ) {

        Optional<Booking> booking = bookingService.getBookingById(id);

        if (booking.isEmpty()) {
            return ResponseEntity.status(404).body(" ошибка! бронь не найдена");
        }

        if (!isAdmin()
                && !booking.get().getPassenger().getLogin().equals(getCurrentUsername())) {
            return ResponseEntity.status(403).body("нет прав!");
        }

        return ResponseEntity.ok(booking.get());
    }
    @Operation(summary = "удалить бронь")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('CUSTOMER')")
    public ResponseEntity<?> deleteBooking(@PathVariable Long id) {

        Optional<Booking> booking = bookingService.getBookingById(id);

        if (booking.isEmpty()) {
            return ResponseEntity.status(404).body("бронь не найдена");
        }

        if (!isAdmin()
                && !booking.get().getPassenger().getLogin().equals(getCurrentUsername())) {
            return ResponseEntity.status(403).body("нет прав!");
        }

        try {
            bookingService.deleteBooking(id);
            return ResponseEntity.noContent().build();
        } catch (BookingNotFoundException e) {
            return ResponseEntity.status(404).body("бронь не найдена");
        }
    }

    private String getCurrentUsername() {
        Object principal = SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();

        return (principal instanceof UserDetails)
                ? ((UserDetails) principal).getUsername()
                : principal.toString();
    }

    private boolean isAdmin() {
        return SecurityContextHolder.getContext()
                .getAuthentication()
                .getAuthorities()
                .stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }
}
