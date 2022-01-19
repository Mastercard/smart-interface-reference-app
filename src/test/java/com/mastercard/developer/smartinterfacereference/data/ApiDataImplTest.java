package com.mastercard.developer.smartinterfacereference.data;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mastercard.api.model.Authentication;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

class ApiDataImplTest {

    private ApiDataImpl apiData;

    @BeforeEach
    void setup() {
        apiData = new ApiDataImpl(new ObjectMapper());
    }

    @Test
    void getAuthenticationFrictionlessBrowser_parsesJson() {
        Authentication authentication = apiData.getAuthenticationFrictionlessBrowser();
        assertEquals("02", authentication.getDeviceChannel());
    }

    @Test
    void getAuthenticationFrictionlessApp_parsesJson() {
        Authentication authentication = apiData.getAuthenticationFrictionlessApp();
        assertEquals("01", authentication.getDeviceChannel());
    }

    @Test
    void getAuthenticationChallengeBrowser_parsesJson() {
        Authentication authentication = apiData.getAuthenticationChallengeBrowser();
        assertEquals("02", authentication.getDeviceChannel());
    }

    @Test
    void getAuthenticationChallengeApp_parsesJson() {
        Authentication authentication = apiData.getAuthenticationChallengeApp();
        assertEquals("01", authentication.getDeviceChannel());
    }

    @Test
    void getAuthenticationDecoupledChallenge_parsesJson() {
        Authentication authentication = apiData.getAuthenticationDecoupledChallenge();
        assertEquals("01", authentication.getDeviceChannel());
    }

    @Test
    void getAuthenticationAbandonedChallenge_parsesJson() {
        Authentication authentication = apiData.getAuthenticationAbandonedChallenge();
        assertEquals("02", authentication.getDeviceChannel());
    }

    @Test
    void testLoadInvalidJson() throws IOException {
        ObjectMapper objectMapper = Mockito.mock(ObjectMapper.class);
        apiData = new ApiDataImpl(objectMapper);
        when(objectMapper.readValue(any(InputStream.class), eq(Authentication.class))).thenThrow(IOException.class);
        assertThrows(RuntimeException.class, () -> apiData.getAuthenticationAbandonedChallenge());
    }
}
