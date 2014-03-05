/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.configuration.reloadable.impl;

import com.junbo.configuration.ConfigService;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;

import java.util.WeakHashMap;

/**.
 * Java doc
 */
public class ReloadableConfigConverterFactory implements ConverterFactory<String, ReloadableConfig> {
    private static final String CONFIG_PREFIX = "[";
    private static final String CONFIG_SUFFIX = "]";

    private ConfigService configService;

    @Required
    public void setConfigService(ConfigService configService) {
        this.configService = configService;
    }

    public <T extends ReloadableConfig> Converter<String, T> getConverter(Class<T> targetType) {
        return new ReloadableConfigConverter(configService, targetType);
    }

    private static final class ReloadableConfigConverter<T extends ReloadableConfig> implements Converter<String, T> {
        private ConfigService configService;
        private Class<T> configType;
        private WeakHashMap<String, ReloadableConfig> instanceCache = new WeakHashMap<>();

        public ReloadableConfigConverter(ConfigService configService, Class<T> configType) {
            this.configService = configService;
            this.configType = configType;
        }

        @SuppressWarnings("unchecked")
        public T convert(String key) {
            key = trimBrackets(key);
            synchronized (instanceCache) {
                if (instanceCache.containsKey(key)) {
                    return (T)instanceCache.get(key);
                }

                try {
                    T instance = configType.newInstance();
                    instance.initialize(configService, key);
                    instanceCache.put(key, instance);
                    return instance;
                }
                catch (InstantiationException | IllegalAccessException | IllegalArgumentException ex) {
                    throw new RuntimeException("Error creating instance of " + configType, ex);
                }
            }
        }

        private static String trimBrackets(String key) {
            if (!key.startsWith(CONFIG_PREFIX) || !key.endsWith(CONFIG_SUFFIX) || key.length() <= 2) {
                throw new RuntimeException("Invalid ReloadableConfig key: " + key);
            }
            return key.substring(1, key.length() - 1);
        }
    }
}
