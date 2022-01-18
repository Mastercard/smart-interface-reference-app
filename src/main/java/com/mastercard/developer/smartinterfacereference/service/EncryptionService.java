package com.mastercard.developer.smartinterfacereference.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.mastercard.developer.encryption.JweConfig;
import com.mastercard.developer.encryption.JweConfigBuilder;
import com.mastercard.developer.encryption.JweEncryption;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;

@Service
public class EncryptionService {

    private final String encryptionCertPath;
    private final boolean encryptionEnabled;

    private Certificate certificate;
    private ObjectMapper mapper;

    @Autowired
    EncryptionService(@Value("${app.encryption.cert}") String encryptionCertPath,
                      @Value("${app.encryption.enabled}") boolean encryptionEnabled,
                      ObjectMapper mapper) {
        this.encryptionCertPath = encryptionCertPath;
        this.encryptionEnabled = encryptionEnabled;
        this.mapper = mapper;
    }

    @PostConstruct
    void init(){
        if (encryptionEnabled) {
            certificate = getEncryptionCertificate(encryptionCertPath);
        }
    }

    public String encryptData(String data) {
        if (!encryptionEnabled) {
            throw new EncryptionException("Encryption not enabled", null);
        }
        try {
            JweConfig config = JweConfigBuilder.aJweEncryptionConfig()
                    .withEncryptionPath("$", "$")
                    .withEncryptionCertificate(certificate)
                    .build();

            String encryptedPayload = JweEncryption.encryptPayload(data, config);
            return mapper.readTree(encryptedPayload).get("encryptedData").asText();
        } catch (Exception e) {
            throw new EncryptionException("Error encrypting data", e);
        }
    }

    private static Certificate getEncryptionCertificate(String encryptionCertPath) {
        try {
            CertificateFactory instance = CertificateFactory.getInstance("X.509");
            return instance.generateCertificate(new ClassPathResource(encryptionCertPath).getInputStream());
        } catch (CertificateException | IOException e) {
            throw new EncryptionException("Failed to load encryption certificate", e);
        }
    }

    private static class EncryptionException extends RuntimeException {
        public EncryptionException(String msg, Exception e) {
            super(msg, e);
        }
    }
}
