/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.common.id;

/**
 * EmailId.
 */
@IdResourcePath("/emails/{0}")
public class EmailId extends Id {
    public EmailId() {}

    public EmailId(Long value) {
        super(value);
    }
}
