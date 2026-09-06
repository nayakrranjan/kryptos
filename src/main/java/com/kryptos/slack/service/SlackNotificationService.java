package com.kryptos.slack.service;

import com.kryptos.slack.dto.SlackUserResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Service
public class SlackNotificationService {

    private final RestClient restClient;
    private final Logger LOGGER = LoggerFactory.getLogger(SlackNotificationService.class);

    public SlackNotificationService(@Value("${slack.token}") String token) {

        this.restClient = RestClient.builder().baseUrl("https://slack.com/api").defaultHeader(
                HttpHeaders.AUTHORIZATION, "Bearer " + token).defaultHeader(
                HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE).build();
    }

    public void sendMessage(String slackUserId, String message) {
        var response = restClient.post().uri("/chat.postMessage")
                .body(Map.of("channel", slackUserId, "text", message))
                .retrieve().body(String.class);
        LOGGER.info("Slack Message Response: {}", response);
    }

    public String getSlackUserIdByEmail(String email) {
        SlackUserResponse response = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/users.lookupByEmail")
                        .queryParam("email", email)
                        .build())
                .retrieve().body(SlackUserResponse.class);
        LOGGER.info("User Response for {}: {}", email, response);
        return response.getUser().getId();
    }
}
