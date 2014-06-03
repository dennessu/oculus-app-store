/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.billing.clientproxy.impl.sabrix

import com.thoughtworks.xstream.annotations.XStreamAlias
import com.thoughtworks.xstream.annotations.XStreamAsAttribute
import com.thoughtworks.xstream.annotations.XStreamImplicit
import groovy.transform.CompileStatic

/**
 * Batch level output for tax calculation.
 */
@CompileStatic
@XStreamAlias("OUTDATA")
class TaxCalculationResponse {
    @XStreamAlias('REQUEST_STATUS')
    RequestStatus requestStatus

    @XStreamImplicit(itemFieldName="INVOICE")
    List<Invoice> invoice

    @XStreamAsAttribute
    @XStreamAlias('version')
    String version
}
