/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.billing.clientproxy.impl.avalara
/**
 *  AvalaraAddress
 */
class AvalaraAddress {
    // Reference code uniquely identifying this address instance.
    String addressCode
    // Address line 1, required if Latitude and Longitude are not provided.
    String line1
    // Address line 2
    String line2
    // Address line 3
    String line3
    // City name, required unless PostalCode is specified and/or Latitude and Longitude are provided.
    String city
    // State, province, or region name.
    // Required unless City is specified and/or Latitude and Longitude are provided.
    String region
    // Postal or ZIP code
    // Required unless City and Region are specified, and/or Latitude and Longitude are provided.
    String postalCode
    // Country code.
    // If not provided, will default to "US".
    String country
    // Output for ValidateAddress only
    String county
    // Output for ValidateAddress only
    String fipsCode
    // Output for ValidateAddress only
    String carrierRoute
    // Output for ValidateAddress only
    String postNet
    // Output for ValidateAddress only
    AddressType addressType
    // Geographic latitude.
    // Input for GetTax only.
    // If Latitude is defined, it is expected that the longitude field will also be provided.
    // Failure to do so will result in operation error.
    BigDecimal latitude
    // Geographic longitude.
    // Input for GetTax only.
    // If Longitude is defined, it is expected that the latitude field will also be provided.
    // Fail to do so will result in operation error.
    BigDecimal longitude
    // AvaTax tax region identifier.
    // Input for GetTax only
    // If a non-zero value is entered into TaxRegionId, other fields will be ignored.
    String taxRegionId
}

