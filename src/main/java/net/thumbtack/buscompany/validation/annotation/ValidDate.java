package net.thumbtack.buscompany.validation.annotation;

import net.thumbtack.buscompany.validation.clazz.DateValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;


@Documented
@Constraint(validatedBy = DateValidator.class)
@Target({ElementType.TYPE_USE, ElementType.FIELD, ElementType.PARAMETER, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidDate {
    String message() default "Must be a date in the format YYYY-MM-DD";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
