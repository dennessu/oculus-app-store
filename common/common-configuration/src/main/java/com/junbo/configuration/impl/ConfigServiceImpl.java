/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.configuration.impl;

import com.junbo.configuration.ConfigContext;
import com.junbo.utils.FileWatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.io.*;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchEvent;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * This ConfigurationService can have two parameters:
 * 1):  activeEnv:      If this environment(onebox/int/prod...) is configured,
 *                      it will load configuration from its environment configuration;
 *                      If this environment isn't configured, it will load from onebox environment by default;
 * 2):  configDir:      This is the override configuration file, if it is set,
 *                      it will override the same property in the configuration data, and we will watch this file;
 *                      if it isn't set, use configuration data.
 */
@SuppressWarnings("unused")
public class ConfigServiceImpl implements com.junbo.configuration.ConfigService {
    //region private fields
    private static final String CONFIG_PROPERTY_FILE = "configuration.properties";
    private static final String CONFIG_DIR_OPTS = "configDir";
    private static final String ACTIVE_ENV_OPTS = "activeEnv";
    private static final String SUFFIX_PROPERTY_FILE = ".properties";
    private static final String DEFAULT_FOLDER = "_default";
    private static final String DEFAULT_PROPERTIES_FILE = "_default.properties";
    private static final String DEFAULT_ENVIRONMENT = "onebox";
    private static final String CONFIG_PATH = "junbo/conf";

    private static Logger logger = LoggerFactory.getLogger(ConfigServiceImpl.class);

    private String configPath;
    private ConfigContext configContext;

    private Properties jarProperties = new Properties();
    private Properties overrideProperties = new Properties();
    private Properties finalProperties = new Properties();

    private ConfigChangeListenerMap listeners = new ConfigChangeListenerMap();

    //endregion

    public ConfigServiceImpl() {
        this.loadConfig();
        this.watch();
    }

    @Override
    public ConfigContext getConfigContext() {
        return configContext;
    }

    @Override
    public String getConfigValue(String configKey) {
        return finalProperties.getProperty(configKey);
    }

    @Override
    public Properties getAllConfigItems() {
        return finalProperties;
    }

    @Override
    public void addListener(String configKey, ConfigListener listener) {
        listeners.addListener(configKey, listener);
    }

    //region private methods

    private ConfigContext readConfigContext(Path path) {
        logger.info("Reading config context from: " + path);
        Properties properties = readProperties(path);
        String environment = properties.getProperty("environment");
        //String shards = properties.getProperty("shards");

        if (StringUtils.isEmpty(environment)) {
            throw new RuntimeException(String.format(
                    "Configuration context incomplete from path %s. environment=%s",
                    path, environment));
        }

        return new ConfigContext(environment);
    }

    // This path is the configurationData first level Path
    private Properties readJarProperties() {
        Properties properties = new Properties();

        try {
            //load sequence as below, so the priority as reverse
            // 1) _default/_default.properties
            // 2) _default/**/*.properties
            // 3) {env}/_default.properties
            // 4) {env}/**/*.properties

            // 1) _default/_default.properties
            InputStream inputStream = this.getClass().getClassLoader().
                    getResourceAsStream(CONFIG_PATH + "/" + DEFAULT_FOLDER + "/" + DEFAULT_PROPERTIES_FILE);
            if(inputStream != null) {
                properties.load(inputStream);
            }

            // 2) _default/**/*.properties
            Enumeration<URL> en = this.getClass().getClassLoader().getResources(
                    CONFIG_PATH + "/" + DEFAULT_FOLDER);

            if (en.hasMoreElements()) {
                URL url = en.nextElement();
                List<String> strings = getJarFiles(url.getPath());
                for(String string : strings) {
                    InputStream inputStream1 = this.getClass().getClassLoader().getResourceAsStream(string);
                    properties.load(inputStream1);
                }
            }

            // 3) {env}/_default.properties
            String env = this.getConfigContext().getEnvironment();
            inputStream = this.getClass().getClassLoader().
                    getResourceAsStream(CONFIG_PATH + "/" + env + "/" + DEFAULT_PROPERTIES_FILE);
            if(inputStream != null) {
                properties.load(inputStream);
            }

            // 4) {env}/**/*.properties
            en = this.getClass().getClassLoader().getResources(
                    CONFIG_PATH + "/" + env);

            if (en.hasMoreElements()) {
                URL url = en.nextElement();
                List<String> strings = getJarFiles(url.getPath());
                for(String string : strings) {
                    InputStream inputStream1 = this.getClass().getClassLoader().getResourceAsStream(string);
                    properties.load(inputStream1);
                }
            }
        }
        catch (IOException ex) {
            logger.warn("Failed to read jar file: ");
        }

        return properties;
    }

