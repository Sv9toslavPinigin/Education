package net.thumbtack.buscompany.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import net.thumbtack.buscompany.validation.annotation.ValidDate;

import javax.validation.constraints.NotEmpty;

@AllArgsConstructor
@Getter
@Builder
@Setter
public class ScheduleDto {
    @NotEmpty
    @ValidDate
    private String fromDate;
    @NotEmpty
    @ValidDate
    private String toDate;
    @NotEmpty
    private String period;
}
