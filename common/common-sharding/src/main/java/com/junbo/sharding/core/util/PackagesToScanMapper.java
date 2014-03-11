/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.sharding.core.util;

import java.util.Properties;

/**
 * Created by haomin on 14-3-11.
 */
public class PackagesToScanMapper {
    private Properties properties;


    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    public String[] getPackagesToScan(String dbname) {
        if (this.properties == null) {
            throw new RuntimeException("PackagesToScanMapper not initialized properly");
        }
        if (dbname != null) {
            String str = this.properties.getProperty(dbname);
            if (str != null) {
                return str.split(";");
            }
        }

        return null;
    }
}
