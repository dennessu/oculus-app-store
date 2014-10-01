/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.configuration.reloadable;

import com.junbo.configuration.reloadable.impl.ReloadableConfig;

import java.util.UUID;

/**.
 * Java doc
 */
public class UuidConfig extends ReloadableConfig<UUID> {
    @Override
    protected UUID parseValue(String value) {
        return UUID.fromString(value);
    }
}
