package net.thumbtack.buscompany.repository.iface;

import net.thumbtack.buscompany.model.Trip;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;


public interface TripRepositoryCustom {
    List<Trip> getAllTripsWithParams(LocalDate fromDate,
                                     LocalDate toDate,
                                     String fromStation,
                                     String toStation,
                                     String busName);
}
