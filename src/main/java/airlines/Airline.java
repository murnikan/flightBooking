package airlines;

public abstract class Airline {
    protected String name;
    protected String country;
    protected int foundedYear;
    protected String info;
    protected String iataCode;
    protected String website;

    protected int freeBaggageKg;
    protected double extraKgPrice;

    public Airline(String name, String country, int foundedYear, String info,
                   String iataCode, String website, int freeBaggageKg, double extraKgPrice) {
        this.name = name;
        this.country = country;
        this.foundedYear = foundedYear;
        this.info = info;
        this.iataCode = iataCode;
        this.website = website;
        this.freeBaggageKg = freeBaggageKg;
        this.extraKgPrice = extraKgPrice;
    }

    public String getName() { return name; }
    public String getCountry() { return country; }
    public int getFoundedYear() { return foundedYear; }
    public String getInfo() { return info; }
    public String getIataCode() { return iataCode; }
    public String getWebsite() { return website; }
    public int getFreeBaggageKg() { return freeBaggageKg; }
    public double getExtraKgPrice() { return extraKgPrice; }

    public String getBaggageInfo() {
        return String.format("Бесплатный багаж: %d кг | Стоимость перевеса: %.2f за кг",
                freeBaggageKg, extraKgPrice);
    }

    public String getFullInfo() {
        return String.format("%s (%s), %s, основана в %d, сайт: %s\n%s\n%s",
                name, iataCode, country, foundedYear, website, info, getBaggageInfo());
    }
}
