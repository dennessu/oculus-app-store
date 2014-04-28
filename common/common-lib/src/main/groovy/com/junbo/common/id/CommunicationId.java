/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.common.id;

/**
 * Created by xiali_000 on 4/21/2014.
 */
@IdResourcePath("/communications/{0}")
public class CommunicationId extends Id {

    public CommunicationId() {}

    public CommunicationId(long value) {
        super(value);
    }
}
