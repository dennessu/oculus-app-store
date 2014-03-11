/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.billing.clientproxy.impl.avalara

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Created by LinYi on 14-3-10.
 */
class TaxAddress {
    @JsonProperty('Address')
    String address
    @JsonProperty('AddressCode')
    String addressCode
    @JsonProperty('City')
    String city
    @JsonProperty('Region')
    String region
    @JsonProperty('Country')
    String country
    @JsonProperty('PostalCode')
    String postalCode
    @JsonProperty('Latitude')
    String latitude
    @JsonProperty('Longitude')
    String longitude
    @JsonProperty('TaxRegionId')
    String taxRegionId
    @JsonProperty('JurisCode')
    String jurisCode
}
