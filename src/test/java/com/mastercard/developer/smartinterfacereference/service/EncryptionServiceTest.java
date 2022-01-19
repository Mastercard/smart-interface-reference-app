package com.mastercard.developer.smartinterfacereference.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mastercard.developer.smartinterfacereference.service.EncryptionService.EncryptionException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EncryptionServiceTest {

    private EncryptionService encryptionService;
    private ObjectMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new ObjectMapper();
    }

    @Test
    void encryptDataEncryptionDisabled() {
        encryptionService = new EncryptionService("encryption/enc.unittests.crt", false, mapper);
        encryptionService.init();

        assertThrows(EncryptionException.class, () -> {
            encryptionService.encryptData("{\"test\":\"value\"}");
        });
    }

    @Test
    void encryptData() {
        encryptionService = new EncryptionService("encryption/enc.unittests.crt", true, mapper);
        encryptionService.init();
        String encryptedData = encryptionService.encryptData("{\"test\":\"value\"}");
        assertNotNull(encryptedData);
    }

    @Test
    void encryptDataInvalidCert() {
        encryptionService = new EncryptionService("invalid", true, mapper);
        assertThrows(EncryptionException.class, () -> {
            encryptionService.init();
        });
    }

    @Test
    void encryptDataInvalidData() {
        encryptionService = new EncryptionService("encryption/enc.unittests.crt", true, mapper);
        encryptionService.init();

        assertThrows(EncryptionException.class, () -> {
            encryptionService.encryptData("invalid");
        });
    }
}