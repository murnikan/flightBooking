package planes;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "planes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Plane {
// сущность самолет атрибуты: рег. номер, модель тип, вместимость, макс вес, макс дистанция
    @Id
    @Column(name = "registration_number", nullable = false, unique = true)
    private String registrationNumber; // ключ

    @Column(nullable = false)
    private String model;

    @Column(nullable = false)
    private String type;

    @Column(nullable = false)
    private int capacity;

    @Column(name = "max_weight_kg", nullable = false)
    private int maxWeightKg;

    @Column(name = "max_distance_km", nullable = false)
    private int maxDistanceKm;

    @Column(name = "production_year", nullable = false)
    private int productionYear;

    public boolean checkWeight(int baggageKg) {
        return baggageKg <= maxWeightKg;
    }

    public boolean checkDistance(int distanceKm) {
        return distanceKm <= maxDistanceKm;
    }

    public String getFullInfo() {
        return String.format(
                "тип: %s | модель: %s | вместимость: %d | макс. вес: %d кг | макс. дистанция: %d км | год: %d | рег. номер: %s",
                type, model, capacity, maxWeightKg, maxDistanceKm, productionYear, registrationNumber
        );
    }
}
