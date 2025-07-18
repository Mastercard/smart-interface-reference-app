package com.mastercard.developer.smartinterfacereference.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mastercard.ApiException;
import com.mastercard.api.AuthenticationApi;
import com.mastercard.api.SupportedVersionsApi;
import com.mastercard.api.model.AbandonedChallenge;
import com.mastercard.api.model.Authentication;
import com.mastercard.api.model.AuthenticationResult;
import com.mastercard.api.model.ChallengeResult;
import com.mastercard.api.model.SupportedVersion;
import com.mastercard.api.model.SupportedVersionsSearch;
import com.mastercard.developer.smartinterfacereference.data.ApiData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.springframework.test.util.ReflectionTestUtils;
import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SmartInterfaceIntegrationServiceTest {

    @Mock
    private AuthenticationApi authenticationApi;
    @Mock
    private SupportedVersionsApi supportedVersionsApi;
    @Mock
    private ApiData apiData;
    @Mock
    private EncryptionService encryptionService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Captor
    private ArgumentCaptor<Authentication> authenticationCaptor;

    private static final String THREE_DS_SERVER_TRANS_ID = "12345";
    private SmartInterfaceIntegrationService integrationService;

    @Mock
    Logger mockLogger;

    @BeforeEach
    void setUp() throws Exception {
        ApiHelper apiHelper = new ApiHelperImpl(objectMapper, authenticationApi, supportedVersionsApi);
        integrationService = new SmartInterfaceIntegrationService(apiHelper, apiData, encryptionService);
        // Set the logger using ReflectionTestUtils
        setFinalStatic(SmartInterfaceIntegrationService.class.getDeclaredField("LOGGER"), mockLogger);
    }

    static void setFinalStatic(Field field, Object newValue) throws Exception{
        final Field unsafeField = Unsafe.class.getDeclaredField("theUnsafe");
        unsafeField.setAccessible(true);
        Unsafe unsafe = (Unsafe)unsafeField.get(null);
        Object fieldBase = unsafe.staticFieldBase(field);
        long fieldOffset = unsafe.staticFieldOffset(field);
        unsafe.putObject(fieldBase, fieldOffset, newValue);
    }

    @Test
    void executeBrowserFrictionlessFlow() throws Exception {
        SupportedVersionsSearch supportedVersionsSearch = new SupportedVersionsSearch().acctNumber("0000100000007000000");

        SupportedVersion supportedVersion = new SupportedVersion().threeDSServerTransID(THREE_DS_SERVER_TRANS_ID);

        Authentication emptyAuth = new Authentication();

        Authentication authWithInfo = new Authentication()
                .threeDSServerTransID(THREE_DS_SERVER_TRANS_ID)
                .acctNumber("0000100000007000000");

        AuthenticationResult authenticationResult = new AuthenticationResult().transStatus("Y");

        when(apiData.getAuthenticationFrictionlessBrowser()).thenReturn(emptyAuth);

        when(supportedVersionsApi.getSupportedVersions(supportedVersionsSearch)).thenReturn(List.of(supportedVersion));

        when(authenticationApi.authenticate(authWithInfo)).thenReturn(authenticationResult);

        integrationService.executeBrowserFrictionlessFlow();

        verify(authenticationApi).authenticate(authenticationCaptor.capture());
        assertEquals("12345", authenticationCaptor.getValue().getThreeDSServerTransID());
    }

    @Test
    void executeAppFrictionlessFlow() throws Exception {
        SupportedVersionsSearch supportedVersionsSearch = new SupportedVersionsSearch().acctNumber("0000100000007000000");

        SupportedVersion supportedVersion = new SupportedVersion().threeDSServerTransID(THREE_DS_SERVER_TRANS_ID);

        Authentication emptyAuth = new Authentication();

        Authentication authWithInfo = new Authentication()
                .threeDSServerTransID(THREE_DS_SERVER_TRANS_ID)
                .acctNumber("0000100000007000000");

        AuthenticationResult authenticationResult = new AuthenticationResult().transStatus("Y");

        when(apiData.getAuthenticationFrictionlessApp()).thenReturn(emptyAuth);

        when(supportedVersionsApi.getSupportedVersions(supportedVersionsSearch)).thenReturn(List.of(supportedVersion));

        when(authenticationApi.authenticate(authWithInfo)).thenReturn(authenticationResult);

        integrationService.executeAppFrictionlessFlow();

        verify(authenticationApi).authenticate(authenticationCaptor.capture());
        assertEquals("12345", authenticationCaptor.getValue().getThreeDSServerTransID());
    }

    @Test
    void executeBrowserChallengeFlow() throws Exception {
        SupportedVersionsSearch supportedVersionsSearch = new SupportedVersionsSearch().acctNumber("0000100000007000006");

        SupportedVersion supportedVersion = new SupportedVersion().threeDSServerTransID(THREE_DS_SERVER_TRANS_ID);

        Authentication emptyAuth = new Authentication();

        Authentication authWithInfo = new Authentication()
                .threeDSServerTransID(THREE_DS_SERVER_TRANS_ID)
                .acctNumber("0000100000007000006");

        AuthenticationResult authenticationResult = new AuthenticationResult().transStatus(TransStatus.CHALLENGE_EMVCO)
                                                                              .threeDSServerTransID(THREE_DS_SERVER_TRANS_ID);

        when(apiData.getAuthenticationChallengeBrowser()).thenReturn(emptyAuth);

        when(supportedVersionsApi.getSupportedVersions(supportedVersionsSearch)).thenReturn(List.of(supportedVersion));

        when(authenticationApi.authenticate(authWithInfo)).thenReturn(authenticationResult);

        when(authenticationApi.getChallengeResults(THREE_DS_SERVER_TRANS_ID)).thenReturn(new ChallengeResult().transStatus("Y"));

        integrationService.executeBrowserChallengeFlow();

        verify(authenticationApi).authenticate(authenticationCaptor.capture());
        assertEquals("12345", authenticationCaptor.getValue().getThreeDSServerTransID());
    }

    @Test
    void executeBrowserChallengeFlow_succeeds_whenRetryLogicTriggered() throws Exception {
        SupportedVersionsSearch supportedVersionsSearch = new SupportedVersionsSearch().acctNumber("0000100000007000006");

        SupportedVersion supportedVersion = new SupportedVersion().threeDSServerTransID(THREE_DS_SERVER_TRANS_ID);

        Authentication emptyAuth = new Authentication();

        Authentication authWithInfo = new Authentication()
                .threeDSServerTransID(THREE_DS_SERVER_TRANS_ID)
                .acctNumber("0000100000007000006");

        AuthenticationResult authenticationResult = new AuthenticationResult().transStatus(TransStatus.CHALLENGE_EMVCO)
                .threeDSServerTransID(THREE_DS_SERVER_TRANS_ID);

        when(apiData.getAuthenticationChallengeBrowser()).thenReturn(emptyAuth);

        when(supportedVersionsApi.getSupportedVersions(supportedVersionsSearch)).thenReturn(List.of(supportedVersion));

        when(authenticationApi.authenticate(authWithInfo)).thenReturn(authenticationResult);

        when(authenticationApi.getChallengeResults(THREE_DS_SERVER_TRANS_ID))
                .thenThrow(new ApiException("", null, 0, null, "{\"errorDetail\": \"threeDSServerTransID challenge in progress\"}"))
                .thenReturn(new ChallengeResult());

        integrationService.executeBrowserChallengeFlow();

        verify(authenticationApi).authenticate(authenticationCaptor.capture());
        assertEquals("12345", authenticationCaptor.getValue().getThreeDSServerTransID());

        verify(authenticationApi, times(2)).getChallengeResults(THREE_DS_SERVER_TRANS_ID);
    }

    @Test
    void executeAppChallengeFlow_succeeds_whenRetryLogicTriggered() throws Exception {
        SupportedVersionsSearch supportedVersionsSearch = new SupportedVersionsSearch().acctNumber("0000100000007000003");

        SupportedVersion supportedVersion = new SupportedVersion().threeDSServerTransID(THREE_DS_SERVER_TRANS_ID);

        Authentication emptyAuth = new Authentication();

        Authentication authWithInfo = new Authentication()
                .threeDSServerTransID(THREE_DS_SERVER_TRANS_ID)
                .acctNumber("0000100000007000003");

        AuthenticationResult authenticationResult = new AuthenticationResult().transStatus(TransStatus.CHALLENGE_EMVCO)
                .threeDSServerTransID(THREE_DS_SERVER_TRANS_ID);

        when(apiData.getAuthenticationChallengeApp()).thenReturn(emptyAuth);

        when(supportedVersionsApi.getSupportedVersions(supportedVersionsSearch)).thenReturn(List.of(supportedVersion));

        when(authenticationApi.authenticate(authWithInfo)).thenReturn(authenticationResult);

        when(authenticationApi.getChallengeResults(THREE_DS_SERVER_TRANS_ID)).thenReturn(new ChallengeResult().transStatus("Y"));

        integrationService.executeAppChallengeFlow();

        verify(authenticationApi).authenticate(authenticationCaptor.capture());
        assertEquals("12345", authenticationCaptor.getValue().getThreeDSServerTransID());
    }

    @Test
    void executeAppChallengeFlow() throws Exception {
        SupportedVersionsSearch supportedVersionsSearch = new SupportedVersionsSearch().acctNumber("0000100000007000003");

        SupportedVersion supportedVersion = new SupportedVersion().threeDSServerTransID(THREE_DS_SERVER_TRANS_ID);

        Authentication emptyAuth = new Authentication();

        Authentication authWithInfo = new Authentication()
                .threeDSServerTransID(THREE_DS_SERVER_TRANS_ID)
                .acctNumber("0000100000007000003");

        AuthenticationResult authenticationResult = new AuthenticationResult().transStatus(TransStatus.CHALLENGE_EMVCO)
                                                                              .threeDSServerTransID(THREE_DS_SERVER_TRANS_ID);

        when(apiData.getAuthenticationChallengeApp()).thenReturn(emptyAuth);

        when(supportedVersionsApi.getSupportedVersions(supportedVersionsSearch)).thenReturn(List.of(supportedVersion));

        when(authenticationApi.authenticate(authWithInfo)).thenReturn(authenticationResult);

        when(authenticationApi.getChallengeResults(THREE_DS_SERVER_TRANS_ID))
                .thenThrow(new ApiException("", null, 0, null, "{\"errorDetail\": \"threeDSServerTransID challenge in progress\"}"))
                .thenReturn(new ChallengeResult());

        integrationService.executeAppChallengeFlow();

        verify(authenticationApi).authenticate(authenticationCaptor.capture());
        assertEquals("12345", authenticationCaptor.getValue().getThreeDSServerTransID());

        verify(authenticationApi, times(2)).getChallengeResults(THREE_DS_SERVER_TRANS_ID);
    }

    @Test
    void executeDecoupledChallengeFlow_succeeds_whenRetryLogicTriggered() throws Exception {
        SupportedVersionsSearch supportedVersionsSearch = new SupportedVersionsSearch().acctNumber("0000100000007000004");

        SupportedVersion supportedVersion = new SupportedVersion().threeDSServerTransID(THREE_DS_SERVER_TRANS_ID);

        Authentication emptyAuth = new Authentication();

        Authentication authWithInfo = new Authentication()
                .threeDSServerTransID(THREE_DS_SERVER_TRANS_ID)
                .acctNumber("0000100000007000004")
                .threeDSRequestorDecMaxTime("1")
                .threeDSRequestorDecReqInd("Y");

        AuthenticationResult authenticationResult = new AuthenticationResult().transStatus(TransStatus.CHALLENGE_EMVCO)
                                                                              .threeDSServerTransID(THREE_DS_SERVER_TRANS_ID);

        when(apiData.getAuthenticationDecoupledChallenge()).thenReturn(emptyAuth);

        when(supportedVersionsApi.getSupportedVersions(supportedVersionsSearch)).thenReturn(List.of(supportedVersion));

        when(authenticationApi.authenticate(authWithInfo)).thenReturn(authenticationResult);

        when(authenticationApi.getChallengeResults(THREE_DS_SERVER_TRANS_ID)).thenReturn(new ChallengeResult().transStatus("Y"));

        integrationService.executeDecoupledChallengeFlow();

        verify(authenticationApi).authenticate(authenticationCaptor.capture());
        assertEquals("12345", authenticationCaptor.getValue().getThreeDSServerTransID());
    }

    @Test
    void executeDecoupledChallengeFlow() throws Exception {
        SupportedVersionsSearch supportedVersionsSearch = new SupportedVersionsSearch().acctNumber("0000100000007000004");

        SupportedVersion supportedVersion = new SupportedVersion().threeDSServerTransID(THREE_DS_SERVER_TRANS_ID);

        Authentication emptyAuth = new Authentication();

        Authentication authWithInfo = new Authentication()
                .threeDSServerTransID(THREE_DS_SERVER_TRANS_ID)
                .acctNumber("0000100000007000004")
                .threeDSRequestorDecMaxTime("1")
                .threeDSRequestorDecReqInd("Y");

        AuthenticationResult authenticationResult = new AuthenticationResult().transStatus(TransStatus.CHALLENGE_EMVCO)
                                                                              .threeDSServerTransID(THREE_DS_SERVER_TRANS_ID);

        when(apiData.getAuthenticationDecoupledChallenge()).thenReturn(emptyAuth);

        when(supportedVersionsApi.getSupportedVersions(supportedVersionsSearch)).thenReturn(List.of(supportedVersion));

        when(authenticationApi.authenticate(authWithInfo)).thenReturn(authenticationResult);

        when(authenticationApi.getChallengeResults(THREE_DS_SERVER_TRANS_ID))
                .thenThrow(new ApiException("", null, 0, null, "{\"errorDetail\": \"threeDSServerTransID challenge in progress\"}"))
                .thenReturn(new ChallengeResult());

        integrationService.executeDecoupledChallengeFlow();

        verify(authenticationApi).authenticate(authenticationCaptor.capture());
        assertEquals("12345", authenticationCaptor.getValue().getThreeDSServerTransID());

        verify(authenticationApi, times(2)).getChallengeResults(THREE_DS_SERVER_TRANS_ID);
    }

    @Test
    void executeAbandonedChallengeFlow() throws Exception {
        SupportedVersionsSearch supportedVersionsSearch = new SupportedVersionsSearch().acctNumber("0000100000007000005");

        SupportedVersion supportedVersion = new SupportedVersion().threeDSServerTransID(THREE_DS_SERVER_TRANS_ID);

        Authentication emptyAuth = new Authentication();

        Authentication authWithInfo = new Authentication()
                .threeDSServerTransID(THREE_DS_SERVER_TRANS_ID)
                .acctNumber("0000100000007000005");

        AuthenticationResult authenticationResult = new AuthenticationResult().transStatus(TransStatus.CHALLENGE_EMVCO)
                                                                              .threeDSServerTransID(THREE_DS_SERVER_TRANS_ID);

        when(apiData.getAuthenticationAbandonedChallenge()).thenReturn(emptyAuth);

        when(supportedVersionsApi.getSupportedVersions(supportedVersionsSearch)).thenReturn(List.of(supportedVersion));

        when(authenticationApi.authenticate(authWithInfo)).thenReturn(authenticationResult);

        integrationService.executeAbandonedChallengeFlow();

        verify(authenticationApi).authenticate(authenticationCaptor.capture());
        assertEquals("12345", authenticationCaptor.getValue().getThreeDSServerTransID());

        verify(authenticationApi).abandonChallenge(eq("12345"), any(AbandonedChallenge.class));
    }

    @Test
    void executeFrictionlessFlowWithPanEncryption() throws Exception {
        SupportedVersionsSearch supportedVersionsSearch = new SupportedVersionsSearch().encryptedData("encryptedData");
        SupportedVersion supportedVersion = new SupportedVersion().threeDSServerTransID("12345");
        Authentication authWithTxnId = new Authentication()
                .encryptedData("encryptedData")
                .threeDSServerTransID("12345");
        AuthenticationResult authenticationResult = new AuthenticationResult().transStatus("Y");

        when(encryptionService.encryptData("{\"acctNumber\":\"0000100000001000010\"}")).thenReturn("encryptedData");
        when(apiData.getAuthenticationFrictionlessBrowser()).thenReturn(new Authentication());
        when(supportedVersionsApi.getSupportedVersions(supportedVersionsSearch)).thenReturn(List.of(supportedVersion));
        when(authenticationApi.authenticate(authWithTxnId)).thenReturn(authenticationResult);

        integrationService.executeFrictionlessFlowWithPanEncryption();

        verify(authenticationApi).authenticate(authenticationCaptor.capture());
        assertEquals("12345", authenticationCaptor.getValue().getThreeDSServerTransID());
    }

    @Test
    void executeAbandonedChallengeFlow_WhenNoChallenge_ShouldLogError() throws ApiException {
        SupportedVersion supportedVersion = new SupportedVersion().threeDSServerTransID(THREE_DS_SERVER_TRANS_ID);
        Authentication authentication = new Authentication();
        AuthenticationResult authenticationResult = new AuthenticationResult();
        authenticationResult.setTransStatus("Y"); // Authentication successful, no challenge needed

        when(apiData.getAuthenticationAbandonedChallenge()).thenReturn(authentication);
        when(supportedVersionsApi.getSupportedVersions(any(SupportedVersionsSearch.class))).thenReturn(List.of(supportedVersion));
        when(authenticationApi.authenticate(any(Authentication.class))).thenReturn(authenticationResult);

        integrationService.executeAbandonedChallengeFlow();

        verify(mockLogger).error("Flow was not a challenge flow. No challenge was abandoned.");
    }

    @Test
    void processAuthenticationResult_whenNotAuthenticated_shouldLogNotAuthenticatedMessage() {

        AuthenticationResult authenticationResult = new AuthenticationResult();
        String notAuthenticatedStatus = "N";
        authenticationResult.setTransStatus(notAuthenticatedStatus);

        ReflectionTestUtils.invokeMethod(integrationService, "processAuthenticationResult", authenticationResult);

        verify(mockLogger).info("Received transStatus [{}]. NOT Authenticated!", notAuthenticatedStatus);
    }


}