/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.configuration.reloadable;

import com.junbo.configuration.reloadable.impl.ReloadableConfig;

import java.lang.reflect.ParameterizedType;

/**.
 * Java doc for EnumConfig
 * @param <T>
 */
public abstract class EnumConfig<T extends Enum> extends ReloadableConfig<T> {
    @Override
    protected T parseValue(String value) {
        Class clazz = (Class) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        return (T) Enum.valueOf(clazz, value);
    }
}
