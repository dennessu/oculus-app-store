/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.billing.clientproxy.impl.avalara

import groovy.transform.CompileStatic

/**
 * Created by LinYi on 14-3-10.
 */
@CompileStatic
class AvalaraConfiguration {
    String baseUrl
    String companyCode
    String customerCode
    String detailLevel
    String authorization
    String shipFromStreet
    String shipFromCity
    String shipFromState
    String shipFromPostalCode
    String shipFromCountry
}