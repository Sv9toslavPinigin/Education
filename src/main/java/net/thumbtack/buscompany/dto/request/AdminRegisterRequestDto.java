package net.thumbtack.buscompany.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import net.thumbtack.buscompany.validation.annotation.NameLength;
import net.thumbtack.buscompany.validation.annotation.PasswordLength;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

@AllArgsConstructor
@Getter
@Setter
@Builder
public class AdminRegisterRequestDto {
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
    @NotEmpty
    private String position;
    @NotEmpty
    @NameLength
    @Pattern(regexp = "^[а-яА-ЯёЁa-zA-Z0-9]+$",
            message = "Login can contain only Latin and Russian letters and numbers")
    private String login;
    @NotEmpty
    @PasswordLength
    private String password;
}
