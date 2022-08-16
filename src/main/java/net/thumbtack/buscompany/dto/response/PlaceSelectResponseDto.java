package net.thumbtack.buscompany.dto.response;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class PlaceSelectResponseDto {
    private int orderId;
    private String ticket;
    private String firstName;
    private String lastName;
    private String passport;
    private int place;
}
