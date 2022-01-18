package com.mastercard.developer.smartinterfacereference.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class EncryptionServiceTest {

    private EncryptionService encryptionService;

    @BeforeEach
    void setUp() {
        ObjectMapper mapper = new ObjectMapper();
        encryptionService = new EncryptionService("encryption/enc.unittests.crt", true, mapper);
        encryptionService.init();
    }

    @Test
    void encryptData() throws Exception {
        String encryptedData = encryptionService.encryptData("{\"test\":\"value\"}");
        assertNotNull(encryptedData);
    }
}