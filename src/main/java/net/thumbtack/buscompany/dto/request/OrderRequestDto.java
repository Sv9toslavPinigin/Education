package net.thumbtack.buscompany.dto.request;

import lombok.*;
import net.thumbtack.buscompany.validation.annotation.ValidDate;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class OrderRequestDto {
    @NotNull
    private int tripId;
    @NotEmpty
    @ValidDate
    private String date;
    @NotEmpty
    private List<@Valid PassengerDto> passengers;
}
