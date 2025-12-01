package org.example;

import Users.CustomerUser;

import java.util.ArrayList;
import java.util.List;

public class BookingRepository {

    private final List<Booking> bookings = new ArrayList<>();
//попытка создания брони, если не удалось возвращает Null
    public Booking addBooking(CustomerUser passenger, Flight flight) {
        if (passenger == null || flight == null) {
            throw new IllegalArgumentException("пассажир или рейс не могут быть null");
        }

        synchronized (flight) {
            if (flight.getAvailableSeats() <= 0) {
                return null;
            }
            flight.bookSeat();

            long bookedSeatsCount = bookings.stream()
                    .filter(b -> b.getFlight().getFlightId() == flight.getFlightId())
                    .count();
            String bookingId = "BKGN" + flight.getFlightId() + "-" + (bookedSeatsCount + 1);

            Booking booking = new Booking(bookingId, passenger, flight);
            bookings.add(booking);
            return booking;
        }
    }


    public synchronized List<Booking> getAllBookings() {
        return new ArrayList<>(bookings);
    }

    public synchronized Booking getBookingByPassengerName(String firstName, String lastName) {
        return bookings.stream()
                .filter(b -> b.getPassenger().getFirstName().equals(firstName)
                        && b.getPassenger().getLastName().equals(lastName))
                .findFirst()
                .orElse(null);
    }

    public synchronized boolean deleteBookingByPassengerName(String firstName, String lastName) {
        return bookings.removeIf(b -> b.getPassenger().getFirstName().equals(firstName)
                && b.getPassenger().getLastName().equals(lastName));
    }

    public synchronized void printAllBookings() {
        if (bookings.isEmpty()) {
            System.out.println("нет бронирований");
            return;
        }
                 for (Booking b : bookings) {
                     System.out.println(b.getBookingReceipt());
                  }
    }

    public synchronized void clear() {
        bookings.clear();
    }
   /* public void printBookingsByFlight(Flight flight) {
        System.out.println("РЕЙС: " + flight.getFlightInfoShort());
        bookings.stream()
                .filter(b -> b.getFlight().getFlightId() == flight.getFlightId())
                .forEach(b -> System.out.println(b.getBookingReceiptShort()));
    }*/
    public void printAllBookingsByFlights(List<Flight> flights) {
         for (Flight flight : flights) {
             System.out.println("\nВсе бронирования по рейсу с ID " + flight.getFlightId() + ":\n");
            bookings.stream()
                      .filter(b -> b.getFlight().getFlightId() == flight.getFlightId())
                    .forEach(b -> System.out.println(b.getBookingReceiptShort()));
         }
    }

}
