package com.kryptos.controller;

import com.kryptos.common.model.Response;
import com.kryptos.password.service.PasswordService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/password")
public class PasswordController {

    private final PasswordService passwordService;

    private final Logger LOGGER = LoggerFactory.getLogger(PasswordController.class);

    public PasswordController(PasswordService passwordService) {
        this.passwordService = passwordService;
    }

    @GetMapping("/generate")
    public ResponseEntity<Response> generatePassword(@RequestParam(defaultValue = "0") int length,
                                                   @RequestParam(required = false) boolean noSymbols) {
        try {
            Response response = passwordService.generatePassword(length, noSymbols);

            if (response.getErrors() != null && !response.getErrors().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            LOGGER.error("INTERNAL SERVER ERROR {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/is-valid")
    public ResponseEntity<Response> checkPassword(@RequestParam String password) {

        try {
            Response response = passwordService.checkPassword(password);

            if (response.getErrors() != null && !response.getErrors().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception ex) {
            LOGGER.error("INTERNAL SERVER ERROR {}", ex.getMessage(), ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
