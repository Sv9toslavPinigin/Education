package net.thumbtack.buscompany.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import net.thumbtack.buscompany.validation.annotation.NameLength;
import net.thumbtack.buscompany.validation.annotation.PasswordLength;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

@AllArgsConstructor
@Getter
@Builder
public class ClientRegisterRequestDto {
    @NotEmpty
    @NameLength
    @Pattern(regexp = "^[а-яА-ЯёЁ -]+$",
            message = "First name can only contain Russian letters , spaces and \"-\"")
    private String firstName;
    @NotEmpty
    @NameLength
    @Pattern(regexp = "^[а-яА-ЯёЁ -]+$",
            message = "Last name can only contain Russian letters , spaces and \"-\"")
    private String lastName;
    @NameLength
    @Pattern(regexp = "^[а-яА-ЯёЁ -]+|\\s*$",
            message = "Patronymic can only contain Russian letters , spaces and \"-\"")
    private String patronymic;
    @Email
    private String email;
    @NotEmpty
    @Pattern(regexp = "^(\\+7|7|8)?[\\s\\-]?\\(?[489][0-9]{2}\\)?" +
            "[\\s\\-]?[0-9]{3}[\\s\\-]?[0-9]{2}[\\s\\-]?[0-9]{2}$",
            message = "Should be mobile number of Russian operators")
    private String phone;
    @NotEmpty
    @NameLength
    @Pattern(regexp = "^[а-яА-ЯёЁa-zA-Z0-9]+$",
            message = "Login can contain only Latin and Russian letters and numbers")
    private String login;
    @NotEmpty
    @PasswordLength
    private String password;
}
