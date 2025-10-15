package org.example;

public class Airport {
    private String AirportName;
    private String AirportCode;
    private boolean IsAvaibleFlight;

    public String getAirportName() {
        return AirportName;
    }

    public void setAirportName(String airportName) {
        AirportName = airportName;
    }

    public String getAirportCode() {
        return AirportCode;
    }

    public void setAirportCode(String airportCode) {
        AirportCode = airportCode;
    }

    public boolean isAvaibleFlight() {
        return IsAvaibleFlight;
    }

    public void setAvaibleFlight(boolean avaibleFlight) {
        IsAvaibleFlight = avaibleFlight;
    }
}
