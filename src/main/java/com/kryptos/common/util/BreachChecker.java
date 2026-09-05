package com.kryptos.common.util;

import com.kryptos.common.AppConstants;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Component
public class BreachChecker {
    private final RestClient restClient;

    public BreachChecker(RestClient restClient) {
        this.restClient = restClient;
    }

    public boolean isPwned(String password) throws Exception {
        String sha1 = sha1Hex(password).toUpperCase();
        String prefix = sha1.substring(0, 5);
        String suffix = sha1.substring(5);

        String response = restClient.get()
                .uri(AppConstants.HIBP_BASE_URL + prefix)
                .retrieve()
                .body(String.class);

        return response.lines()
                .anyMatch(line -> line.split(":")[0].equalsIgnoreCase(suffix));
    }

    private String sha1Hex(String input) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        byte[] digest = md.digest(input.getBytes(StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder();
        for (byte b : digest) sb.append(String.format("%02x", b));
        return sb.toString();
    }
}
