package com.kryptos.slack.dto;

import lombok.Data;

@Data
public class SlackUser {
    private String id;
    private String name;
    private String real_name;
    private SlackUserProfile profile;
}