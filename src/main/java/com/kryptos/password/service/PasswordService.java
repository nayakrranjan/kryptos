package com.kryptos.password.service;

import com.kryptos.common.AppConstants;
import com.kryptos.common.model.Response;
import com.kryptos.common.util.BreachChecker;
import com.kryptos.password.validator.PasswordValidator;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class PasswordService {
    private final BreachChecker breachChecker;
    private final PasswordValidator passwordValidator;
    private final SecureRandom secureRandom = new SecureRandom();

    public PasswordService(BreachChecker breachChecker, PasswordValidator passwordValidator) {
        this.breachChecker = breachChecker;
        this.passwordValidator = passwordValidator;
    }

    public Response generatePassword(int length, boolean noSymbols) {
        Response response = new Response();
        List<String> errors = new ArrayList<>();

        if (!passwordValidator.isValidPasswordLength(length, errors)) {
            response.setErrors(errors);
            return response;
        }

        final int MIN_LENGTH = 16;
        final int MAX_LENGTH = 24;
        length = length == 0 ? secureRandom.nextInt(MAX_LENGTH - MIN_LENGTH + 1) + MIN_LENGTH
                : length;

        final String ALL_CHARACTERS =
                AppConstants.UPPERCASE + AppConstants.LOWERCASE + AppConstants.DIGITS +
                (noSymbols ? "" : AppConstants.SPECIAL);

        List<Character> password = new ArrayList<>();

        // Guarantee required character types
        password.add(randomCharacter(AppConstants.UPPERCASE));
        password.add(randomCharacter(AppConstants.LOWERCASE));
        password.add(randomCharacter(AppConstants.DIGITS));
        if (!noSymbols) password.add(randomCharacter(AppConstants.SPECIAL));

        // Fill remaining characters
        while (password.size() < length) password.add(randomCharacter(ALL_CHARACTERS));
        // Shuffle so required characters aren't predictable
        Collections.shuffle(password, secureRandom);

        StringBuilder result = new StringBuilder(length);
        password.forEach(result::append);
        response.setData(result.toString());

        return  response;
    }

    private char randomCharacter(String characters) {
        return characters.charAt(
                secureRandom.nextInt(characters.length())
        );
    }

    public Response checkPassword(String password) throws Exception{
        Response response = new Response();
        List<String> errors = new ArrayList<>();

        if (!passwordValidator.isValidPassword(password, errors)) {
            response.setErrors(errors);
            response.setData(false);
            return response;
        }

        if (breachChecker.isPwned(password)) {
            errors.add("Password Found in breach database");
            response.setErrors(errors);
            response.setData(false);
            return response;
        }

        response.setData(true);
        return response;
    }
}
