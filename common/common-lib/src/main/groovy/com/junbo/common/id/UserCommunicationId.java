/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.common.id;

/**
 * Created by minhao on 2/13/14.
 */
@IdResourcePath(value = "/opt-ins/{0}",
                resourceType = "opt-ins",
                regex = "/opt-ins/(?<id>[0-9A-Za-z]+)")
public class UserCommunicationId extends CloudantId {

    public UserCommunicationId() {}
    public UserCommunicationId(String value) {
        super(value);
    }
}
