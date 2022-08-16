package net.thumbtack.buscompany.controller;


import lombok.AllArgsConstructor;
import net.thumbtack.buscompany.repository.iface.OrderRepository;
import net.thumbtack.buscompany.repository.iface.TripRepository;
import net.thumbtack.buscompany.repository.iface.UserRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@ConditionalOnProperty(havingValue = "true", name = "debug", prefix = "buscompany")
public class DebugController {

    private TripRepository tripRepository;
    private UserRepository userRepository;
    private OrderRepository orderRepository;

    @PostMapping(value = "api/debug/clear")
    public void clearDatabase() {
        orderRepository.deleteAll();
        tripRepository.deleteAll();
        userRepository.deleteAll();
    }
}
