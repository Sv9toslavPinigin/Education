package net.thumbtack.buscompany.repository.impl;

import net.thumbtack.buscompany.model.Order;
import net.thumbtack.buscompany.model.*;
import net.thumbtack.buscompany.repository.iface.OrderRepositoryCustom;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.time.LocalDate;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class OrderRepositoryImpl implements OrderRepositoryCustom {

    @Autowired
    EntityManager entityManager;

    @Override
    public List<Order> getOrdersWithParams(Integer clientId,
                                           String fromStation,
                                           String toStation,
                                           String busName,
                                           LocalDate fromDate,
                                           LocalDate toDate) {

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Order> criteriaQuery = criteriaBuilder.createQuery(Order.class);
        Root<Order> orderRoot = criteriaQuery.from(Order.class);
        Join<Order, Client> clientJoin = orderRoot.join("client", JoinType.LEFT);
        Join<Order, Trip> tripJoin = orderRoot.join("trip");
        Join<Trip, Bus> busJoin = tripJoin.join("bus");
        Join<Trip, Schedule> scheduleJoin = tripJoin.join("schedule", JoinType.LEFT);
        ListJoin<Trip,LocalDate> joinDates = tripJoin.joinList("dateTrips",JoinType.LEFT);

        Set<Predicate> predicates = new HashSet<>();
        if (clientId != null) {
            predicates.add(criteriaBuilder.equal(clientJoin.get("id"), clientId));
        }
        if (fromStation != null) {
            predicates.add(criteriaBuilder.equal(tripJoin.get("fromStation"), fromStation));
        }
        if (toStation != null) {
            predicates.add(criteriaBuilder.equal(tripJoin.get("toStation"), toStation));
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

        criteriaQuery.select(orderRoot);
        criteriaQuery.where(predicates.toArray(Predicate[]::new)).groupBy(orderRoot.get("id"));
        TypedQuery<Order> query = entityManager.createQuery(criteriaQuery);
        return query.getResultList();
    }
}
