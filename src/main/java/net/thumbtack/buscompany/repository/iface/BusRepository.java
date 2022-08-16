package net.thumbtack.buscompany.repository.iface;

import net.thumbtack.buscompany.model.Bus;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BusRepository extends CrudRepository<Bus, Integer> {
    boolean existsByBusName(String name);

    List<Bus> findAll();

    Bus findByBusName(String busName);
}
