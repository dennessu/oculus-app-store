/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.configuration;

import java.util.Map;

/**
 * ConfigService is used to read configurations from property files.
 * This interface is not used directly in most cases. The configurations are often used through injection:
 * <ul>
 * <li>Specify ${property.name} in spring context files. </li>
 * <li>Define a ReloadableConfig field and specify [property.name] in spring context files.</li>
 * </ul>
 */
public interface ConfigService {

    /**
     * The callback when the configuration is changed.
     */
    public interface ConfigListener {
        /**
         * The callback method when the configuration value changes.
         *
         * @param configKey The configuration key which has been changed.
         * @param newValue  The new value after the change.
         */
        void onConfigChanged(String configKey, String newValue);
    }

    /**
     * The path used to load configuration.properties.
     *
     * @return The configuration path.
     */
    String getConfigPath();

    /**
     * The context of current machine. The context is used in resolving the property overrides.
     *
     * @return The configuration context.
     */
    ConfigContext getConfigContext();

    /**
     * Resolve the configuration property value by config key.
     *
     * @param configKey The property key.
     * @return The property value.
     */
    String getConfigValue(String configKey);

    /**
     * Resolve the configuration property value by config key.
     *
     * @param configKey The property key.
     * @return The property value.
     */
    Integer getConfigValueAsInt(String configKey, Integer defaultValue);

    /**
     * Resolve the configuration property value by config key.
     *
     * @param configKey The property key.
     * @return The property value.
     */
    Boolean getConfigValueAsBool(String configKey, Boolean defaultValue);

    /**
     * Get all effective configuration items.
     *
     * @return The property bag containing all effective configuration items.
     */
    Map<String, String> getAllConfigItems();

    Map<String, String> getAllConfigItemsMasked();

    /**
     * Add listener to a specified configuration key.
     *
     * @param configKey The configuration key to listen.
     * @param listener  The listener to callback.
     */
    void addListener(String configKey, ConfigListener listener);
}
