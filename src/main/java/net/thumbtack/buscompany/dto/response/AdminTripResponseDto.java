package net.thumbtack.buscompany.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.thumbtack.buscompany.dto.request.ScheduleDto;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
public class AdminTripResponseDto extends TripResponseDto {

    private boolean approved;

    @Builder
    public AdminTripResponseDto(String tripId,
                                String fromStation,
                                String toStation,
                                String start,
                                String duration,
                                String price,
                                BusInfoDto bus,
                                ScheduleDto schedule,
                                List<String> dates,
                                boolean approved) {
        super(tripId, fromStation, toStation, start, duration, price, bus, schedule, dates);
        this.approved = approved;
    }
}
