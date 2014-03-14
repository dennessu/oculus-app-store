package com.junbo.ewallet.spec.model

import groovy.transform.CompileStatic

/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

/**
 * debit request.
 */
@CompileStatic
class DebitRequest {
    UUID trackingUuid
    Long offerId
    BigDecimal amount
}
