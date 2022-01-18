package com.mastercard.developer.smartinterfacereference.service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Definitions for transStatus which is used in the authentication results
 */
public class TransStatus {

    public static final String CHALLENGE_EMVCO = "C";
    public static final String DECOUPLED = "D";
    public static final String AUTHENTICATED = "Y";

    private static final Set<String> CHALLENGES = new HashSet<>(Arrays.asList(CHALLENGE_EMVCO, DECOUPLED));

    private TransStatus(){
    }

    public static boolean isChallenge(String transStatus) {
        return CHALLENGES.contains(transStatus);
    }

    public static boolean isAuthenticated(String transStatus) {
        return AUTHENTICATED.equals(transStatus);
    }
}
