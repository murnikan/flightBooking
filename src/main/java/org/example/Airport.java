package org.example;

public class Airport {
    private String name;
    private String code;
    private String city;
    private boolean isAvailableFlight;


    public Airport(String name, String code, String city, boolean isAvailableFlight) {
        this.name = name;
        this.code = code;
        this.city = city;
        this.isAvailableFlight = isAvailableFlight;
    }


    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public boolean isAvailableFlight() { return isAvailableFlight; }
    public void setAvailableFlight(boolean availableFlight) { isAvailableFlight = availableFlight; }
}