package net.thumbtack.buscompany.model;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "places")
@NoArgsConstructor
@Getter
@Setter
public class Place {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private int placeNumber;
    @OneToOne
    @JoinColumn(name = "passenger_id")
    private Passenger passenger;

    public Place(int placeNumber) {
        this.placeNumber = placeNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Place place = (Place) o;
        return id == place.id && placeNumber == place.placeNumber && Objects.equals(passenger, place.passenger);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, placeNumber, passenger);
    }
}
