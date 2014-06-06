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
class Line {
    // Line item identifier.
    // LineId uniquely identifies the line item row.
    @JsonProperty('LineNo')
    String lineNo
    // Destination (ship-to) address code.
    // DestinationCode references an address from the Addresses collection.
    @JsonProperty('DestinationCode')
    String destinationCode
    // Origination (ship-from) address code.
    // OriginCode references an address from the Addresses collection.
    @JsonProperty('OriginCode')
    String originCode
    // Item code (e.g. SKU). Strongly recommended.
    @JsonProperty('ItemCode')
    String itemCode
    // Item quantity.
    // The tax engine does NOT use this as a multiplier with price to get the Amount.
    @JsonProperty('Qty')
    BigDecimal qty
    // Total amount of item (extended amount, qty * unit price)
    @JsonProperty('Amount')
    BigDecimal amount
    // Product taxability code of the line item.
    // Can be an AvaTax system tax code, or a custom-defined tax code.
    @JsonProperty('TaxCode')
    String taxCode
    // The client application customer or usage type.
    // CustomerUsageType determines the exempt status of the transaction
    // based on the exemption tax rules for the jurisdictions involved.
    // Can also be referred to as Entity/Use Code.
    @JsonProperty('CustomerUsageType')
    String customerUsageType
    // Item description.
    // Required for customers using our filing service.
    @JsonProperty('Description')
    String description
    // Should be set to true if the document level discount is applied to this line item.
    // Defaults to false.
    @JsonProperty('Discounted')
    Boolean discounted
    // Should be set to true if the tax is already included,
    // and sale amount and tax should be back-calculated from the provided Line.Amount.
    // Defaults to false.
    @JsonProperty('TaxIncluded')
    Boolean taxIncluded
    // Value stored with SalesInvoice DocType that is submitter dependent.
    @JsonProperty('Ref1')
    String ref1
    // Value stored with SalesInvoice DocType that is submitter dependent.
    @JsonProperty('Ref2')
    String ref2

    @Override
    String toString() {
        return 'Line{' +
                "lineNo='" + lineNo + '\'' +
                ", destinationCode='" + destinationCode + '\'' +
                ", originCode='" + originCode + '\'' +
                ", itemCode='" + itemCode + '\'' +
                ', qty=' + qty +
                ', amount=' + amount +
                ", taxCode='" + taxCode + '\'' +
                ", customerUsageType='" + customerUsageType + '\'' +
                ", description='" + description + '\'' +
                ', discounted=' + discounted +
                ', taxIncluded=' + taxIncluded +
                ", ref1='" + ref1 + '\'' +
                ", ref2='" + ref2 + '\'' +
                '}'
    }
}
