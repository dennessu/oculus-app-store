/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.common.id;

/**
 * Java doc for subledgerId.
 */
@IdResourcePath("/subledgers/{0}")
public class SubledgerId extends Id {

    public SubledgerId() {}

    public SubledgerId(long value) {
        super(value);
    }
}
