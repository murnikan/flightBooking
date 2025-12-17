package org.example;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.time.Duration;
import java.time.LocalDateTime;

@JsonIgnoreProperties({"flightId", "duration", "flightInfo"})
@Entity
@Table(name = "flights")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class Flight {

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
