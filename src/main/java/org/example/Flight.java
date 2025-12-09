package org.example;

import jakarta.persistence.*;
import lombok.*;
import java.time.Duration;
import java.time.LocalDateTime;

@Entity
@Table(name = "flights")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class Flight {
//сущность Flight атрибуты: код аэропорта отправления/прибытия, время отправления/прибытия, код авиалинии, рег номер самолета и число свободных мест
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long flightId;

    @Column(name = "departure_airport_code", nullable = false)
    private String departureAirportCode;

    @Column(name = "arrival_airport_code", nullable = false)
    private String arrivalAirportCode;

    @Column(name = "departure_time", nullable = false)
    private LocalDateTime departureTime;

    @Column(name = "arrival_time", nullable = false)
    private LocalDateTime arrivalTime;

    @Column(name = "airline_code", nullable = false)
    private String airlineCode;

    @Column(name = "plane_registration", nullable = false)
    private String planeRegistration;

    @Column(name = "available_seats", nullable = false)
    private int availableSeats;

    @Transient
    private Duration duration;

    @PostLoad
    @PostPersist
    @PostUpdate
    private void calculateDuration() {
        if (departureTime != null && arrivalTime != null) {
            duration = Duration.between(departureTime, arrivalTime);
        }
    }

    public synchronized boolean bookSeat() {
        if (availableSeats <= 0) return false;
        availableSeats--;
        return true;
    }

    public synchronized void cancelSeat() {
        availableSeats++;
    }

    public String getFlightInfo() {
        return String.format("Flight %d: %s -> %s, departure=%s, arrival=%s",
                flightId,
                departureAirportCode,
                arrivalAirportCode,
                departureTime != null ? departureTime.toString() : "N/A",
                arrivalTime != null ? arrivalTime.toString() : "N/A");
    }
}
