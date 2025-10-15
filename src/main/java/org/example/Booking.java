package org.example;

public class Booking {
    private String bookingId;
    private Person passenger;
    private Flight flight;

    public Booking(String bookingId, Person passenger, Flight flight) {
        this.bookingId = bookingId;
        this.passenger = passenger;
        this.flight = flight;
    }

    public String getBookingId() {
        return bookingId;
    }

    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
    }

    public Person getPassenger() {
        return passenger;
    }

    public void setPassenger(Person passenger) {
        this.passenger = passenger;
    }

    public Flight getFlight() {
        return flight;
    }

    public void setFlight(Flight flight) {
        this.flight = flight;
    }
}
