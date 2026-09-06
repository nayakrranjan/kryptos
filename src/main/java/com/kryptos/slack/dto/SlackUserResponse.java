package com.kryptos.slack.dto;

import lombok.Data;

@Data
public class SlackUserResponse {
    private boolean ok;
    private SlackUser user;
}