package com.junbo.order.clientproxy.identity

import com.junbo.langur.core.promise.Promise

/**
 * Interface for Country Facade.
 */
public interface CountryFacade {
    Promise<com.junbo.identity.spec.v1.model.Country> getCountry(String country)
}