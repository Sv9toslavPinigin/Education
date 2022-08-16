package net.thumbtack.buscompany.controller;


import lombok.AllArgsConstructor;
import net.thumbtack.buscompany.dto.request.TripRequestDto;
import net.thumbtack.buscompany.dto.response.AdminTripResponseDto;
import net.thumbtack.buscompany.dto.response.TripResponseDto;
import net.thumbtack.buscompany.exception.BuscompanyException;
import net.thumbtack.buscompany.service.TripService;
import net.thumbtack.buscompany.validation.annotation.ValidDate;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@AllArgsConstructor
@Validated
public class TripController {

    private TripService tripService;

    @PostMapping(value = "api/trip", produces = MediaType.APPLICATION_JSON_VALUE)
    public AdminTripResponseDto addTrip(@Valid @RequestBody TripRequestDto requestDto)
            throws BuscompanyException {
        return tripService.addTrip(requestDto);
    }

    @PutMapping(value = "api/trips/{tripId}")
    public AdminTripResponseDto changeTrip(@Valid @RequestBody TripRequestDto requestDto,
                                           @PathVariable("tripId") int tripId) throws BuscompanyException {
        return tripService.changeTrip(requestDto, tripId);
    }

    @DeleteMapping(value = "api/trips/{tripId}")
    public void deleteTrip(@PathVariable("tripId") int tripId) throws BuscompanyException {
        tripService.deleteTrip(tripId);
    }

    @GetMapping(value = "api/trips/{tripId}")
    public AdminTripResponseDto getTrip(@PathVariable("tripId") int tripId) throws BuscompanyException {
        return tripService.getTrip(tripId);
    }

    @PutMapping(value = "api/trips/{tripId}/approve")
    public AdminTripResponseDto approveTrip(@PathVariable("tripId") int tripId) throws BuscompanyException {
        return tripService.approveTrip(tripId);
    }

    @GetMapping(value = "/api/trips")
    public List<TripResponseDto> getTrips(
            Authentication authentication,
            @RequestParam(required = false) String fromStation,
            @RequestParam(required = false) String toStation,
            @RequestParam(required = false) String busName,
            @ValidDate @RequestParam(required = false) String fromDate,
            @ValidDate @RequestParam(required = false) String toDate) throws BuscompanyException {
        return tripService.getTrips(fromStation, toStation, busName, fromDate, toDate, authentication);
    }
}
