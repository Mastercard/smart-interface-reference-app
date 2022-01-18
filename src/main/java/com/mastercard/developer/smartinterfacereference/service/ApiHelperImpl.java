package com.mastercard.developer.smartinterfacereference.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mastercard.ApiException;
import com.mastercard.api.AuthenticationApi;
import com.mastercard.api.SupportedVersionsApi;
import com.mastercard.api.model.AbandonedChallenge;
import com.mastercard.api.model.Authentication;
import com.mastercard.api.model.AuthenticationResult;
import com.mastercard.api.model.ChallengeResult;
import com.mastercard.api.model.Error;
import com.mastercard.api.model.SupportedVersion;
import com.mastercard.api.model.SupportedVersionsSearch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Helper class for calling APIs that logs the request and response, while also
 * converting checked exceptions to runtime exceptions.
 */
@Component
public class ApiHelperImpl implements ApiHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApiHelperImpl.class);

    private final ObjectMapper objectMapper;
    private final AuthenticationApi authenticationApi;
    private final SupportedVersionsApi supportedVersionsApi;

    @Autowired
    ApiHelperImpl(ObjectMapper objectMapper,
                  AuthenticationApi authenticationApi,
                  SupportedVersionsApi supportedVersionsApi) {
        this.objectMapper = objectMapper;
        this.authenticationApi = authenticationApi;
        this.supportedVersionsApi = supportedVersionsApi;
    }

    @Override
    public List<SupportedVersion> getSupportedVersions(SupportedVersionsSearch supportedVersionsSearch) throws ApiException {
        logRequest(supportedVersionsSearch);
        List<SupportedVersion> supportedVersions = supportedVersionsApi.getSupportedVersions(supportedVersionsSearch);
        logResponse(supportedVersions);
        return supportedVersions;
    }

    @Override
    public AuthenticationResult authenticate(Authentication authentication) throws ApiException {
        logRequest(authentication);
        AuthenticationResult authenticationResult = authenticationApi.authenticate(authentication);
        logResponse(authenticationResult);
        return authenticationResult;
    }

    @Override
    public void abandonChallenge(String threeDsServerTransId, AbandonedChallenge abandonedChallenge) throws ApiException {
        logRequest(abandonedChallenge);
        authenticationApi.abandonChallenge(threeDsServerTransId, abandonedChallenge);
    }

    @Override
    public ChallengeResult getChallengeResults(String threeDSServerTransID) throws ApiException {
        return authenticationApi.getChallengeResults(threeDSServerTransID);
    }

    @Override
    public Error getError(ApiException ae) {
        try {
            return objectMapper.readValue(ae.getResponseBody(), Error.class);
        } catch (JsonProcessingException jpe) {
            throw new RuntimeJsonProcessException("Error building Error", jpe);
        }
    }

    private void logRequest(Object request) {
        if(LOGGER.isInfoEnabled()) {
            LOGGER.info("Sending request: {}", convertObjectToJsonString(request));
        }
    }

    private void logResponse(Object response) {
        if(LOGGER.isInfoEnabled()) {
            LOGGER.info("Received Response: {}", convertObjectToJsonString(response));
        }
    }

    private String convertObjectToJsonString(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException jpe) {
            throw new RuntimeJsonProcessException("Error converting object to string", jpe);
        }
    }

    private static class RuntimeJsonProcessException extends RuntimeException {
        public RuntimeJsonProcessException(String msg, JsonProcessingException jpe) {
            super(msg, jpe);
        }
    }
}
