package net.thumbtack.buscompany.validation.clazz;

import net.thumbtack.buscompany.validation.annotation.ValidDate;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DateValidator implements ConstraintValidator<ValidDate, String> {

    public void initialize(ValidDate constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    public boolean isValid(String date, ConstraintValidatorContext context) {
        if (date == null) {
            return true;
        }
        String regexp = "^((?:19|20)[0-9][0-9])-(0?[1-9]|1[012])-(0?[1-9]|[12][0-9]|3[01])$";
        Pattern pattern = Pattern.compile(regexp);
        Matcher matcher = pattern.matcher(date);
        if (matcher.matches()) {
            matcher.reset();
            if (matcher.find()) {
                String day = matcher.group(3);
                String month = matcher.group(2);
                int year = Integer.parseInt(matcher.group(1));
                if (day.equals("31") &&
                        (month.equals("4") || month.equals("6") || month.equals("9") ||
                                month.equals("11") || month.equals("04") || month.equals("06") ||
                                month.equals("09"))) {
                    context.disableDefaultConstraintViolation();
                    context.buildConstraintViolationWithTemplate(
                                    Month.of(Integer.parseInt(month)).getDisplayName(TextStyle.FULL, Locale.ENGLISH)
                                            + " does not have " + day + " days")
                            .addConstraintViolation();
                    return false; // only 1,3,5,7,8,10,12 has 31 days
                } else if (month.equals("2") || month.equals("02")) {
                    //leap year
                    if (year % 4 == 0) {
                        if (day.equals("30") || day.equals("31")) {
                            context.disableDefaultConstraintViolation();
                            context.buildConstraintViolationWithTemplate(
                                            Month.of(Integer.parseInt(month)).getDisplayName(TextStyle.FULL, Locale.ENGLISH)
                                                    + year + " does not have " + day + " days")
                                    .addConstraintViolation();
                            return false;
                        } else {
                            return true;
                        }
                    } else {
                        if (day.equals("29") || day.equals("30") || day.equals("31")) {
                            context.disableDefaultConstraintViolation();
                            context.buildConstraintViolationWithTemplate(
                                            Month.of(Integer.parseInt(month)).getDisplayName(TextStyle.FULL, Locale.ENGLISH)
                                                    + " " + year + " does not have " + day + " days")
                                    .addConstraintViolation();
                            return false;
                        } else {
                            return true;
                        }
                    }
                } else {
                    return true;
                }
            } else {
                return false;
            }
        } else {
            return false;
        }
    }
}
