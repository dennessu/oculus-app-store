/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.billing.clientproxy.impl.avalara

/**
 * Created by LinYi on 14-3-10.
 */
class TaxLine {
    String lineNo
    String taxCode
    Boolean taxability
    double taxable
    double rate
    double tax
    double discount
    double taxCalculated
    double exemption
    TaxDetail[] taxDetails
    String boundaryLevel
}
