/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.ewallet.spec.model

/**
 * credit request.
 */
class CreditRequest {
    Long offerId
    BigDecimal amount
    String type
    Date expirationDate
}
