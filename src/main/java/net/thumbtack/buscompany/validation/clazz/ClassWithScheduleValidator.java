package net.thumbtack.buscompany.validation.clazz;

import net.thumbtack.buscompany.dto.request.ScheduleDto;
import net.thumbtack.buscompany.utils.DayOfWeek;
import net.thumbtack.buscompany.validation.annotation.ScheduleValidation;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

public class ClassWithScheduleValidator implements ConstraintValidator<ScheduleValidation, Object> {
    private ConstraintValidatorContext constraintValidatorContext;



    @Override
    public boolean isValid(Object o, ConstraintValidatorContext constraintValidatorContext) {

        this.constraintValidatorContext = constraintValidatorContext;
        ScheduleDto fieldScheduleValue = (ScheduleDto) new BeanWrapperImpl(o)
                .getPropertyValue("Schedule");
        List<String> fieldDatesValue = (List<String>) new BeanWrapperImpl(o)
                .getPropertyValue("dates");
        if (fieldDatesValue == null & fieldScheduleValue == null) {
            return false;
        }
        if (fieldScheduleValue != null & fieldDatesValue == null) {
            return checkSchedulePeriodFormat(fieldScheduleValue);
        }
        if (fieldScheduleValue != null & fieldDatesValue.isEmpty()) {
            return checkSchedulePeriodFormat(fieldScheduleValue);
        }
        if (fieldScheduleValue == null & !fieldDatesValue.isEmpty()) {
            return true;
        }

        constraintValidatorContext
                .buildConstraintViolationWithTemplate("Should only be Schedule or dates field")
                .addPropertyNode("dates").addConstraintViolation();
        return false;
    }

    private boolean checkSchedulePeriodFormat(ScheduleDto schedule) {

        LocalDate fromDate = LocalDate.parse(schedule.getFromDate());
        LocalDate toDate = LocalDate.parse(schedule.getToDate());

        if (fromDate.isAfter(toDate)) {
            constraintValidatorContext.disableDefaultConstraintViolation();
            constraintValidatorContext
                    .buildConstraintViolationWithTemplate("Schedule.fromDate must be earlier then Schedule.toDate")
                    .addPropertyNode("Schedule.fromDate")
                    .addConstraintViolation();
            return false;
        }

        if (schedule.getPeriod().equalsIgnoreCase("daily")) {
            return true;
        }
        if (schedule.getPeriod().equalsIgnoreCase("odd")) {
            return true;
        }
        if (schedule.getPeriod().equalsIgnoreCase("even")) {
            return true;
        }
        String[] daysArray = schedule.getPeriod().split(",");
        try {
            Arrays.stream(daysArray)
                    .map(String::trim)
                    .map(DayOfWeek::valueOf).collect(Collectors.toList());
            return true;
        } catch (IllegalArgumentException e) {

        }
        try {
            Arrays.stream(daysArray).map(String::trim).map(Integer::parseInt).collect(Collectors.toList());
            return true;
        } catch (NumberFormatException e) {

        }

        constraintValidatorContext.disableDefaultConstraintViolation();
        constraintValidatorContext
                .buildConstraintViolationWithTemplate("Schedule.period format must be: daily, odd, even," +
                        " days of week (Sun, Mon, Thu... etc) or number of day")
                .addPropertyNode("Schedule.period")
                .addConstraintViolation();

        return false;
    }
}
