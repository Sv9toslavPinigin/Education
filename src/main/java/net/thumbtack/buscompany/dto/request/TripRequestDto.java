package net.thumbtack.buscompany.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import net.thumbtack.buscompany.validation.annotation.ScheduleValidation;
import net.thumbtack.buscompany.validation.annotation.ValidDate;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import java.util.List;

@AllArgsConstructor
@Getter
@ScheduleValidation
@Builder
public class TripRequestDto {
    @NotEmpty
    private String busName;
    @NotEmpty
    private String fromStation;
    @NotEmpty
    private String toStation;
    @NotEmpty
    @Pattern(regexp = "^([0-9]|0[0-9]|1[0-9]|2[0-3]):[0-5][0-9]$",
            message = "Must be time format HH:MM")
    private String start;
    @NotEmpty
    @Pattern(regexp = "^([0-9]|0[0-9]|1[0-9]|2[0-3]):[0-5][0-9]$",
            message = "Must be time format HH:MM")
    private String duration;
    @NotEmpty
    @Pattern(regexp = "^\\d{1,}$")
    private String price;
    @Valid
    private ScheduleDto schedule;
    private List<@ValidDate @NotEmpty String> dates;
}
