package com.mastercard.developer.smartinterfacereference;

import com.mastercard.ApiException;
import com.mastercard.developer.smartinterfacereference.service.SmartInterfaceIntegrationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SmartInterfaceIntegrationRunnerTest {

    @Mock
    private SmartInterfaceIntegrationService integrationService;

    @Test
    void run_executesAllFlows_withEncryptionEnabled() throws Exception {
        new SmartInterfaceIntegrationRunner(integrationService, true).run((String)null);
        verifyAllWithEncryption();
    }

    @Test
    void run_executesAllFlows_withEncryptionDisabled() throws Exception {
        new SmartInterfaceIntegrationRunner(integrationService, false).run((String)null);
        verifyAllWithoutEncryption();
    }
    
    @Test
    void run_executeAllFlows_withException_withEncryptionEnabled() throws Exception {
        doThrow(ApiException.class).when(integrationService).executeAppChallengeFlow();
        doThrow(ApiException.class).when(integrationService).executeAppFrictionlessFlow();
        doThrow(ApiException.class).when(integrationService).executeBrowserChallengeFlow();
        doThrow(ApiException.class).when(integrationService).executeBrowserFrictionlessFlow();
        doThrow(ApiException.class).when(integrationService).executeDecoupledChallengeFlow();
        doThrow(ApiException.class).when(integrationService).executeFrictionlessFlowWithPanEncryption();
        doThrow(ApiException.class).when(integrationService).executeAbandonedChallengeFlow();
        new SmartInterfaceIntegrationRunner(integrationService, true).run((String)null);
        verifyAllWithEncryption();
    }

    @Test
    void run_executeAllFlows_withException_withEncryptionDisabled() throws Exception {
        doThrow(ApiException.class).when(integrationService).executeAppChallengeFlow();
        doThrow(ApiException.class).when(integrationService).executeAppFrictionlessFlow();
        doThrow(ApiException.class).when(integrationService).executeBrowserChallengeFlow();
        doThrow(ApiException.class).when(integrationService).executeBrowserFrictionlessFlow();
        doThrow(ApiException.class).when(integrationService).executeDecoupledChallengeFlow();
        doThrow(ApiException.class).when(integrationService).executeAbandonedChallengeFlow();
        new SmartInterfaceIntegrationRunner(integrationService, false).run((String)null);
        verifyAllWithoutEncryption();
    }

    private void verifyAllWithEncryption() throws Exception {
        verify(integrationService).executeAppChallengeFlow();
        verify(integrationService).executeAppFrictionlessFlow();
        verify(integrationService).executeBrowserChallengeFlow();
        verify(integrationService).executeBrowserFrictionlessFlow();
        verify(integrationService).executeDecoupledChallengeFlow();
        verify(integrationService).executeFrictionlessFlowWithPanEncryption();
        verify(integrationService).executeAbandonedChallengeFlow();
    }

    private void verifyAllWithoutEncryption() throws Exception {
        verify(integrationService).executeAppChallengeFlow();
        verify(integrationService).executeAppFrictionlessFlow();
        verify(integrationService).executeBrowserChallengeFlow();
        verify(integrationService).executeBrowserFrictionlessFlow();
        verify(integrationService).executeDecoupledChallengeFlow();
        verify(integrationService).executeAbandonedChallengeFlow();
    }
}