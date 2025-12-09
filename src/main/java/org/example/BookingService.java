package org.example;

import Users.CustomerUser;
import Users.CustomerUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BookingService {

    @Autowired
    private BookingRepository repository;

    @Autowired
    private FlightRepository flightRepository;

    @Autowired
    private CustomerUserRepository customerUserRepository;

    // многопоточка, при бронировании уменьшается на 1 место
    public synchronized Booking createBooking(Long passengerId, Long flightId) {

        CustomerUser passenger = customerUserRepository.findById(passengerId)
                .orElseThrow(() -> new IllegalArgumentException("пассажир не найден"));

        Flight flight = flightRepository.findById(flightId)
                .orElseThrow(() -> new IllegalArgumentException("рейс не найден"));

        if (flight.getAvailableSeats() <= 0)
            throw new IllegalStateException("нет мест!");

        flight.bookSeat();
        flightRepository.save(flight);

        Booking booking = new Booking();
        booking.setPassenger(passenger);
        booking.setFlight(flight);

        return repository.save(booking);
    }

    public List<Booking> getAllBookings() {
        return repository.findAll();
    }

    public Optional<Booking> getBookingById(Long id) {
        return repository.findById(id);
    }

    public List<Booking> getBookingsByPassenger(Long passengerId) {
        CustomerUser passenger = customerUserRepository.findById(passengerId)
                .orElseThrow(() -> new IllegalArgumentException("пассажир не найден"));
        return repository.findByPassenger(passenger);
    }

    public List<Booking> getBookingsByFlight(Long flightId) {
        Flight flight = flightRepository.findById(flightId)
                .orElseThrow(() -> new IllegalArgumentException("рейс не найден"));
        return repository.findByFlight(flight);
    }

    public void deleteBooking(Long id) {
        Booking booking = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("бронирование не найдено"));
        Flight flight = booking.getFlight();

        flight.cancelSeat();
        flightRepository.save(flight);

        repository.deleteById(id);
    }
}
