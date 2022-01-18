package com.mastercard.developer.smartinterfacereference.service;

import com.mastercard.ApiException;
import com.mastercard.api.model.AbandonedChallenge;
import com.mastercard.api.model.Authentication;
import com.mastercard.api.model.AuthenticationResult;
import com.mastercard.api.model.ChallengeResult;
import com.mastercard.api.model.Error;
import com.mastercard.api.model.SupportedVersion;
import com.mastercard.api.model.SupportedVersionsSearch;
import com.mastercard.developer.smartinterfacereference.data.ApiData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class SmartInterfaceIntegrationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SmartInterfaceIntegrationService.class);

    private final ApiHelper apiHelper;
    private final ApiData apiData;
    private final EncryptionService encryptionService;

    private static final int NUM_RETRIES = 300;
    private static final long RETRY_INTERVAL = 200L;
    private static final TimeUnit RETRY_INTERVAL_TIME_UNIT = TimeUnit.MILLISECONDS;

    @Autowired
    SmartInterfaceIntegrationService(ApiHelper apiHelper,
                                     ApiData apiData,
                                     EncryptionService encryptionService) {
        this.apiHelper = apiHelper;
        this.apiData = apiData;
        this.encryptionService = encryptionService;
    }

    /**
     * This method walks through the execution of 3DS Frictionless Flow (no challenge) for requests from a browser.
     * The flow by api is SupportedVersions -> Authentication and shows how to see the result of the authentication.
     *
     * @throws ApiException Failure upon calling the API
     */
    public void executeBrowserFrictionlessFlow() throws ApiException {
        logHeader("Beginning Browser Based Frictionless Flow");

        String acctNumber = "0000100000007000000";

        SupportedVersionsSearch supportedVersionsSearch = new SupportedVersionsSearch().acctNumber(acctNumber);
        List<SupportedVersion> supportedVersions = apiHelper.getSupportedVersions(supportedVersionsSearch);
        String threeDSServerTransID = supportedVersions.get(0).getThreeDSServerTransID();

        Authentication authentication = apiData.getAuthenticationFrictionlessBrowser()
                                               .threeDSServerTransID(threeDSServerTransID)
                                               .acctNumber(acctNumber);
        AuthenticationResult authenticationResult = apiHelper.authenticate(authentication);
        processAuthenticationResult(authenticationResult);
    }

    /**
     * This method walks through the execution of 3DS Frictionless Flow (no challenge) for requests from an app.
     * The flow by api is SupportedVersions -> Authentication and shows how to see the result of the authentication.
     *
     * @throws ApiException Failure upon calling the API
     */
    public void executeAppFrictionlessFlow() throws ApiException {
        logHeader("Beginning App Based Frictionless Flow");

        String acctNumber = "0000100000007000000";

        SupportedVersionsSearch supportedVersionsSearch = new SupportedVersionsSearch().acctNumber(acctNumber);
        List<SupportedVersion> supportedVersions = apiHelper.getSupportedVersions(supportedVersionsSearch);
        String threeDSServerTransID = supportedVersions.get(0).getThreeDSServerTransID();

        Authentication authentication = apiData.getAuthenticationFrictionlessApp()
                                               .threeDSServerTransID(threeDSServerTransID)
                                               .acctNumber(acctNumber);
        AuthenticationResult authenticationResult = apiHelper.authenticate(authentication);
        processAuthenticationResult(authenticationResult);
    }

    /**
     * This method walks through the execution of 3DS Challenge Flow for requests from an app.
     * The flow by api is SupportedVersions -> Authentication -> ChallengeResults and shows how to see the result of the authentication.
     * @throws ApiException Failure upon calling the API
     */
    public void executeAppChallengeFlow() throws ApiException {
        logHeader("Beginning App Based Challenge Flow");

        String acctNumber = "0000100000007000003";

        SupportedVersionsSearch supportedVersionsSearch = new SupportedVersionsSearch().acctNumber(acctNumber);
        List<SupportedVersion> supportedVersions = apiHelper.getSupportedVersions(supportedVersionsSearch);
        String threeDSServerTransID = supportedVersions.get(0).getThreeDSServerTransID();

        Authentication authentication = apiData.getAuthenticationChallengeApp()
                                               .threeDSServerTransID(threeDSServerTransID)
                                               .acctNumber(acctNumber);
        AuthenticationResult authenticationResult = apiHelper.authenticate(authentication);
        processAuthenticationResult(authenticationResult);
    }

    /**
     * This method walks through the execution of 3DS Challenge Flow for requests from a browser.
     * The flow by api is SupportedVersions -> Authentication -> ChallengeResults and shows how to see the result of the authentication.
     * @throws ApiException Failure upon calling the API
     */
    public void executeBrowserChallengeFlow() throws ApiException {
        logHeader("Beginning Browser Based Challenge Flow");

        String acctNumber = "0000100000007000006";

        SupportedVersionsSearch supportedVersionsSearch = new SupportedVersionsSearch().acctNumber(acctNumber);
        List<SupportedVersion> supportedVersions = apiHelper.getSupportedVersions(supportedVersionsSearch);
        String threeDSServerTransID = supportedVersions.get(0).getThreeDSServerTransID();

        Authentication authentication = apiData.getAuthenticationChallengeBrowser()
                                               .threeDSServerTransID(threeDSServerTransID)
                                               .acctNumber(acctNumber);
        AuthenticationResult authenticationResult = apiHelper.authenticate(authentication);
        processAuthenticationResult(authenticationResult);
    }

    /**
     * This method walks through the execution of 3DS Challenge Flow for a request that involves an abandoned challenge.
     * The flow by api is SupportedVersions -> Authentication -> Abandon Challenge -> ChallengeResults and shows how to see the result of the authentication.
     * @throws ApiException Failure upon calling the API
     */
    public void executeAbandonedChallengeFlow() throws ApiException {
        logHeader("Beginning Abandoned Challenge Flow");

        String acctNumber = "0000100000007000005";

        SupportedVersionsSearch supportedVersionsSearch = new SupportedVersionsSearch().acctNumber(acctNumber);
        List<SupportedVersion> supportedVersions = apiHelper.getSupportedVersions(supportedVersionsSearch);
        String threeDSServerTransID = supportedVersions.get(0).getThreeDSServerTransID();

        Authentication authentication = apiData.getAuthenticationAbandonedChallenge()
                                               .threeDSServerTransID(threeDSServerTransID)
                                               .acctNumber(acctNumber);
        AuthenticationResult authenticationResult = apiHelper.authenticate(authentication);

        if (TransStatus.isChallenge(authenticationResult.getTransStatus())) {
            apiHelper.abandonChallenge(threeDSServerTransID, new AbandonedChallenge().abandonReasonCode("01"));
            LOGGER.info("Challenge Abandoned. End of flow.");
        } else {
            LOGGER.error("Flow was not a challenge flow. No challenge was abandoned.");
        }

    }

    /**
     * This method walks through the execution of 3DS Challenge Flow for a request that involves a decoupled challenge.
     * The flow by api is SupportedVersions -> Authentication -> ChallengeResults and shows how to see the result of the authentication.
     * @throws ApiException Failure upon calling the API
     */
    public void executeDecoupledChallengeFlow() throws ApiException {
        logHeader("Beginning Decoupled Challenge Flow");

        String acctNumber = "0000100000007000004";

        SupportedVersionsSearch supportedVersionsSearch = new SupportedVersionsSearch().acctNumber(acctNumber);
        List<SupportedVersion> supportedVersions = apiHelper.getSupportedVersions(supportedVersionsSearch);
        String threeDSServerTransID = supportedVersions.get(0).getThreeDSServerTransID();

        // NOTE: this reference application calculates DecMaxTime as 0 minutes.
        // In practice, one should define this to be longer (e.g. 60 minutes)
        String maxDecoupleTimeMinutes = Long.toString(RETRY_INTERVAL_TIME_UNIT.toMinutes(RETRY_INTERVAL*NUM_RETRIES));
        Authentication authentication = apiData.getAuthenticationDecoupledChallenge()
                                               .threeDSServerTransID(threeDSServerTransID)
                                               .acctNumber(acctNumber)
                                               .threeDSRequestorDecReqInd("Y")
                                               .threeDSRequestorDecMaxTime(maxDecoupleTimeMinutes);
        AuthenticationResult authenticationResult = apiHelper.authenticate(authentication);
        processAuthenticationResult(authenticationResult);
    }

    /**
     * This method walks through the execution of 3DS Frictionless Flow (no challenge) for requests from a browser
     * using encryption for the account number.
     * The flow by api is SupportedVersions -> Authentication and shows how to see the result of the authentication.
     *
     * @throws ApiException Failure upon calling the API
     */
    public void executeFrictionlessFlowWithPanEncryption() throws ApiException {
        logHeader("Beginning Browser Based Frictionless Flow with PAN Encryption");

        String unencryptedData = "{\"acctNumber\":\"0000100000001000010\"}";
        String encryptedData = encryptionService.encryptData(unencryptedData);
        SupportedVersionsSearch supportedVersionsSearch = new SupportedVersionsSearch().encryptedData(encryptedData);
        List<SupportedVersion> supportedVersions = apiHelper.getSupportedVersions(supportedVersionsSearch);
        String threeDSServerTransID = supportedVersions.get(0).getThreeDSServerTransID();

        Authentication authentication = apiData.getAuthenticationFrictionlessBrowser()
                                               .threeDSServerTransID(threeDSServerTransID)
                                               .encryptedData(encryptedData);
        AuthenticationResult authenticationResult = apiHelper.authenticate(authentication);
        processAuthenticationResult(authenticationResult);
    }

    private void processAuthenticationResult(AuthenticationResult authenticationResult) {
        String transStatus = authenticationResult.getTransStatus();
        if(TransStatus.isAuthenticated(transStatus)) {
            LOGGER.info("Received transStatus [{}]. Authenticated!", transStatus);
        } else if (TransStatus.isChallenge(transStatus)) {
            processChallengeResult(authenticationResult);
        } else {
            LOGGER.info("Received transStatus [{}]. NOT Authenticated!", transStatus);
        }
    }

    private void processChallengeResult(AuthenticationResult authenticationResult) {
        ChallengeResult challengeResult = null;
        String threeDSServerTransID = authenticationResult.getThreeDSServerTransID();

        for (int i = 0; i < NUM_RETRIES; i++) {
            try {
                challengeResult = apiHelper.getChallengeResults(threeDSServerTransID);
                break;
            } catch (ApiException e) {
                Error error = apiHelper.getError(e);
                if ("threeDSServerTransID challenge in progress".equals(error.getErrorDetail())) {
                    LOGGER.info("Challenge Results not yet available. Retrying in {} millis", RETRY_INTERVAL_TIME_UNIT.toMillis(RETRY_INTERVAL));
                    try {
                        RETRY_INTERVAL_TIME_UNIT.sleep(RETRY_INTERVAL);
                    } catch (InterruptedException ie) {
                        LOGGER.warn("Interrupted, stopping processing...");
                        Thread.currentThread().interrupt();
                        return;
                    }
                } else {
                    LOGGER.error("Error retrieving ChallengeResults object", e);
                    return;
                }
            }
        }

        if (challengeResult == null) {
            LOGGER.error("Maximum number of retries reached. No ChallengeResult was returned.");
            return;
        }

        String transStatus = challengeResult.getTransStatus();
        if (TransStatus.isAuthenticated(transStatus)) {
            LOGGER.info("Received transStatus [{}]. Authenticated!", transStatus);
        } else {
            LOGGER.error("Received transStatus [{}]. NOT Authenticated", transStatus);
        }
    }

    private void logHeader(String headerString) {
        LOGGER.info("\n\n");
        LOGGER.info(headerString);
        LOGGER.info("\n\n");
    }
}
