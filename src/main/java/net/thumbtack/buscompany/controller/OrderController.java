package net.thumbtack.buscompany.controller;

import lombok.AllArgsConstructor;
import net.thumbtack.buscompany.dto.request.OrderRequestDto;
import net.thumbtack.buscompany.dto.request.PlaceSelectRequestDto;
import net.thumbtack.buscompany.dto.response.OrderResponseDto;
import net.thumbtack.buscompany.dto.response.PlaceSelectResponseDto;
import net.thumbtack.buscompany.exception.BuscompanyException;
import net.thumbtack.buscompany.service.OrderService;
import net.thumbtack.buscompany.validation.annotation.ValidDate;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@AllArgsConstructor
public class OrderController {

    private OrderService orderService;

    @PostMapping(value = "api/orders")
    public OrderResponseDto addOrder(@Valid @RequestBody OrderRequestDto requestDto,
                                     Authentication authentication) throws BuscompanyException {

        return orderService.addOrder(requestDto, authentication);
    }

    @GetMapping(value = "api/orders")
    public List<OrderResponseDto> getOrders(Authentication authentication,
                                            @RequestParam(required = false) Integer clientId,
                                            @RequestParam(required = false) String fromStation,
                                            @RequestParam(required = false) String toStation,
                                            @RequestParam(required = false) String busName,
                                            @ValidDate @RequestParam(required = false) String fromDate,
                                            @ValidDate @RequestParam(required = false) String toDate) {

        return orderService.getOrders(clientId, fromStation, toStation, busName, fromDate, toDate, authentication);
    }

    @GetMapping(value = "api/places/{orderId}")
    public List<String> getFreePlaces(@PathVariable("orderId") int orderId,
                                      Authentication authentication) throws BuscompanyException {
        return orderService.getFreePlaces(orderId, authentication);
    }

    @PostMapping(value = "/api/places")
    public PlaceSelectResponseDto selectPlace(@RequestBody PlaceSelectRequestDto requestDto,
                                              Authentication authentication) throws BuscompanyException {

        return orderService.selectPlace(requestDto, authentication);
    }

    @DeleteMapping(value = "/api/orders/{orderId}")
    public void removeOrder(@PathVariable int orderId, Authentication authentication) throws BuscompanyException {
        orderService.removeOrder(orderId, authentication);
    }

}
