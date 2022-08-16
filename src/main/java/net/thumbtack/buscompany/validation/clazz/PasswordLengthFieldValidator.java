package net.thumbtack.buscompany.validation.clazz;

import net.thumbtack.buscompany.validation.annotation.PasswordLength;
import org.springframework.beans.factory.annotation.Value;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PasswordLengthFieldValidator implements ConstraintValidator<PasswordLength, String> {

    @Value("${min_password_length}")
    private int minPasswdLength;

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        if (s.length() < minPasswdLength) {
            constraintValidatorContext.disableDefaultConstraintViolation();
            constraintValidatorContext.buildConstraintViolationWithTemplate("Length should be more " + minPasswdLength).addConstraintViolation();
            return false;
        }
        return true;
    }
}
