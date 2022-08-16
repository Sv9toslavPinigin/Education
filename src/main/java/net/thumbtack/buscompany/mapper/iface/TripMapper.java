package net.thumbtack.buscompany.mapper.iface;

import net.thumbtack.buscompany.dto.request.TripRequestDto;
import net.thumbtack.buscompany.dto.response.AdminTripResponseDto;
import net.thumbtack.buscompany.dto.response.TripResponseDto;
import net.thumbtack.buscompany.exception.BuscompanyException;
import net.thumbtack.buscompany.mapper.classes.BusFromRepositoryMapper;
import net.thumbtack.buscompany.mapper.classes.DateMapper;
import net.thumbtack.buscompany.model.Trip;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(uses = {ScheduleMapper.class,
        DateMapper.class,
        BusFromRepositoryMapper.class},
        componentModel = "spring")
public interface TripMapper {

    @Mapping(source = "busName", target = "bus")
    @Mapping(source = "requestDto.dates", target = "dateTrips")
    Trip fromTripDto(TripRequestDto requestDto) throws BuscompanyException;

    @Mapping(source = "id", target = "tripId")
    AdminTripResponseDto tripToAdminTripDto(Trip trip);

    @Mapping(source = "id", target = "tripId")
    TripResponseDto tripToTripDto(Trip trip);


    @Mapping(source = "trip.id", target = "id")
    @Mapping(source = "requestDto.dates", target = "dateTrips")
    @Mapping(source = "requestDto.schedule", target = "schedule")
    @Mapping(source = "requestDto.fromStation", target = "fromStation")
    @Mapping(source = "requestDto.toStation", target = "toStation")
    @Mapping(source = "requestDto.start", target = "start")
    @Mapping(source = "requestDto.duration", target = "duration")
    @Mapping(source = "requestDto.price", target = "price")
    @Mapping(source = "trip.approved", target = "approved")
    @Mapping(source = "requestDto.busName", target = "bus")
    Trip changeTrip(TripRequestDto requestDto, Trip trip) throws BuscompanyException;

}
