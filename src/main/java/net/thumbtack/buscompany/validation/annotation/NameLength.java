package net.thumbtack.buscompany.validation.annotation;

import net.thumbtack.buscompany.validation.clazz.NameLengthFieldValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = NameLengthFieldValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface NameLength {
    String message() default "Invalid length";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
