/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.common.id;

/**
 * EmailId.
 */
@IdResourcePath(value = "/emails/{0}",
                resourceType = "emails",
                regex = "/emails/(?<id>[0-9A-Za-z]+)")
public class EmailId extends CloudantId {
    public EmailId() {}

    public EmailId(String value) {
        super(value);
    }
}
