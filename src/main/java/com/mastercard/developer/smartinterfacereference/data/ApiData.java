package com.mastercard.developer.smartinterfacereference.data;

import com.mastercard.api.model.Authentication;

public interface ApiData {

    /**
     * Fetch authentication request object for a frictionless browser request
     * @return The authentication object
     */
    Authentication getAuthenticationFrictionlessBrowser();

    /**
     * Fetch authentication request object for a frictionless app request
     * @return The authentication object
     */
    Authentication getAuthenticationFrictionlessApp();

    /**
     * Fetch authentication request object for a challenge browser request
     * @return The authentication object
     */
    Authentication getAuthenticationChallengeBrowser();

    /**
     * Fetch authentication request object for a challenge app request
     * @return The authentication object
     */
    Authentication getAuthenticationChallengeApp();

    /**
     * Fetch authentication request object for an abandoned challenge request
     * @return The authentication object
     */
    Authentication getAuthenticationAbandonedChallenge();

    /**
     * Fetch authentication request object for a decoupled challenge request
     * @return The authentication object
     */
    Authentication getAuthenticationDecoupledChallenge();
}
