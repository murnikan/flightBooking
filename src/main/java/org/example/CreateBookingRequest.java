package org.example;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "запрос на создание брони")
public class CreateBookingRequest {

    @Schema(description = "ID пассажира", example = "34")
    private Long passengerId;

    @Schema(description = "ID рейса", example = "303")
    private Long flightId;

    public Long getPassengerId() {
        return passengerId;
    }

    public void setPassengerId(Long passengerId) {
        this.passengerId = passengerId;
    }

    public Long getFlightId() {
        return flightId;
    }

    public void setFlightId(Long flightId) {
        this.flightId = flightId;
    }
}
