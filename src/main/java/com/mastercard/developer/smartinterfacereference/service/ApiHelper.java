package com.mastercard.developer.smartinterfacereference.service;

import com.mastercard.ApiException;
import com.mastercard.api.model.AbandonedChallenge;
import com.mastercard.api.model.Authentication;
import com.mastercard.api.model.AuthenticationResult;
import com.mastercard.api.model.ChallengeResult;
import com.mastercard.api.model.Error;
import com.mastercard.api.model.SupportedVersion;
import com.mastercard.api.model.SupportedVersionsSearch;

import java.util.List;

public interface ApiHelper {
    List<SupportedVersion> getSupportedVersions(SupportedVersionsSearch supportedVersionsSearch) throws ApiException;
    AuthenticationResult authenticate(Authentication authentication) throws ApiException;
    ChallengeResult getChallengeResults(String threeDSServerTransID) throws ApiException;
    void abandonChallenge(String threeDsServerTransId, AbandonedChallenge abandonedChallenge) throws ApiException;
    Error getError(ApiException ae);
}
