/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.ewallet.spec.model

import groovy.transform.CompileStatic

/**
 * Transaction Entity. Only for response.
 */
@CompileStatic
class Transaction {
    Long walletId
    String type
    BigDecimal amount
    Long offerId
    Date createdTime
    String createdBy
}
