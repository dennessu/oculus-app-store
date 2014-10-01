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
public class StringConfig extends ReloadableConfig<String> {
    @Override
    protected String parseValue(String value) {
        return value;
    }
}