    private List<String> getJarFiles(String jarPath) {
        List<String> resourcePathes = new ArrayList<String>();
        String[] jarInfo = jarPath.split("!");
        String jarFilePath = jarInfo[0].substring(jarInfo[0].indexOf("/"));
        String packagePath = jarInfo[1].substring(1);
        try {
            JarFile jarFile = new JarFile(jarFilePath);
            Enumeration<JarEntry> entrys = jarFile.entries();
            while (entrys.hasMoreElements()) {
                JarEntry jarEntry = entrys.nextElement();
                String entryName = jarEntry.getName();
                if (entryName.startsWith(packagePath) && entryName.endsWith(SUFFIX_PROPERTY_FILE)) {
                    resourcePathes.add(entryName);
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return resourcePathes;
    }

    private void loadJarEntry(Properties props, JarFile jarFile, String entryName) {
        if (props == null || jarFile == null || entryName == null) {
            return;
        }

        InputStream stream = null;
        try {
            JarEntry jarEntry = jarFile.getJarEntry(entryName);
            if (jarEntry != null) {
                stream = jarFile.getInputStream(jarEntry);
            }
            if (stream != null) {
                props.load(stream);
            }
        }
        catch (Exception ex) {
            throw new RuntimeException("Unable to load properties from path [" + entryName + "]", ex);
        }
        finally {
            try {
                if (stream != null) {
                    stream.close();
                }
            }
            catch (IOException ex) {
                // ignored
            }
        }
    }

    private Properties readProperties(Path path) {
        logger.info("Reading override properties from: " + path);
        try (InputStream in = new BufferedInputStream(new FileInputStream(path.toString()))) {
            Properties properties = new Properties();
            properties.load(in);
            return properties;
        } catch (IOException ex) {
            throw new RuntimeException("Failed to read property file: " + path, ex);
        }
    }

    private void loadConfig() {
        String environment = System.getProperty(ACTIVE_ENV_OPTS);
        if (StringUtils.isEmpty(environment)) {
            logger.info("No environment configured, will use default environment=onebox.\n");
            configContext = new ConfigContext(DEFAULT_ENVIRONMENT);
        }
        else {
            logger.info("Environment is configured as environment=" + environment);
            configContext = new ConfigContext(environment);
        }

        // Read jar configuration files
        jarProperties = readJarProperties();

        String configDir = System.getProperty(CONFIG_DIR_OPTS);
        if (!StringUtils.isEmpty(configDir)) {
            logger.info("Scanning configuration from configDir: " + configDir);

            Path configFilePath = Paths.get(configDir, CONFIG_PROPERTY_FILE);
            overrideProperties = readProperties(configFilePath);
        }

        finalProperties = new Properties();
        finalProperties.putAll(jarProperties);
        finalProperties.putAll(overrideProperties);
    }

    private void watch(){
        String configDir = System.getProperty(CONFIG_DIR_OPTS);
        if(!StringUtils.isEmpty(configDir)) {
            FileWatcher.getInstance().addListener(Paths.get(configDir), new FileWatcher.FileListener() {
                @Override
                public void onFileChanged(Path path, WatchEvent.Kind<Path> kind) {
                    loadConfig();
                }
            });
        }
    }

    private static final class ConfigChangeListenerList {
        private ArrayList<WeakReference<ConfigListener>> listeners = new ArrayList<>();

        public void addListener(ConfigListener listener) {
            listeners.add(new WeakReference<>(listener));
        }

        public void notify(String key, String newValue) {
            ArrayList<WeakReference<ConfigListener>> toRemove = new ArrayList<>();
            for (WeakReference<ConfigListener> listenerWeakReference : listeners) {
                ConfigListener listener = listenerWeakReference.get();
                if (listener == null) {
                    toRemove.add(listenerWeakReference);
                }
                else {
                    listener.onConfigChanged(key, newValue);
                }
            }
            listeners.removeAll(toRemove);
        }
    }

    private static final class ConfigChangeListenerMap {
        private ConcurrentHashMap<String, ConfigChangeListenerList> listeners = new ConcurrentHashMap<>();

        public void addListener(String key, ConfigListener listener) {
            ConfigChangeListenerList list = listeners.get(key);
            if (list == null) {
                final ConfigChangeListenerList newList = new ConfigChangeListenerList();
                list = listeners.putIfAbsent(key, newList);
                if (list == null) {
                    list = newList;
                }
            }
            list.addListener(listener);
        }

        public void notify(String key, String newValue) {
            ConfigChangeListenerList list = listeners.get(key);
            if (list != null) {
                list.notify(key, newValue);
            }
        }
    }

    //endregion
}
