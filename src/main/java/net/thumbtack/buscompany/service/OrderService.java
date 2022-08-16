package net.thumbtack.buscompany.service;

import lombok.AllArgsConstructor;
import net.thumbtack.buscompany.dto.request.OrderRequestDto;
import net.thumbtack.buscompany.dto.request.PlaceSelectRequestDto;
import net.thumbtack.buscompany.dto.response.OrderResponseDto;
import net.thumbtack.buscompany.dto.response.PlaceSelectResponseDto;
import net.thumbtack.buscompany.exception.BuscompanyErrorCode;
import net.thumbtack.buscompany.exception.BuscompanyException;
import net.thumbtack.buscompany.mapper.iface.OrderMapper;
import net.thumbtack.buscompany.model.*;
import net.thumbtack.buscompany.repository.iface.*;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

@AllArgsConstructor
@Service
public class OrderService extends ServiceBase {

    private OrderRepository orderRepository;
    private OrderMapper orderMapper;
    private TripRepository tripRepository;
    private ClientRepository clientRepository;
    private DateTripRepository dateTripRepository;
    private PlaceRepository placeRepository;

    @Transactional
    public OrderResponseDto addOrder(OrderRequestDto requestDto, Authentication authentication) throws BuscompanyException {
        Optional<Trip> optionalTrip = tripRepository.findById(requestDto.getTripId());
        if (optionalTrip.isEmpty()) {
            throw new BuscompanyException(BuscompanyErrorCode.TRIP_NOT_FOUND, "tripId");
        }
        Trip trip = optionalTrip.get();
        if (!trip.isApproved()) {
            throw new BuscompanyException(BuscompanyErrorCode.TRIP_NOT_APPROVED, "tripId");
        }
        Optional<DateTrip> optionalDateTrip = trip.getDateTrips().stream()
                .filter(x -> x.getDate().isEqual(LocalDate.parse(requestDto.getDate())))
                .findFirst();
        if (optionalDateTrip.isEmpty()) {
            throw new BuscompanyException(BuscompanyErrorCode.DATE_NOT_FOUND, "date");
        }

        Order order = orderMapper.orderDtoToOrder(requestDto);
        Client client = clientRepository.findByUsername(authentication.getName());
        order.setClient(client);
        int passengersCount = order.getPassengers().size();
        DateTrip dateTrip = optionalDateTrip.get();
        if (dateTripRepository.update(passengersCount, dateTrip.getId()) != 1) {
            throw new BuscompanyException(BuscompanyErrorCode.NO_FREE_PLACES, "date");
        }
        orderRepository.save(order);
        return orderMapper.orderToDto(order);
    }

    public List<OrderResponseDto> getOrders(Integer clientId,
                                            String fromStation,
                                            String toStation,
                                            String busName,
                                            String fromDate,
                                            String toDate,
                                            Authentication authentication) {

        LocalDate dateFrom = parseDate(fromDate);
        LocalDate dateTo = parseDate(toDate);
        if (authentication.getAuthorities().contains(Role.ROLE_CLIENT)) {
            clientId = clientRepository.findByUsername(authentication.getName()).getId();
        }
        List<Order> orders = orderRepository.getOrdersWithParams(clientId, fromStation, toStation, busName, dateFrom, dateTo);

        return orders.stream().map(orderMapper::orderToDto).collect(toList());
    }


    public List<String> getFreePlaces(int orderId, Authentication authentication) throws BuscompanyException {
        Optional<Order> orderOptional = orderRepository.findById(orderId);
        if (orderOptional.isEmpty()) {
            throw new BuscompanyException(BuscompanyErrorCode.ORDER_NOT_FOUND, "orderId");
        }
        Order order = orderOptional.get();
        if (!order.getClient().getUsername().equals(authentication.getName())) {
            throw new BuscompanyException(BuscompanyErrorCode.ORDER_ACCESS_DENIED, "orderId");
        }
        DateTrip dateTrip = order.getTrip().getDateTrips().stream()
                .filter(x -> x.getDate().isEqual(order.getDate()))
                .findFirst().get();
        return dateTrip.getPlaces().stream()
                .filter(x->x.getPassenger()==null)
                .map(x -> "Место" + x.getPlaceNumber())
                .collect(toList());
    }


    @Transactional
    public PlaceSelectResponseDto selectPlace(PlaceSelectRequestDto requestDto, Authentication authentication) throws BuscompanyException {
        Optional<Order> orderOptional = orderRepository.findById(requestDto.getOrderId());
        if (orderOptional.isEmpty()) {
            throw new BuscompanyException(BuscompanyErrorCode.ORDER_NOT_FOUND, "orderId");
        }
        Order order = orderOptional.get();
        if (!order.getClient().getUsername().equals(authentication.getName())) {
            throw new BuscompanyException(BuscompanyErrorCode.ORDER_ACCESS_DENIED, "orderId");
        }
        Optional<Passenger> optionalPassenger = order.getPassengers().stream()
                .filter(passenger -> passenger.getPassport().equals(requestDto.getPassport()))
                .findFirst();
        if (optionalPassenger.isEmpty()) {
            throw new BuscompanyException(BuscompanyErrorCode.PASSENGER_NOT_FOUND, "passport");
        }
        Passenger passenger = optionalPassenger.get();
        int selectedPlace = requestDto.getPlace();
        if (selectedPlace == 0 || selectedPlace > order.getTrip().getBus().getPlaceCount()) {
            throw new BuscompanyException(BuscompanyErrorCode.PLACE_NOT_FOUND, "place");
        }
        DateTrip dateTrip = order.getTrip().getDateTrips().stream()
                .filter(x -> x.getDate().isEqual(order.getDate()))
                .findFirst().get();
        Place place = dateTrip.getPlaces().get(requestDto.getPlace() - 1);
        if (place.getPassenger()!=null) {
            throw new BuscompanyException(BuscompanyErrorCode.PLACE_ALREADY_SELECT, "place");
        }
        place.setPassenger(passenger);
        placeRepository.save(place);
        orderRepository.save(order);

        return PlaceSelectResponseDto.builder()
                .orderId(order.getId())
                .ticket("Билет " + order.getId() + "_" + requestDto.getPlace())
                .place(requestDto.getPlace())
                .firstName(requestDto.getFirstName())
                .lastName(requestDto.getLastName())
                .passport(requestDto.getPassport())
                .build();
    }

    @Transactional
    public void removeOrder(int orderId, Authentication authentication) throws BuscompanyException {
        Optional<Order> orderOptional = orderRepository.findById(orderId);
        if (orderOptional.isEmpty()) {
            throw new BuscompanyException(BuscompanyErrorCode.ORDER_NOT_FOUND, "orderId");
        }
        Order order = orderOptional.get();
        if (!order.getClient().getUsername().equals(authentication.getName())) {
            throw new BuscompanyException(BuscompanyErrorCode.ORDER_ACCESS_DENIED, "orderId");
        }
        orderRepository.delete(order);
    }


}
