package org.example;

import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Flight {
    private Integer flightid;
    private Integer pass_capacity;
    private Airport departAirport;
    private Airport destAirport;

    private final Lock bookingLock = new ReentrantLock();

    public Integer getFlightid() {
        return flightid;
    }

    public void setFlightid(Integer flightid) {
        this.flightid = flightid;
    }

    public Airport getDepartAirport() {
        return departAirport;
    }

    public void setDepartAirport(Airport departAirport) {
        this.departAirport = departAirport;
    }

    public Airport getDestAirport() {
        return destAirport;
    }

    public void setDestAirport(Airport destAirport) {
        this.destAirport = destAirport;
    }

    public Integer getPass_capacity() {
        return pass_capacity;
    }

    public void setPass_capacity(Integer pass_capacity) {
        this.pass_capacity = pass_capacity;
    }

    public String getFlightInfo() {
        return "Номер рейса: " + flightid +
                " | Отправление из: " + (departAirport != null ? departAirport.getAirportCode() : "нет данных") +
                " | Прибывает в: " + (destAirport != null ? destAirport.getAirportCode() : "нет данных") +
                " | Пассажировместимость: " + pass_capacity;
    }
    public String bookSeatSafe(Person person, List<Booking> bookings) {
        bookingLock.lock();
        try {
            long bookedSeats = bookings.stream()
                    .filter(b -> b.getFlight().getFlightid().equals(this.flightid))
                    .count();

            if (bookedSeats >= pass_capacity) {
                return "Ошибка! нет мест для " + person.getName();
            }

            String bookingId = "BKGN" + (bookings.size() + 1);
            Booking booking = new Booking(bookingId, person, this);
            bookings.add(booking);

            return "Бронь создана: " + bookingId + " для " + person.getName();
        } finally {
            bookingLock.unlock();
        }
    }

}
