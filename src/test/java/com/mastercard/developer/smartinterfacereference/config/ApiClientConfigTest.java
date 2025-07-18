package com.mastercard.developer.smartinterfacereference.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Optional;
import okhttp3.OkHttpClient;
import com.mastercard.ApiClient;
import com.mastercard.api.AuthenticationApi;
import com.mastercard.api.SupportedVersionsApi;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ApiClientConfigTest {

    private ApiClientConfig apiClientConfig;

    @Mock
    private SSLSocketFactory sslSocketFactory;


    private X509TrustManager trustManager;

    @Mock
    private ApiClient apiClient;

   @BeforeEach
    void setUp() {
        apiClientConfig = new ApiClientConfig();
        // Create a test implementation of X509TrustManager
        trustManager = new X509TrustManager() {

            @Override
            public void checkServerTrusted(X509Certificate[] chain, String authType) {
                // This method is empty for Testing
            }

            @Override
            public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                // This method is empty for Testing
            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0]; // Return empty array instead of null
            }
        };
    }

    @Test
    void testApiClient() {
        OkHttpClient okHttpClient = new OkHttpClient();
        String basePath = "https://test-api.mastercard.com";

        ApiClient result = apiClientConfig.apiClient(okHttpClient, basePath);

        assertNotNull(result);
        assertEquals(basePath, result.getBasePath());
    }

    @Test
    void testOkHttpClientWithSSL() {
        Optional<SSLSocketFactory> sslFactory = Optional.of(sslSocketFactory);

        OkHttpClient result = apiClientConfig.okHttpClient(sslFactory, trustManager);

        assertNotNull(result);
    }

    @Test
    void testOkHttpClientWithoutSSL() {
        Optional<SSLSocketFactory> sslFactory = Optional.empty();

        OkHttpClient result = apiClientConfig.okHttpClient(sslFactory, trustManager);

        assertNotNull(result);
    }

    @Test
    void testSslSocketFactorySuccess() throws GeneralSecurityException, IOException {
        String keyStoreLoc = "/test-keystore.p12";
        String keyStorePassword = "password";
        String keyStoreType = "PKCS12";
        Optional<X509TrustManager> trustManagerOptional = Optional.of(trustManager);

        SSLSocketFactory result = apiClientConfig.sslSocketFactory(
                keyStoreLoc,
                keyStorePassword,
                keyStoreType,
                trustManagerOptional
        );

        assertNotNull(result);
    }

    @Test
    void testSslSocketFactoryWithBlankKeystore() throws GeneralSecurityException, IOException {
        String keyStoreLoc = "";
        String keyStorePassword = "";
        String keyStoreType = "PKCS12";
        Optional<X509TrustManager> trustManagerOptional = Optional.of(trustManager);

        SSLSocketFactory result = apiClientConfig.sslSocketFactory(
                keyStoreLoc,
                keyStorePassword,
                keyStoreType,
                trustManagerOptional
        );

        assertNull(result);
    }

    @Test
    void testSslSocketFactoryWithoutTrustManager() {
        String keyStoreLoc = "/test-keystore.p12";
        String keyStorePassword = "password";
        String keyStoreType = "PKCS12";
        Optional<X509TrustManager> trustManagerOptional = Optional.empty();

        assertThrows(GeneralSecurityException.class, () -> {
            apiClientConfig.sslSocketFactory(
                    keyStoreLoc,
                    keyStorePassword,
                    keyStoreType,
                    trustManagerOptional
            );
        });
    }

    @Test
    void testX509TrustManager() throws GeneralSecurityException {
        X509TrustManager result = apiClientConfig.x509TrustManager();
        assertNotNull(result);
    }

    @Test
    void testAuthenticationApi() {
        AuthenticationApi result = apiClientConfig.authenticationApi(apiClient);
        assertNotNull(result);
    }

    @Test
    void testSupportedVersionsApi() {
        SupportedVersionsApi result = apiClientConfig.supportedVersionsApi(apiClient);
        assertNotNull(result);
    }


}

