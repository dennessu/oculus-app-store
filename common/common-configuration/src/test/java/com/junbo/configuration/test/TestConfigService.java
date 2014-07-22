package com.junbo.configuration.test;

import com.junbo.configuration.ConfigContext;
import com.junbo.configuration.ConfigService;
import com.junbo.configuration.ConfigServiceManager;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class TestConfigService implements ConfigService, AutoCloseable {
    private ConfigService oldInstance;
    private ConfigContext context = new ConfigContext("unittest").complete("dc0", "127.0.0.1/32");
    private ConfigService.ConfigListener listener;
    private Map<String, String> properties = new HashMap<String, String>() {{
        put("common.topo.datacenters", "http://localhost:8080/v1;0;dc0;2,http://localhost:8080/v1;1;dc1;2");
    }};

    public TestConfigService() {
        this.oldInstance = ConfigServiceManager.instance();
        ConfigServiceManager.setInstance(this);
    }

    @Override
    public String getConfigPath() {
        return "dummy";
    }

    @Override
    public ConfigContext getConfigContext() {
        return context;
    }

    @Override
    public String getConfigValue(String configKey) {
        return properties.get(configKey);
    }

    @Override
    public Map<String, String> getAllConfigItems() {
        return properties;
    }

    @Override
    public Map<String, String> getAllConfigItemsMasked() {
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
        properties.put(configKey, newValue);
        this.listener.onConfigChanged(configKey, newValue);
    }

    @Override
    public void close() throws Exception {
        ConfigServiceManager.setInstance(oldInstance);
    }
}