package com.kryptos.password.validator;

import com.kryptos.common.AppConstants;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PasswordValidator {

    public boolean isValidPasswordLength(int length, List<String> errors) {
        if (length != 0 && (length < 16 || length > 24)) {
            errors.add("Length must be between 16 and 24");
            return false;
        }
        return true;
    }

    public boolean isValidPassword(String password, List<String> errors) {
        if (password == null || password.isEmpty()) {
            errors.add("Invalid Password. Please enter a valid password.");
            return false;
        }

        boolean isValid = isValidPasswordLength(password.length(), errors);

        if (!AppConstants.UPPER.matcher(password).matches()) {
            errors.add("Missing uppercase letter");
            isValid = false;
        }
        if (!AppConstants.LOWER.matcher(password).matches()) {
            errors.add("Missing lowercase letter");
            isValid = false;
        }
        if (!AppConstants.DIGIT.matcher(password).matches()) {
            errors.add("Missing digit");
            isValid = false;
        }
        if (!AppConstants.SPECIAL_PTN.matcher(password).matches()) {
            errors.add("Missing special character");
            isValid = false;
        }
        return isValid;
    }
}
