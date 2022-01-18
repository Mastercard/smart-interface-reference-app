package com.mastercard.developer.smartinterfacereference;

import com.mastercard.ApiException;
import com.mastercard.developer.smartinterfacereference.service.SmartInterfaceIntegrationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class SmartInterfaceIntegrationRunner implements CommandLineRunner {

    private final SmartInterfaceIntegrationService integrationService;
    private final boolean encryptionEnabled;
    private static final Logger LOGGER = LoggerFactory.getLogger(SmartInterfaceIntegrationRunner.class);

    @Autowired
    public SmartInterfaceIntegrationRunner(SmartInterfaceIntegrationService integrationService,
                                           @Value("${app.encryption.enabled:false}") boolean encryptionEnabled) {
        this.integrationService = integrationService;
        this.encryptionEnabled = encryptionEnabled;
    }

    @Override
    public void run(String... args) throws Exception {

        try {
            integrationService.executeAppFrictionlessFlow();
        } catch (ApiException e){
            LOGGER.error("Error while executing frictionless app flow: {}", e.getResponseBody());
        }

        try {
            integrationService.executeBrowserFrictionlessFlow();
        } catch (ApiException e){
            LOGGER.error("Error while executing frictionless browser flow: {}", e.getResponseBody());
        }

        try {
            if (encryptionEnabled) {
                integrationService.executeFrictionlessFlowWithPanEncryption();
            } else {
                LOGGER.info("Encryption is disabled in application.yml, skipping PAN encryption scenario...");
            }
        } catch (ApiException e){
            LOGGER.error("Error while executing frictionless flow with encryption: {}", e.getResponseBody());
        }

        try {
            integrationService.executeBrowserChallengeFlow();
        } catch (ApiException e){
            LOGGER.error("Error while executing challenge browser flow: {}", e.getResponseBody());
        }

        try {
            integrationService.executeAppChallengeFlow();
        } catch (ApiException e){
            LOGGER.error("Error while executing challenge app flow: {}", e.getResponseBody());
        }

        try {
            integrationService.executeAbandonedChallengeFlow();
        } catch (ApiException e){
            LOGGER.error("Error while executing abandoned challenge flow: {}", e.getResponseBody());
        }

        try {
            integrationService.executeDecoupledChallengeFlow();
        } catch (ApiException e){
            LOGGER.error("Error while executing decoupled challenge flow: {}", e.getResponseBody());
        }
    }

}
