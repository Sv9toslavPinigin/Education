package net.thumbtack.buscompany.repository.iface;

import net.thumbtack.buscompany.model.Trip;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface TripRepository extends CrudRepository<Trip, Integer>, TripRepositoryCustom {

}
