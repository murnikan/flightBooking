package org.example;

import airlines.Airline;
import planes.Plane;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Flight {
    private int flightId;
    private Airport departureAirport;
    private Airport arrivalAirport;
    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;
    private Duration duration;
    private Airline airline;
    private Plane plane;
    private int availableSeats;

    public Flight(int flightId, Airport departureAirport, Airport arrivalAirport,
                  LocalDateTime departureTime, LocalDateTime arrivalTime,
                  Airline airline, Plane plane) {
        this.flightId = flightId;
        this.departureAirport = departureAirport;
        this.arrivalAirport = arrivalAirport;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.duration = Duration.between(departureTime, arrivalTime);
        this.airline = airline;
        this.plane = plane;
        this.availableSeats = plane.getCapacity();
    }

    public int getFlightId() { return flightId; }
    public Airport getDepartureAirport() { return departureAirport; }
    public Airport getArrivalAirport() { return arrivalAirport; }
    public String getDepartureCity() { return departureAirport.getCity(); }
    public String getArrivalCity() { return arrivalAirport.getCity(); }
    public String getDepartureCode() { return departureAirport.getCode(); }
    public String getArrivalCode() { return arrivalAirport.getCode(); }
    public LocalDateTime getDepartureTime() { return departureTime; }
    public LocalDateTime getArrivalTime() { return arrivalTime; }
    public Duration getDuration() { return duration; }
    public Airline getAirline() { return airline; }
    public Plane getPlane() { return plane; }

    public synchronized int getAvailableSeats() {
        return availableSeats;
    }

    public synchronized boolean bookSeat() {
        if (availableSeats <= 0) {
            return false;
        }
        availableSeats--;
        return true;
    }

    public String getFlightInfo() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        return String.format(
                "Рейс: %d | %s (%s) -> %s (%s) | Отправление: %s | Прибытие: %s | Длительность: %d ч %d мин | Авиакомпания: %s | Самолёт: %s | Свободных мест: %d",
                flightId,
                departureAirport.getCity(), departureAirport.getCode(),
                arrivalAirport.getCity(), arrivalAirport.getCode(),
                departureTime.format(dtf), arrivalTime.format(dtf),
                duration.toHours(), duration.toMinutesPart(),
                airline.getName(), plane.getFullInfo(), getAvailableSeats()
        );
    }

    public String getFlightInfoShort() {
        return String.format("%d: %s (%s) -> %s (%s) | Отправление: %s",
                flightId,
                departureAirport.getCity(), departureAirport.getCode(),
                arrivalAirport.getCity(), arrivalAirport.getCode(),
                departureTime.format(DateTimeFormatter.ofPattern("dd.MM HH:mm")));
    }
}
