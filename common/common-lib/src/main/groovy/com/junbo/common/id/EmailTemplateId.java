/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.common.id;

/**
 * EmailTemplateId.
 */
@IdResourcePath(value = "/email-templates/{0}", regex = "/email-templates/(?<id>[0-9A-Z]+)")
public class EmailTemplateId extends Id {
    public EmailTemplateId() {}

    public EmailTemplateId(Long value) {
        super(value);
    }
}
