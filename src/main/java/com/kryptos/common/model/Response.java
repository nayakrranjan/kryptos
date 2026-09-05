package com.kryptos.common.model;

import lombok.Data;

import java.util.List;

@Data
public class Response {
    private Object data;
    private List<String> errors;
}
