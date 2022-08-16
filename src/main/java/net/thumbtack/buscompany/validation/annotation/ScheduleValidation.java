package net.thumbtack.buscompany.validation.annotation;


import net.thumbtack.buscompany.validation.clazz.ClassWithScheduleValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ClassWithScheduleValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ScheduleValidation {
    String message() default "Should only be Schedule or dates field";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
