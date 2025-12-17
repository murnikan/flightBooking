package Users;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO для создания/обновления администратора")
public class AdminUserDTO {

    @Schema(description = "логин администратора", example = "admin1111")
    private String login;

    @Schema(description = "пароль администратора", example = "password123")
    private String password;

    public String getLogin() { return login; }
    public void setLogin(String login) { this.login = login; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
