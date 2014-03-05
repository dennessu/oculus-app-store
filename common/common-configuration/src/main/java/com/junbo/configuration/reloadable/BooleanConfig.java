/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.configuration.reloadable;

import com.junbo.configuration.reloadable.impl.ReloadableConfig;

/**.
 * Java doc
 */
public class BooleanConfig extends ReloadableConfig<Boolean> {
    @Override
    protected Boolean parseValue(String value) {
        return Boolean.parseBoolean(value);
    }
}
