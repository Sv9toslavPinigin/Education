package net.thumbtack.buscompany.repository.iface;

import net.thumbtack.buscompany.model.DateTrip;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface DateTripRepository extends CrudRepository<DateTrip, Integer> {

    @Modifying
    @Query(value = "UPDATE date_trip SET free_places = free_places - :places " +
            "where id=:id and (free_places - :places) >=0",
            nativeQuery = true)
    int update(@Param("places") int places, @Param("id") int id);
}