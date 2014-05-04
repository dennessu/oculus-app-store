/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.billing.clientproxy.impl.avalara

import com.fasterxml.jackson.annotation.JsonProperty
import groovy.transform.CompileStatic

/**
 * Created by LinYi on 14-3-10.
 */
@CompileStatic
class TaxAddress {
    // Canonical street address
    @JsonProperty('Address')
    String address
    // Reference code uniquely identifying this address instance.
    // Will always correspond to an address code supplied to in the address collection provided in the request.
    @JsonProperty('AddressCode')
    String addressCode
    // City name
    @JsonProperty('City')
    String city
    // State or region name
    @JsonProperty('Region')
    String region
    // Country code, as ISO 3166-1 (Alpha-2) country code
    @JsonProperty('Country')
    String country
    // Postal code
    @JsonProperty('PostalCode')
    String postalCode
    // Geographic latitude.
    // Latitude is only defined if input address was a geographic point.
    @JsonProperty('Latitude')
    String latitude
    // Geographic longitude.
    // Longitude is only defined if input address was a geographic point.
    @JsonProperty('Longitude')
    String longitude
    // AvaTax tax region identifier
    @JsonProperty('TaxRegionId')
    String taxRegionId
    // Tax jurisdiction code
    @JsonProperty('JurisCode')
    String jurisCode
}
