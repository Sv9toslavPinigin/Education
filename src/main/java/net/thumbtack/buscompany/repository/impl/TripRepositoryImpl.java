package net.thumbtack.buscompany.repository.impl;

import net.thumbtack.buscompany.model.Bus;
import net.thumbtack.buscompany.model.Schedule;
import net.thumbtack.buscompany.model.Trip;
import net.thumbtack.buscompany.repository.iface.TripRepositoryCustom;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class TripRepositoryImpl implements TripRepositoryCustom {

    @Autowired
    EntityManager entityManager;

    public List<Trip> getAllTripsWithParams(LocalDate fromDate, LocalDate toDate, String fromStation, String toStation, String busName) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Trip> criteriaQuery = criteriaBuilder.createQuery(Trip.class);
        Root<Trip> tripRoot = criteriaQuery.from(Trip.class);
        Join<Trip, Bus> busJoin = tripRoot.join("bus");
        Join<Trip, Schedule> scheduleJoin = tripRoot.join("schedule", JoinType.LEFT);
        ListJoin<Trip, LocalDate> joinDates = tripRoot.joinList("dateTrips", JoinType.LEFT);

        Set<Predicate> predicates = new HashSet<>();
        if (fromStation != null) {
            predicates.add(criteriaBuilder.equal(tripRoot.get("fromStation"), fromStation));
        }
        if (toStation != null) {
            predicates.add(criteriaBuilder.equal(tripRoot.get("toStation"), toStation));
        }
        if (busName != null) {
            predicates.add(criteriaBuilder.equal(busJoin.get("busName"), busName));
        }
        Predicate datePredicate = null;
        if (fromDate != null) {
            datePredicate = criteriaBuilder.or
                    (criteriaBuilder.equal(joinDates.get("date"), fromDate),
                            criteriaBuilder.equal(scheduleJoin.get("fromDate"), fromDate));
        }
        if (toDate != null & datePredicate != null) {
            datePredicate = criteriaBuilder.or(criteriaBuilder.or
                    (criteriaBuilder.equal(joinDates.get("date"), toDate),
                            criteriaBuilder.equal(scheduleJoin.get("toDate"), toDate)), datePredicate);
        } else if (toDate != null) {
            datePredicate = criteriaBuilder.or
                    (criteriaBuilder.equal(joinDates.get("date"), toDate),
                            criteriaBuilder.equal(scheduleJoin.get("toDate"), toDate));
        }
        if (datePredicate != null) {
            predicates.add(datePredicate);
        }

        criteriaQuery.select(tripRoot);
        criteriaQuery.where(predicates.toArray(Predicate[]::new)).groupBy(tripRoot.get("id"));
        TypedQuery<Trip> query = entityManager.createQuery(criteriaQuery);
        return query.getResultList();
    }
}
