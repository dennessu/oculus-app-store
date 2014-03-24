package com.junbo.billing.clientproxy.impl.avalara

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Created by LinYi on 14-3-24.
 */
class ValidatedAddress {
    // Address line 1
    @JsonProperty('Line1')
    String line1
    // Address line 2
    @JsonProperty('Line2')
    String line2
    // Address line 3
    @JsonProperty('Line3')
    String line3
    // City name, required unless PostalCode is specified and/or Latitude and Longitude are provided.
    @JsonProperty('City')
    String city
    // State, province, or region name.
    // Required unless City is specified and/or Latitude and Longitude are provided.
    @JsonProperty('Region')
    String region
    // Postal or ZIP code
    // Required unless City and Region are specified, and/or Latitude and Longitude are provided.
    @JsonProperty('PostalCode')
    String postalCode
    // Country code.
    // If not provided, will default to "US".
    @JsonProperty('Country')
    String country
    // Address type code.
    @JsonProperty('AddressType')
    String addressType
    // Geographic latitude.
    // Input for GetTax only.
    // If Latitude is defined, it is expected that the longitude field will also be provided.
    // Failure to do so will result in operation error.
    @JsonProperty('Latitude')
    BigDecimal latitude
    // Geographic longitude.
    // Input for GetTax only.
    // If Longitude is defined, it is expected that the latitude field will also be provided.
    // Fail to do so will result in operation error.
    @JsonProperty('Longitude')
    BigDecimal longitude
    // AvaTax tax region identifier.
    // Input for GetTax only
    // If a non-zero value is entered into TaxRegionId, other fields will be ignored.
    @JsonProperty('TaxRegionId')
    String taxRegionId
    // FIPSCode is a unique 10-digit code representing each geographic combination of state, county, and city.
    // The code is made up of the Federal Information Processing Code (FIPS)
    // that uniquely identifies each state, county, and city in the U.S.
    // Returned for US addresses only.
    @JsonProperty('FipsCode')
    String fipsCode
    // CarrierRoute is a four-character string representing a US postal carrier route.
    // The first character of this property, the term, is always alphabetic, and the last three numeric.
    // For example, "R001" or "C027" would be typical carrier routes.
    // The alphabetic letter indicates the type of delivery associated with this address.
    // Returned for US addresses only.
    @JsonProperty('CarrierRoute')
    String carrierRoute
    // POSTNet is a 12-digit barcode containing the ZIP Code, ZIP+4 Code, and the delivery point code,
    // used by the USPS to direct mail. Returned for US addresses only digits represent delivery information:
    //    1-5 ZIP code
    //    6-9 Plus4 code
    //    10-11 Delivery point
    //    12 Check digit
    @JsonProperty('PostNet')
    String postNet

    @JsonProperty('County')
    String county
}
