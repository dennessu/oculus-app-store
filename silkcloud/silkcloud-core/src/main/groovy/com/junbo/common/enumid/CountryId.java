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
@IdResourcePath("/countries/{0}")
public class CountryId extends EnumId {
    public CountryId() {}

    public CountryId(String value) {
        super(value);
    }
}
