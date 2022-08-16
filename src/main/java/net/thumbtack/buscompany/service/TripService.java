package net.thumbtack.buscompany.service;

import lombok.AllArgsConstructor;
import net.thumbtack.buscompany.dto.request.TripRequestDto;
import net.thumbtack.buscompany.dto.response.AdminTripResponseDto;
import net.thumbtack.buscompany.dto.response.TripResponseDto;
import net.thumbtack.buscompany.exception.BuscompanyErrorCode;
import net.thumbtack.buscompany.exception.BuscompanyException;
import net.thumbtack.buscompany.mapper.iface.TripMapper;
import net.thumbtack.buscompany.model.Role;
import net.thumbtack.buscompany.model.Trip;
import net.thumbtack.buscompany.repository.iface.BusRepository;
import net.thumbtack.buscompany.repository.iface.TripRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class TripService extends ServiceBase{
    private TripRepository tripRepository;
    private BusRepository busRepository;
    private TripMapper tripMapper;

    public AdminTripResponseDto addTrip(TripRequestDto requestDto) throws BuscompanyException {
        if (!busRepository.existsByBusName(requestDto.getBusName())) {
            throw new BuscompanyException(BuscompanyErrorCode.BUS_NOT_FOUND, "busName");
        }
        Trip trip = tripMapper.fromTripDto(requestDto);
        trip = tripRepository.save(trip);
        return tripMapper.tripToAdminTripDto(trip);
    }

    public AdminTripResponseDto changeTrip(TripRequestDto requestDto, int tripId) throws BuscompanyException {
        Optional<Trip> optionalTrip = tripRepository.findById(tripId);
        if (optionalTrip.isEmpty()) {
            throw new BuscompanyException(BuscompanyErrorCode.TRIP_NOT_FOUND, "tripId");
        }
        Trip trip = optionalTrip.get();
        if (trip.isApproved()) {
            throw new BuscompanyException(BuscompanyErrorCode.TRIP_EDIT_FORBIDDEN, "tripId");
        }
        trip = tripMapper.changeTrip(requestDto, trip);
        trip = tripRepository.save(trip);
        return tripMapper.tripToAdminTripDto(trip);
    }

    public void deleteTrip(int tripId) throws BuscompanyException {
        if (!tripRepository.existsById(tripId)) {
            throw new BuscompanyException(BuscompanyErrorCode.TRIP_NOT_FOUND, "tripId");
        }
        tripRepository.deleteById(tripId);
    }

    public AdminTripResponseDto getTrip(int tripId) throws BuscompanyException {
        Optional<Trip> optionalTrip = tripRepository.findById(tripId);
        if (optionalTrip.isEmpty()) {
            throw new BuscompanyException(BuscompanyErrorCode.TRIP_NOT_FOUND, "tripId");
        }
        Trip trip = optionalTrip.get();
        return tripMapper.tripToAdminTripDto(trip);
    }

    public AdminTripResponseDto approveTrip(int tripId) throws BuscompanyException {
        Optional<Trip> optionalTrip = tripRepository.findById(tripId);
        if (optionalTrip.isEmpty()) {
            throw new BuscompanyException(BuscompanyErrorCode.TRIP_NOT_FOUND, "tripId");
        }
        Trip trip = optionalTrip.get();
        trip.setApproved(true);
        trip = tripRepository.save(trip);
        return tripMapper.tripToAdminTripDto(trip);
    }

    public List<TripResponseDto> getTrips(String fromStation,
                                          String toStation,
                                          String busName,
                                          String fromDate,
                                          String toDate,
                                          Authentication authentication) throws BuscompanyException {

        LocalDate dateFrom = parseDate(fromDate);
        LocalDate dateTo = parseDate(toDate);


        List<Trip> trips;
        trips = tripRepository.getAllTripsWithParams(dateFrom, dateTo, fromStation, toStation, busName);

        return mapTripList(trips, authentication);
    }

    private List<TripResponseDto> mapTripList(List<Trip> trips, Authentication authentication) {
        if (authentication.getAuthorities().contains(Role.ROLE_ADMIN)) {
            return trips.stream().map(tripMapper::tripToAdminTripDto).collect(Collectors.toList());
        } else {
            return trips.stream().filter(Trip::isApproved).map(tripMapper::tripToTripDto).collect(Collectors.toList());
        }
    }

}
