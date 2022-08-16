package net.thumbtack.buscompany.mapper.classes;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;

@Component
public class DateMapper {

    @Autowired
    private DateTimeFormatter format;

    public String asString(LocalDate date) {
        return date != null ? date.format(format) : null;
    }

    public LocalDate asLocalDate(String date) {
        try {
            return date != null ? LocalDate.parse(date,format) : null;
        } catch (DateTimeParseException e) {
            throw new RuntimeException(e);
        }
    }
}
