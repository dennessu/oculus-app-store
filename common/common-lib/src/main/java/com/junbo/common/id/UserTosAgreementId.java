/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.common.id;

/**
 * Created by minhao on 2/13/14.
 */
@IdResourcePath("/users/{userId}/tos-agreements/{0}")
public class UserTosAgreementId extends Id {

    public UserTosAgreementId() {}
    public UserTosAgreementId(long value) {
        super(value);
    }
}
