/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.configuration.reloadable;

import com.junbo.configuration.reloadable.impl.ReloadableConfig;

import java.io.IOException;
import java.io.StringReader;
import java.util.Properties;

/**.
 * Java doc
 */
public class PropertiesConfig extends ReloadableConfig<Properties> {
    @Override
    protected Properties parseValue(String value) {
        try {
            Properties result = new Properties();
            result.load(new StringReader(value));
            return result;
        }
        catch (IOException ex) {
            throw new RuntimeException("Error creating StringReader from value: " + value, ex);
        }
    }
}
