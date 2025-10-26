package planes;


public abstract class Plane {
    protected String model;
    protected String type;
    protected int capacity;
    protected int maxWeightKg;
    protected int maxDistanceKm;
    protected int productionYear;
    protected String registrationNumber;
    public Plane(String model, String type, int capacity, int maxWeightKg,
                 int maxDistanceKm, int productionYear, String registrationNumber) {
        this.model = model;
        this.type = type;
        this.capacity = capacity;
        this.maxWeightKg = maxWeightKg;
        this.maxDistanceKm = maxDistanceKm;
        this.productionYear = productionYear;
        this.registrationNumber = registrationNumber;
    }

    public boolean checkWeight(int baggageKg) {
        return baggageKg <= maxWeightKg;
    }

    public boolean checkDistance(int distanceKm) {
        return distanceKm <= maxDistanceKm;
    }

    public String getFullInfo() {
        return String.format(
                "тип: %s | модель: %s | вместимость: %d | макс. вес: %d кг | макс. дистанция: %d км | год: %d | рег. номер: %s",
                getClass().getSimpleName(),
                model,
                capacity,
                maxWeightKg,
                maxDistanceKm,
                productionYear,
                registrationNumber
        );
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public int getMaxWeightKg() {
        return maxWeightKg;
    }

    public void setMaxWeightKg(int maxWeightKg) {
        this.maxWeightKg = maxWeightKg;
    }

    public int getMaxDistanceKm() {
        return maxDistanceKm;
    }

    public void setMaxDistanceKm(int maxDistanceKm) {
        this.maxDistanceKm = maxDistanceKm;
    }

    public int getProductionYear() {
        return productionYear;
    }

    public void setProductionYear(int productionYear) {
        this.productionYear = productionYear;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }
}
