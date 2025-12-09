package airports;

import jakarta.persistence.*;

@Entity
@Table(name = "airports")
public class Airport {
// атрибуты code(ключ) name city
    @Id
    @Column(name = "code", length = 3, nullable = false)
    private String code;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String city;

    @Column(name = "is_available_flight")
    private boolean availableFlight;
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public boolean isAvailableFlight() { return availableFlight; }
    public void setAvailableFlight(boolean availableFlight) { this.availableFlight = availableFlight; }

    public String getAirportInfo() {
        return String.format(
                "%s (%s), город: %s, доступен: %s",
                name, code, city, availableFlight ? "Да" : "Нет"
        );
    }
}
