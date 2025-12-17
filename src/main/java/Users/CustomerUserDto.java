package Users;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomerUserDto {
    private String login;
    private String password;
    private String firstName;
    private String lastName;
    private int passportNumber;
}
