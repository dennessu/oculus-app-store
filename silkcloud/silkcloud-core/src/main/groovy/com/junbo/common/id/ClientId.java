/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.common.id;

/**
 * Created by minhao on 2/13/14.
 */
@IdResourcePath(value = "/clients/{0}",
                resourceType = "clients",
                regex = "/clients/(?<id>[0-9A-Za-z]+)")
public class ClientId extends CloudantId {
    public ClientId() {}

    public ClientId(String value) {
        super(value);
    }
}

