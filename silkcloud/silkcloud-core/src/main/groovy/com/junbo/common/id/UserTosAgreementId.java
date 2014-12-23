/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.common.id;

/**
 * Created by minhao on 2/13/14.
 */
@IdResourcePath(value = "/tos-agreements/{0}",
                resourceType = "tos-agreements",
                regex = "/tos-agreements/(?<id>[0-9A-Za-z]+)")
public class UserTosAgreementId extends CloudantId {

    public UserTosAgreementId() {}
    public UserTosAgreementId(String value) {
        super(value);
    }
}
