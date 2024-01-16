package com.mastercard.developer.smartinterfacereference.config;

import com.mastercard.ApiClient;
import com.mastercard.api.AuthenticationApi;
import com.mastercard.api.SupportedVersionsApi;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.util.Optional;

@Configuration
public class ApiClientConfig {

    @Bean
    ApiClient apiClient(OkHttpClient okHttpClient,
                        @Value("${si.base-path}") String siBasePath) {
        return new ApiClient()
                .setHttpClient(okHttpClient)
                .setBasePath(siBasePath);
    }

    @Bean
    OkHttpClient okHttpClient(Optional<SSLSocketFactory> sslSocketFactory,
                              X509TrustManager trustManager) {
        OkHttpClient.Builder builder =  new OkHttpClient.Builder()
                .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BASIC));

        if (sslSocketFactory.isPresent()) {
            builder = builder.sslSocketFactory(sslSocketFactory.get(), trustManager);
        }
        return builder.build();
    }

    @Bean
    SSLSocketFactory sslSocketFactory(@Value("${si.auth.keyStore}") String keyStoreLoc,
                                      @Value("${si.auth.keyStore-password}") String keyStorePassword,
                                      @Value("${si.auth.keyStore-type}") String keyStoreType,
                                      Optional<X509TrustManager> trustManager) throws GeneralSecurityException , IOException {

        if (keyStoreLoc.isBlank() || keyStorePassword.isBlank()) {
            return null;
        }

        KeyStore keyStore = keyStore(keyStoreLoc, keyStorePassword.toCharArray(),keyStoreType);

        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        trustManagerFactory.init(keyStore);

        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        keyManagerFactory.init(keyStore, keyStorePassword.toCharArray());

        SSLContext sslContext = SSLContext.getInstance("TLSv1.2");

        if(trustManager.isPresent()) {
            sslContext.init(keyManagerFactory.getKeyManagers(), new TrustManager[]{trustManager.get()}, new SecureRandom());
            return sslContext.getSocketFactory();
        } else {
            throw new GeneralSecurityException("X509TrustManager is null. Check that keyStore and keyStore-password are set in config.");
        }
    }

    @Bean
    X509TrustManager x509TrustManager() throws GeneralSecurityException {
        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        trustManagerFactory.init((KeyStore) null);

        for(TrustManager trustManager : trustManagerFactory.getTrustManagers()) {
            if (trustManager instanceof X509TrustManager) {
                return (X509TrustManager) trustManager;
            }
        }

        return null;
    }

    private KeyStore keyStore(String file, char[] password, String keyStoreType) throws GeneralSecurityException, IOException {
        KeyStore keyStore = KeyStore.getInstance(keyStoreType);

        try (InputStream in = ApiClientConfig.class.getResourceAsStream(file)) {
            keyStore.load(in, password);
        }

        return keyStore;
    }

    @Bean
    AuthenticationApi authenticationApi(ApiClient apiClient) {
        return new AuthenticationApi(apiClient);
    }

    @Bean
    SupportedVersionsApi supportedVersionsApi(ApiClient apiClient) {
        return new SupportedVersionsApi(apiClient);
    }
}
