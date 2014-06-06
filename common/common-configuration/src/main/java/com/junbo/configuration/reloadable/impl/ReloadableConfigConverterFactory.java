/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.configuration.reloadable.impl;

import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;

/**.
 * Java doc
 */
public class ReloadableConfigConverterFactory implements ConverterFactory<String, ReloadableConfig> {

    public <T extends ReloadableConfig> Converter<String, T> getConverter(Class<T> targetType) {
        return new ReloadableConfigConverter(targetType);
    }

    private static final class ReloadableConfigConverter<T extends ReloadableConfig> implements Converter<String, T> {
        private Class<T> configType;

        public ReloadableConfigConverter(Class<T> configType) {
            this.configType = configType;
        }

        @SuppressWarnings("unchecked")
        public T convert(String key) {
            return ReloadableConfigFactory.create(key, configType);
        }
    }
}
