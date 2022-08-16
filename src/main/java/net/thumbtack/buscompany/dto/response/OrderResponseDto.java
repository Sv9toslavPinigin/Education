package net.thumbtack.buscompany.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.thumbtack.buscompany.dto.request.PassengerDto;

import java.util.List;

@AllArgsConstructor
@Getter
@Setter
public class OrderResponseDto {
    private int orderId;
    private int tripId;
    private String fromStation;
    private String toStation;
    private String busName;
    private String date;
    private String start;
    private String duration;
    private String price;
    private String totalPrice;
    private List<PassengerDto> passengers;
}
