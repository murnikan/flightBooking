package org.example;

import Users.CustomerUser;
import Users.CustomerUserRepository;
import exceptions.*;
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


    public synchronized Booking createBooking(Long passengerId, Long flightId)
            throws PassengerNotFoundException,
            FlightNotFoundException,
            NoSeatsAvailableException,
            InvalidBookingDataException,
            BookingConflictException,
            BookingServiceException {

        if (passengerId == null || flightId == null)
            throw new InvalidBookingDataException("Отсутствуют passengerId или flightId");

        CustomerUser passenger = customerUserRepository.findById(passengerId)
                .orElseThrow(() -> new PassengerNotFoundException("Пассажир с id=" + passengerId + " не найден"));

        Flight flight = flightRepository.findById(flightId)
                .orElseThrow(() -> new FlightNotFoundException("Рейс с id=" + flightId + " не найден"));

        if (flight.getAvailableSeats() <= 0)
            throw new NoSeatsAvailableException(" нет свободных мест");
        boolean hasExistingBooking =
                repository.existsByPassengerAndFlight(passenger, flight);

        if (hasExistingBooking)
            throw new BookingConflictException("Пассажир уже имеет бронь на этот рейс");

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

    public List<Booking> getBookingsByPassenger(Long passengerId)
            throws PassengerNotFoundException {

        CustomerUser passenger = customerUserRepository.findById(passengerId)
                .orElseThrow(() -> new PassengerNotFoundException("Пассажир не найден"));

        return repository.findByPassenger(passenger);
    }

    public List<Booking> getBookingsByFlight(Long flightId)
            throws FlightNotFoundException {

        Flight flight = flightRepository.findById(flightId)
                .orElseThrow(() -> new FlightNotFoundException("Рейс не найден"));

        return repository.findByFlight(flight);
    }

    public void deleteBooking(Long id)
            throws BookingNotFoundException {

        Booking booking = repository.findById(id)
                .orElseThrow(() -> new BookingNotFoundException("Бронирование не найдено"));

        Flight flight = booking.getFlight();
        flight.cancelSeat();
        flightRepository.save(flight);

        repository.deleteById(id);
    }

}
