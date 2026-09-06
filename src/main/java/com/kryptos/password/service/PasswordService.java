package com.kryptos.password.service;

import com.kryptos.common.AppConstants;
import com.kryptos.common.model.Response;
import com.kryptos.common.util.BreachChecker;
import com.kryptos.password.validator.PasswordValidator;
import com.kryptos.slack.service.SlackNotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class PasswordService {

    @Value("${slack.status}")
    private boolean slackStatus;
    @Value("${slack.username}")
    private String slackUsername;

    private final BreachChecker breachChecker;
    private final PasswordValidator passwordValidator;
    private final SlackNotificationService slackNotificationService;

    private final SecureRandom secureRandom = new SecureRandom();
    private final Logger LOGGER = LoggerFactory.getLogger(PasswordService.class);

    public PasswordService(BreachChecker breachChecker, PasswordValidator passwordValidator,
                           SlackNotificationService slackNotificationService) {
        this.breachChecker = breachChecker;
        this.passwordValidator = passwordValidator;
        this.slackNotificationService = slackNotificationService;
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

        if (slackStatus) {
            String userId = slackNotificationService.getSlackUserIdByEmail(slackUsername);
            String message = String.format("Generated Password:: %s", result);
            LOGGER.info("Sending slack message to user {} {}", slackUsername, userId);
            slackNotificationService.sendMessage(userId, message);
        }

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
