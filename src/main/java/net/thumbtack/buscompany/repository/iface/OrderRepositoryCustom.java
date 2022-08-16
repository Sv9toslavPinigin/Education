package net.thumbtack.buscompany.repository.iface;

import net.thumbtack.buscompany.model.Order;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

public interface OrderRepositoryCustom {
    List<Order> getOrdersWithParams(Integer clientId,
                                    String fromStation,
                                    String toStation,
                                    String busName,
                                    LocalDate fromDate,
                                    LocalDate toDate);
}
