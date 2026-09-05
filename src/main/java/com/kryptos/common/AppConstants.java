package com.kryptos.common;

import java.util.regex.Pattern;

public class AppConstants {
    public static final String HIBP_BASE_URL = "https://api.pwnedpasswords.com/range/";

    public static final String UPPERCASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    public static final String LOWERCASE = "abcdefghijklmnopqrstuvwxyz";
    public static final String DIGITS = "0123456789";
    public static final String SPECIAL = "!@#$%^&*()-_=+[]{}";

    public static final Pattern UPPER = Pattern.compile(".*[A-Z].*");
    public static final Pattern LOWER = Pattern.compile(".*[a-z].*");
    public static final Pattern DIGIT = Pattern.compile(".*\\d.*");
    public static final Pattern SPECIAL_PTN =
            Pattern.compile(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?].*");

}
