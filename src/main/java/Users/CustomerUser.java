package Users;

import jakarta.persistence.*;
import lombok.*;

@Entity
@DiscriminatorValue("CUSTOMER")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CustomerUser extends User {

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false)
    private int passportNumber;

    public CustomerUser(String login, String password,
                        String firstName, String lastName, int passportNumber) {
        super(login, password, UserRole.CUSTOMER);
        this.firstName = firstName;
        this.lastName = lastName;
        this.passportNumber = passportNumber;
    }
}
