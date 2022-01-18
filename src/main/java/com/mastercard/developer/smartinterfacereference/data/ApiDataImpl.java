package com.mastercard.developer.smartinterfacereference.data;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mastercard.api.model.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class ApiDataImpl implements ApiData {

    private final ObjectMapper objectMapper;

    @Autowired
    public ApiDataImpl(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public Authentication getAuthenticationFrictionlessBrowser() {
        return fromJson("json/browser/authentication-frictionless.json", Authentication.class);
    }

    public Authentication getAuthenticationFrictionlessApp() {
        return fromJson("json/app/authentication-frictionless.json", Authentication.class);
    }

    public Authentication getAuthenticationChallengeBrowser() {
        return fromJson("json/browser/authentication-challenge.json", Authentication.class);
    }

    public Authentication getAuthenticationChallengeApp() {
        return fromJson("json/app/authentication-challenge.json", Authentication.class);

    }

    public Authentication getAuthenticationAbandonedChallenge() {
        return fromJson("json/app/authentication-abandoned-challenge.json", Authentication.class);
    }

    public Authentication getAuthenticationDecoupledChallenge() {
        return fromJson("json/app/authentication-decoupled.json", Authentication.class);
    }

    private <T> T fromJson(String resourcePath, Class<T> clazz) {
        try {
            return objectMapper.readValue(getClass().getClassLoader().getResourceAsStream(resourcePath), clazz);
        } catch (IOException ioe) {
            throw new RuntimeIOException(String.format("Error loading resource %s", resourcePath), ioe);
        }
    }

    private static class RuntimeIOException extends RuntimeException {
        public RuntimeIOException(String msg, IOException ioe) {
            super(msg, ioe);
        }
    }
}
