/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.sharding.core.util;

import com.junbo.sharding.annotations.DatabaseName;
import org.springframework.core.annotation.AnnotationUtils;

/**
 * Created by haomin on 14-3-11.
 */
public class Helper {
    private Helper() {}

    public static String getDatabaseName(Class<?> clazz) {
        DatabaseName dbName = AnnotationUtils.findAnnotation(clazz, DatabaseName.class);

        if (dbName != null) {
            return dbName.value();
        }

        throw new RuntimeException("Can't find @DatabaseName on Dao " + clazz.getCanonicalName());
    }
}
