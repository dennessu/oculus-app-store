/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.store.rest.purchase;

import com.junbo.langur.core.promise.Promise;

/**
 * The PurchaseStateSerializer class.
 */
public interface TokenProcessor {

    Promise<String> toTokenString(Object obj);

    Promise<Object> toTokenObject(String tokenString, Class cls);
}
