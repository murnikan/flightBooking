package org.example;

import Users.CustomerUser;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Booking {
    private String bookingId;
    private CustomerUser passenger;
    private Flight flight;
    private LocalDateTime bookingTime;
    private boolean isPaid;

    public Booking(String bookingId, CustomerUser passenger, Flight flight) {
        this.bookingId = bookingId;
        this.passenger = passenger;
        this.flight = flight;
        this.bookingTime = LocalDateTime.now();
        this.isPaid = false;
    }

    public String getBookingId() { return bookingId; }
    public CustomerUser getPassenger() { return passenger; }
    public Flight getFlight() { return flight; }
    public LocalDateTime getBookingTime() { return bookingTime; }
    public boolean isPaid() { return isPaid; }
    public void pay() { this.isPaid = true; }

    // методы для выдачи чеков (второй используется для общего вывода в демораннинге
    public String getBookingReceipt() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        return String.format(
                "====== Бронь подтверждена ======\n" +
                        "ID брони: %s\n" +
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
                flight.getDepartureCity(),
                flight.getArrivalCity(),
                flight.getDepartureTime().format(dtf),
                flight.getArrivalTime().format(dtf),
                flight.getDuration().toHours(),
                flight.getDuration().toMinutesPart(),
                flight.getAirline().getName(),
                flight.getPlane().getFullInfo(),
                isPaid ? "Оплачено" : "Не оплачено"
        );
    }
    public String getBookingReceiptShort() {
        return String.format("Пассажир: %s %s | Паспорт: %d | Статус: %s",
                passenger.getFirstName(),
                passenger.getLastName(),
                passenger.getPassportNumber(),
                isPaid ? "Оплачено" : "Не оплачено");
    }

}
