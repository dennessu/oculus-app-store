/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.billing.clientproxy.impl.sabrix

import groovy.transform.CompileStatic

/**
 * Configuration class for Sabrix.
 */
@CompileStatic
class SabrixConfiguration {
    String companyRole
    String externalCompanyId
    String username
    String password

    String shipFromCity
    String shipFromState
    String shipFromPostalCode
    String shipFromCountry
}