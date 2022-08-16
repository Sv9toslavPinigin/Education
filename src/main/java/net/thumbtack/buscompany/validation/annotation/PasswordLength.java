package net.thumbtack.buscompany.validation.annotation;

import net.thumbtack.buscompany.validation.clazz.PasswordLengthFieldValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = PasswordLengthFieldValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface PasswordLength {
    String message() default "Invalid password length";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
