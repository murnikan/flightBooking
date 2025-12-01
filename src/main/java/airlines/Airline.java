package airlines;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "airlines")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Airline {
// атрибуты сущности авиалинии: iata_code (ключевое поле, уникальное для каждого аэропорта в мире),
// название страна, год основания, сайт и общая информация
    @Id
    @Column(name = "iata_code", nullable = false, unique = true, length = 3)
    private String iataCode; // PK

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private String country;

    private int foundedYear;

    @Column(length = 2000)
    private String info;

    private String website;

    private int freeBaggageKg;
    private double extraKgPrice;

    public String getBaggageInfo() {
        return String.format("Бесплатный багаж: %d кг | Стоимость перевеса: %.2f за кг",
                freeBaggageKg, extraKgPrice);
    }

    public String getFullInfo() {
        return String.format("%s (%s), %s, основана в %d, сайт: %s\n%s\n%s",
                name, iataCode, country, foundedYear, website, info, getBaggageInfo());
    }
}
