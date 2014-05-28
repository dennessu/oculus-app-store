/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.billing.clientproxy.impl.sabrix

import com.thoughtworks.xstream.annotations.XStreamAlias
import com.thoughtworks.xstream.annotations.XStreamAsAttribute
import groovy.transform.CompileStatic

/**
 * Batch level input/output for tax calculation.
 */
@CompileStatic
@XStreamAlias("INDATA")
class Batch {
    @XStreamAsAttribute
    @XStreamAlias('USERNAME')
    String username

    @XStreamAsAttribute
    @XStreamAlias('PASSWORD')
    String password

    @XStreamAsAttribute
    @XStreamAlias('EXTERNAL_COMPANY_ID')
    String externalCompanyId

    @XStreamAsAttribute
    @XStreamAlias('COMPANY_ROLE')
    String companyRole

    @XStreamAsAttribute
    @XStreamAlias('INVOICE')
    List<Invoice> invoice

    @XStreamAsAttribute
    @XStreamAlias('REQUEST_STATUS')
    RequestStatus requestStatus
}
