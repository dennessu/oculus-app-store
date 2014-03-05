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
public class IntegerConfig extends ReloadableConfig<Integer> {
    @Override
    protected Integer parseValue(String value) {
        return Integer.parseInt(value);
    }
}
