/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.common.id;

/**
 * EmailTemplateId.
 */
@IdResourcePath(value = "/email-templates/{0}",
                resourceType = "email-templates",
                regex = "/email-templates/(?<id>[0-9A-Za-z]+)")
public class EmailTemplateId extends CloudantId {
    public EmailTemplateId() {}

    public EmailTemplateId(String value) {
        super(value);
    }
}
