package net.thumbtack.buscompany.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class AdminProfileEditResponseDto {
    private String firstName;
    private String lastName;
    private String patronymic;
    private String position;
    private String userType;
}
