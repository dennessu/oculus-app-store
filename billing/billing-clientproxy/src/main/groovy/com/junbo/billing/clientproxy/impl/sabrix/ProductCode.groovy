/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.billing.clientproxy.impl.sabrix

import groovy.transform.CompileStatic

/**
 * Product code enum.
 */
@CompileStatic
public enum ProductCode {
    DOWNLOADABLE_SOFTWARE('DOWNLOADABLE SOFTWARE'),
    DIGITAL_CONTENT('DIGITAL CONTENT'),
    STORE_BALANCE('STORE BALANCE'),
    PHYSICAL_GOODS('PHYSICAL GOODS'),
    SHIPPING_AND_HANDLING('SHIPPING AND HANDLING')

    String code

    ProductCode(String code) {
        this.code = code
    }
}