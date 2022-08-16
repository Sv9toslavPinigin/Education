package net.thumbtack.buscompany.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ClientProfileEditResponseDto {
    private String firstName;
    private String lastName;
    private String patronymic;
    private String email;
    private String phone;
    private String userType;
}
