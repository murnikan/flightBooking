package airlines;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class AirlineDTO {

    @NotBlank
    @Size(min = 2, max = 3)
    private String iataCode;

    @NotBlank
    private String name;

    @NotBlank
    private String country;

    private int foundedYear;
    private String info;
    private String website;
    private int freeBaggageKg;
    private double extraKgPrice;

    public String getIataCode() { return iataCode; }
    public void setIataCode(String iataCode) { this.iataCode = iataCode; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

    public int getFoundedYear() { return foundedYear; }
    public void setFoundedYear(int foundedYear) { this.foundedYear = foundedYear; }

    public String getInfo() { return info; }
    public void setInfo(String info) { this.info = info; }

    public String getWebsite() { return website; }
    public void setWebsite(String website) { this.website = website; }

    public int getFreeBaggageKg() { return freeBaggageKg; }
    public void setFreeBaggageKg(int freeBaggageKg) { this.freeBaggageKg = freeBaggageKg; }

    public double getExtraKgPrice() { return extraKgPrice; }
    public void setExtraKgPrice(double extraKgPrice) { this.extraKgPrice = extraKgPrice; }
}
