package net.thumbtack.buscompany.model;

import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "passengers")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Passenger {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String firstName;
    private String lastName;
    private String passport;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Passenger passenger = (Passenger) o;
        return id == passenger.id && Objects.equals(firstName, passenger.firstName) && Objects.equals(lastName, passenger.lastName) && passport.equals(passenger.passport);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, firstName, lastName, passport);
    }
}
