package net.thumbtack.buscompany.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.thumbtack.buscompany.dto.request.ScheduleDto;

import java.util.List;

@AllArgsConstructor
@Getter
@NoArgsConstructor
@Setter
public class TripResponseDto {
    private String tripId;
    private String fromStation;
    private String toStation;
    private String start;
    private String duration;
    private String price;
    private BusInfoDto bus;
    private ScheduleDto schedule;
    private List<String> dates;
}
