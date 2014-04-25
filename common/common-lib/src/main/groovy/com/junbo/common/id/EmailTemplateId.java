/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.common.id;

/**
 * EmailTemplateId.
 */
@IdResourcePath("/email-templates/{0}")
public class EmailTemplateId extends Id {
    public EmailTemplateId() {}

    public EmailTemplateId(Long value) {
        super(value);
    }
}