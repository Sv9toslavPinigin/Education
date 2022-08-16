package net.thumbtack.buscompany.dto.response;

import lombok.Builder;
import lombok.Getter;


@Getter

public class ClientResponseDto extends UserDto {
    private String email;
    private String phone;

    @Builder
    public ClientResponseDto(int id, String firstName, String lastName, String patronymic, String userType, String email, String phone) {
        super(id, firstName, lastName, patronymic, userType);
        this.email = email;
        this.phone = phone;
    }
}
