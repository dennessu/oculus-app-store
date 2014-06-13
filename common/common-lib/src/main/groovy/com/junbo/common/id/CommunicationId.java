/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.common.id;

/**
 * Created by xiali_000 on 4/21/2014.
 */
@IdResourcePath(value = "/communications/{0}",
                resourceType = "communications",
                regex = "/communications/(?<id>[0-9A-Za-z]+)")
public class CommunicationId extends CloudantId {

    public CommunicationId() {}

    public CommunicationId(String value) {
        super(value);
    }
}
