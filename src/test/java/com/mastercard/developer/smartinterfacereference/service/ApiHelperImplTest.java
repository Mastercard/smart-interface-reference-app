package com.mastercard.developer.smartinterfacereference.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.mastercard.ApiException;
import com.mastercard.api.AuthenticationApi;
import com.mastercard.api.SupportedVersionsApi;
import org.junit.jupiter.api.Test;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.mastercard.api.model.Error;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class ApiHelperImplTest {

    @Mock
    private ObjectMapper mockObjectMapper;
    @Mock
    private AuthenticationApi authenticationApi;
    @Mock
    private SupportedVersionsApi supportedVersionsApi;

    @InjectMocks
    private ApiHelperImpl apiHelper;

    @Test
    void getError_shouldThrowRuntimeJsonProcessException_whenJsonProcessingFails() throws JsonProcessingException {
        ApiException mockApiException = mock(ApiException.class);

        String invalidJson = "invalid json";

        when(mockApiException.getResponseBody()).thenReturn(invalidJson);
        doThrow(new JsonProcessingException("Simulated JSON processing error") {})
                .when(mockObjectMapper)
                .readValue(invalidJson, Error.class);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            apiHelper.getError(mockApiException);
        });
        assertEquals("Error building Error", exception.getMessage());
    }

    @Test
    void convertObjectToJsonString_shouldThrowRuntimeJsonProcessException_whenJsonProcessingFails() throws JsonProcessingException {
        Object testObject = new Object();

        when(mockObjectMapper.writeValueAsString(any()))
                .thenThrow(new JsonProcessingException("Simulated JSON processing error") {});

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            ReflectionTestUtils.invokeMethod(apiHelper, "convertObjectToJsonString", testObject);
        });

        assertEquals("Error converting object to string", exception.getMessage());

        verify(mockObjectMapper).writeValueAsString(testObject);
    }
}

