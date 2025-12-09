package org.example;

import Users.CustomerUser;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity
@Table(name = "bookings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "booking_id")
    private Long bookingId;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)

    @JoinColumn(name = "passenger_id")
    @JsonIgnoreProperties({"bookings", "hibernateLazyInitializer", "handler"})
    private CustomerUser passenger;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)

    @JoinColumn(name = "flight_id")
    @JsonIgnoreProperties({"bookings", "hibernateLazyInitializer", "handler"})
    private Flight flight;

    @Column(name = "booking_time", nullable = false)
    private LocalDateTime bookingTime;

    @Column(name = "is_paid", nullable = false)
    private boolean isPaid = false;

    @PrePersist
    private void prePersist() {
        if (bookingTime == null) {
            bookingTime = LocalDateTime.now();
        }
    }

    public void pay() {
        this.isPaid = true;
    }

    public String getBookingReceipt() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        String airlineCode = flight.getAirlineCode() != null ? flight.getAirlineCode() : "N/A";
        String planeReg = flight.getPlaneRegistration() != null ? flight.getPlaneRegistration() : "N/A";

        Duration duration = flight.getDuration() != null ? flight.getDuration() : Duration.ZERO;

        return String.format(
                "====== Бронь подтверждена ======\n" +
                        "ID брони: %d\n" +
                        "Пассажир: %s %s\n" +
                        "Паспорт: %d\n" +
                        "Дата брони: %s\n" +
                        "Рейс: %s -> %s\n" +
                        "Отправление: %s | Прибытие: %s\n" +
                        "Длительность: %d ч %d мин\n" +
                        "Авиакомпания: %s\n" +
                        "Самолёт: %s\n" +
                        "Статус оплаты: %s\n" +
                        "===============================",
                bookingId,
                passenger.getFirstName(),
                passenger.getLastName(),
                passenger.getPassportNumber(),
                bookingTime.format(dtf),
                flight.getDepartureAirportCode(),
                flight.getArrivalAirportCode(),
                flight.getDepartureTime().format(dtf),
                flight.getArrivalTime().format(dtf),
                duration.toHours(),
                duration.toMinutesPart(),
                airlineCode,
                planeReg,
                isPaid ? "Оплачено" : "Не оплачено"
        );
    }

    public String getBookingReceiptShort() {
        return String.format(
                "Пассажир: %s %s | Паспорт: %d | Статус: %s",
                passenger.getFirstName(),
                passenger.getLastName(),
                passenger.getPassportNumber(),
                isPaid ? "Оплачено" : "Не оплачено"
        );
    }
}
