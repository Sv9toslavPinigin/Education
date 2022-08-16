package net.thumbtack.buscompany.validation.clazz;

import net.thumbtack.buscompany.validation.annotation.NameLength;
import org.springframework.beans.factory.annotation.Value;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class NameLengthFieldValidator implements ConstraintValidator<NameLength, String> {

    @Value("${max_name_length}")
    private int maxLength;


    @Override
    public void initialize(NameLength constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        if (s == null) {
            return true;
        }
        if (s.length() > maxLength) {
            constraintValidatorContext.disableDefaultConstraintViolation();
            constraintValidatorContext
                    .buildConstraintViolationWithTemplate("Length must be less " + maxLength)
                    .addConstraintViolation();
            return false;
        }
        return true;
    }
}
