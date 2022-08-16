package net.thumbtack.buscompany.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@Getter
@NoArgsConstructor
@Setter
public abstract class UserDto {
    private int id;
    private String firstName;
    private String lastName;
    private String patronymic;
    private String userType;
}
