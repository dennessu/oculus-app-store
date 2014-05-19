package com.junbo.ewallet.spec.model

import groovy.transform.CompileStatic

/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

/**
 * Refund Request.
 */
@CompileStatic
class RefundRequest {
    UUID trackingUuid
    Long transactionId
    BigDecimal amount
}
