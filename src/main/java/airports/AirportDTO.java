package airports;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class AirportDTO {

    @NotBlank
    @Size(min = 3, max = 3)
    private String code;

    @NotBlank
    private String name;

    @NotBlank
    private String city;
    private boolean availableFlight;
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public boolean isAvailableFlight() { return availableFlight; }
    public void setAvailableFlight(boolean availableFlight) { this.availableFlight = availableFlight; }
}
