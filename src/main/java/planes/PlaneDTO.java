package planes;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public class PlaneDTO {

    @NotBlank
    private String registrationNumber;

    @NotBlank
    private String model;

    @NotBlank
    private String type;
    @Min(1)
    private int capacity;
    @Min(1)
    private int maxWeightKg;
    @Min(1)
    private int maxDistanceKm;
    @Min(1900)
    private int productionYear;

    // геттеры и сеттеры
    public String getRegistrationNumber() { return registrationNumber; }
    public void setRegistrationNumber(String registrationNumber) { this.registrationNumber = registrationNumber; }

    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }

    public int getMaxWeightKg() { return maxWeightKg; }
    public void setMaxWeightKg(int maxWeightKg) { this.maxWeightKg = maxWeightKg; }

    public int getMaxDistanceKm() { return maxDistanceKm; }
    public void setMaxDistanceKm(int maxDistanceKm) { this.maxDistanceKm = maxDistanceKm; }

    public int getProductionYear() { return productionYear; }
    public void setProductionYear(int productionYear) { this.productionYear = productionYear; }
}
