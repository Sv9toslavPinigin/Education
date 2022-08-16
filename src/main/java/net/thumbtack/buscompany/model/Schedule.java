package net.thumbtack.buscompany.model;

import lombok.*;
import net.thumbtack.buscompany.exception.BuscompanyErrorCode;
import net.thumbtack.buscompany.exception.BuscompanyException;
import net.thumbtack.buscompany.utils.DayOfWeek;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "schedules")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Schedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private LocalDate fromDate;
    private LocalDate toDate;
    private String period;

    public List<LocalDate> getDateList() throws BuscompanyException {

        List<LocalDate> datesList = fromDate.datesUntil(toDate.plusDays(1))
                .collect(Collectors.toList());

        if (period.equalsIgnoreCase("daily")) {
            return datesList;
        }
        if (period.equalsIgnoreCase("even")) {
            return datesList.stream()
                    .filter(x -> x.getDayOfMonth() % 2 == 0)
                    .collect(Collectors.toList());
        }

        if (period.equalsIgnoreCase("odd")) {
            return datesList.stream()
                    .filter(x -> x.getDayOfMonth() % 2 != 0)
                    .collect(Collectors.toList());
        }


        String[] daysArray = period.split(",");


        try {
            List<Integer> daysInt = Arrays.stream(daysArray)
                    .map(String::trim)
                    .map(Integer::parseInt)
                    .collect(Collectors.toList());
            return datesList.stream()
                    .filter(x -> daysInt.contains(x.getDayOfMonth()))
                    .collect(Collectors.toList());
        } catch (NumberFormatException e) {
        }

        Set<String> daysOfWeekSet = Arrays.stream(daysArray).collect(Collectors.toSet());
        if (daysOfWeekSet.size() != daysArray.length) {
            throw new BuscompanyException(BuscompanyErrorCode.INVALID_SCHEDULE_PERIOD_FORMAT, "period");
        }

        List<Integer> dayOfWeeks = daysOfWeekSet.stream()
                .map(String::trim)
                .map(DayOfWeek::valueOf)
                .map(DayOfWeek::getValue).collect(Collectors.toList());
        List<LocalDate> dateList = datesList.stream()
                .filter(x -> dayOfWeeks.contains(x.getDayOfWeek().getValue()))
                .collect(Collectors.toList());

        if (dateList.isEmpty()) {
            throw new BuscompanyException(BuscompanyErrorCode.INVALID_SCHEDULE_PERIOD_FORMAT, "period");
        }
        return dateList;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Schedule schedule = (Schedule) o;
        return id == schedule.id && fromDate.equals(schedule.fromDate) && toDate.equals(schedule.toDate) && period.equals(schedule.period);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, fromDate, toDate, period);
    }
}
