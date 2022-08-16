package net.thumbtack.buscompany.model;

import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "orders")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @ManyToOne
    @JoinColumn(name = "trip_id")
    private Trip trip;
    @ManyToOne
    @JoinColumn(name = "client_id")
    private Client client;
    private LocalDate date;
    private String totalPrice;
    @OneToMany(cascade = CascadeType.ALL)
    List<Passenger> passengers;

    public void setPassengers(List<Passenger> passengers) {
        this.totalPrice = String.valueOf((Integer.parseInt(trip.getPrice()) * passengers.size()));
        this.passengers = passengers;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return id == order.id && trip.equals(order.trip) && client.equals(order.client) && date.equals(order.date) && totalPrice.equals(order.totalPrice) && passengers.equals(order.passengers);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, trip, client, date, totalPrice, passengers);
    }
}
