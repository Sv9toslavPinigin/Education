package net.thumbtack.buscompany.mapper.classes;

import net.thumbtack.buscompany.model.Trip;
import net.thumbtack.buscompany.repository.iface.TripRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class TripFromRepositoryMapper {

    @Autowired
    TripRepository tripRepository;

    public Trip asTrip(int tripId) {
        Optional<Trip> optionalTrip = tripRepository.findById(tripId);
        if (optionalTrip.isEmpty()) {
            return null;
        }
        return optionalTrip.get();
    }

}
