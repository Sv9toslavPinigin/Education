package net.thumbtack.buscompany.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "date_trip")
@NoArgsConstructor
@Getter
@Setter
public class DateTrip {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Version
    private int version;
    private LocalDate date;
    private int freePlaces;
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<Place> places;


    public DateTrip(LocalDate date, List<Place> places, int freePlaces) {
        this.date = date;
        this.places = places;
        this.freePlaces = freePlaces;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DateTrip dateTrip = (DateTrip) o;
        return id == dateTrip.id && freePlaces == dateTrip.freePlaces && Objects.equals(version, dateTrip.version) && date.equals(dateTrip.date) && places.equals(dateTrip.places);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, version, date, freePlaces, places);
    }
}
