package net.thumbtack.buscompany.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

@AllArgsConstructor
@Getter
@Setter
@Builder
public class PlaceSelectRequestDto {
    @NotEmpty
    private int orderId;
    @NotEmpty
    private String firstName;
    @NotEmpty
    private String lastName;
    @NotEmpty
    private String passport;
    @NotEmpty
    private int place;
}
