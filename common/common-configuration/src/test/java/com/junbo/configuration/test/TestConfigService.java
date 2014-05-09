package com.junbo.configuration.test;

import com.junbo.configuration.ConfigContext;
import com.junbo.configuration.ConfigService;
import com.junbo.configuration.ConfigServiceManager;

import java.util.Properties;

public class TestConfigService implements ConfigService, AutoCloseable {
    private ConfigService oldInstance;
    private ConfigContext context = new ConfigContext("unittest", "dc0", "0.0.0.0/0");
    private ConfigService.ConfigListener listener;
    private Properties properties = new Properties();

    public TestConfigService() {
        this.oldInstance = ConfigServiceManager.instance();
        ConfigServiceManager.setInstance(this);
    }

    @Override
    public ConfigContext getConfigContext() {
        return context;
    }

    @Override
    public String getConfigValue(String configKey) {
        return properties.getProperty(configKey);
    }

    @Override
    public Properties getAllConfigItems() {
        return properties;
    }

    @Override
    public void addListener(String configKey, ConfigService.ConfigListener listener) {
        this.listener = listener;
    }

    public void setConfigContext(ConfigContext context) {
        this.context = context;
    }

    public void updateConfig(String configKey, String newValue) {
        properties.setProperty(configKey, newValue);
        this.listener.onConfigChanged(configKey, newValue);
    }

    @Override
    public void close() throws Exception {
        ConfigServiceManager.setInstance(oldInstance);
    }
}