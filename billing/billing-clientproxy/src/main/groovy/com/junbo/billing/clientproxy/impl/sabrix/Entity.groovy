package com.junbo.billing.clientproxy.impl.sabrix

import groovy.transform.CompileStatic

/**
 * Entity Enum.
 */
@CompileStatic
public enum Entity {
    US_ENTITY('US', '139')

    String country
    String externalCompanyId

    Entity(String country, String externalCompanyId) {
        this.country = country
        this.externalCompanyId = externalCompanyId
    }
}