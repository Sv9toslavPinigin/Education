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
public class ClientProfileEditRequestDto {
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
    @Email
    private String mail;
    @NotEmpty
    @Pattern(regexp = "^(\\+7|7|8)?[\\s\\-]?\\(?[489][0-9]{2}\\)?" +
            "[\\s\\-]?[0-9]{3}[\\s\\-]?[0-9]{2}[\\s\\-]?[0-9]{2}$",
            message = "Should be mobile numbers of Russian operators")
    private String phone;
    @NotEmpty
    private String oldPassword;
    @NotEmpty
    @PasswordLength
    private String newPassword;
}
