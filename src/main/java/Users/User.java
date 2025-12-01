package Users;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "role")
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
public abstract class User {
    // атрибуты сущности user  id, login, password role, ключевое поле id
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String login;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, insertable = false, updatable = false)
    private UserRole role;
    public User(String login, String password, UserRole role) {
        this.login = login;
        this.password = password;
        this.role = role;
    }
}
