/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.common.enumid;

import com.junbo.common.id.IdResourcePath;

/**
 * Created by haomin on 14-4-24.
 */
@IdResourcePath("/locales/{0}")
public class LocaleId extends EnumId {
    public LocaleId() {}

    public LocaleId(String value) {
        super(value);
    }

    @Override
    public String getValue() {
        String value = super.getValue();
        if (value != null) {
            value = value.replace('-', '_');
        }
        return value;
    }

    @Override
    public void setValue(String value) {
        if (value != null) {
            value = value.replace('-', '_');
        }
        super.setValue(value);
    }

    @Override
    public String toString() {
        String value = super.toString();
        if (value != null) {
            value = value.replace('-', '_');
        }
        return value;
    }
}
