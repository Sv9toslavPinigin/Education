package net.thumbtack.buscompany.mapper.iface;

import net.thumbtack.buscompany.dto.request.OrderRequestDto;
import net.thumbtack.buscompany.dto.response.OrderResponseDto;
import net.thumbtack.buscompany.mapper.classes.BusFromRepositoryMapper;
import net.thumbtack.buscompany.mapper.classes.DateMapper;
import net.thumbtack.buscompany.mapper.classes.TripFromRepositoryMapper;
import net.thumbtack.buscompany.model.Order;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(uses = {DateMapper.class,
        BusFromRepositoryMapper.class,
        TripFromRepositoryMapper.class},
        componentModel = "spring",
        builder = @Builder(disableBuilder = true))
public interface OrderMapper {

    @Mapping(source = "tripId", target = "trip")
    Order orderDtoToOrder(OrderRequestDto requestDto);

    @Mapping(source = "id", target = "orderId")
    @Mapping(source = "trip.id", target = "tripId")
    @Mapping(source = "trip.fromStation", target = "fromStation")
    @Mapping(source = "trip.toStation", target = "toStation")
    @Mapping(source = "trip.start", target = "start")
    @Mapping(source = "trip.duration", target = "duration")
    @Mapping(source = "trip.price", target = "price")
    @Mapping(target = "busName", expression = "java(order.getTrip().getBus().getBusName())")
    OrderResponseDto orderToDto(Order order);
}
