package com.mastercard.developer.smartinterfacereference;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.beans.factory.annotation.Autowired;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
@SpringBootTest
class SmartInterfaceReferenceApplicationTests {

    @Autowired
    private ApplicationContext applicationContext;
    @Test
    void testContextLoads() {
        assertNotNull(applicationContext, "Application context should not be null");
    }

    @Test
    void testMainMethodStartsApplication() {
        assertDoesNotThrow(() -> {
            SmartInterfaceReferenceApplication.main(new String[]{});
        }, "Main method should execute without throwing any exceptions");
    }
}