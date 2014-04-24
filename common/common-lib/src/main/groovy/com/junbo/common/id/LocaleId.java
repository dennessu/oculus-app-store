/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.common.id;

/**
 * Created by liangfu on 4/24/14.
 */
@IdResourcePath("/locales/{0}")
public class LocaleId extends Id {

    public LocaleId() {}

    public LocaleId(long value) {
        super(value);
    }
}
