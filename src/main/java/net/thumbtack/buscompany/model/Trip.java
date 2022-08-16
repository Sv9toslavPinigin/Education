package net.thumbtack.buscompany.model;


import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.thumbtack.buscompany.exception.BuscompanyException;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Entity
@Table(name = "trips")
@Getter
@Setter
@NoArgsConstructor
public class Trip {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Version
    private int version;
    @ManyToOne
    @JoinColumn(name = "bus_id")
    private Bus bus;
    private String fromStation;
    private String toStation;
    private String start;
    private String duration;
    private String price;
    private boolean approved;
    @OneToOne(orphanRemoval = true,
            cascade = CascadeType.ALL)
    private Schedule schedule;
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<DateTrip> dateTrips;

    @Builder
    public Trip(int id,
                int version,
                Bus bus,
                String fromStation,
                String toStation,
                String start,
                String duration,
                String price,
                boolean approved,
                Schedule schedule,
                List<LocalDate> dateTrips) throws BuscompanyException {
        this.id = id;
        this.version = version;
        this.bus = bus;
        this.fromStation = fromStation;
        this.toStation = toStation;
        this.start = start;
        this.duration = duration;
        this.price = price;
        this.approved = approved;
        this.schedule = schedule;
        setDateTrips(dateTrips);
    }


    public List<LocalDate> getDates() {
        return dateTrips.stream().map(DateTrip::getDate).sorted().collect(Collectors.toList());
    }

    public void setDateTrips(List<LocalDate> dates) throws BuscompanyException {
        Integer placeCount = bus.getPlaceCount();
        //List<Place> places = IntStream.range(1, placeCount + 1).mapToObj(Place::new).collect(Collectors.toList());
        if (schedule == null & dates != null) {
            dateTrips = dates.stream()
                    .map(x -> new DateTrip(x, generatePlaces(placeCount), placeCount))
                    .collect(Collectors.toList());
        }
        if (schedule != null) {
            dateTrips = schedule.getDateList().stream()
                    .map(x -> new DateTrip(x, generatePlaces(placeCount), placeCount))
                    .collect(Collectors.toList());
        }
    }

    public void setSchedule(Schedule schedule) throws BuscompanyException {
        this.schedule = schedule;
        if (schedule != null) {
            Integer placeCount = bus.getPlaceCount();
            //List<Place> places = IntStream.range(1, placeCount + 1).mapToObj(Place::new).collect(Collectors.toList());
            dateTrips = schedule.getDateList().stream()
                    .map(x -> new DateTrip(x, generatePlaces(placeCount), placeCount))
                    .collect(Collectors.toList());
        }
    }
    private List<Place> generatePlaces(int placeCount){
        return IntStream.range(1, placeCount + 1).mapToObj(Place::new).collect(Collectors.toList());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Trip trip = (Trip) o;
        return id == trip.id && version == trip.version && approved == trip.approved && bus.equals(trip.bus) && fromStation.equals(trip.fromStation) && toStation.equals(trip.toStation) && start.equals(trip.start) && duration.equals(trip.duration) && price.equals(trip.price) && schedule.equals(trip.schedule) && dateTrips.equals(trip.dateTrips);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, version, bus, fromStation, toStation, start, duration, price, approved, schedule, dateTrips);
    }
}
