package net.thumbtack.buscompany.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "buses")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Bus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "busname")
    private String busName;

    @Column(name = "placecount")
    private Integer placeCount;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Bus bus = (Bus) o;
        return id == bus.id && busName.equals(bus.busName) && placeCount.equals(bus.placeCount);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, busName, placeCount);
    }

}
