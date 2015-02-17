/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.configuration.impl;

import com.junbo.configuration.ConfigContext;
import com.junbo.configuration.crypto.CipherService;
import com.junbo.configuration.crypto.impl.AESCipherServiceImpl;
import com.junbo.utils.FileUtils;
import com.junbo.utils.FileWatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.*;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchEvent;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * This ConfigurationService can have two parameters:
 * 1):  environment:    If this environment(onebox/int/prod...) is configured,
 * it will load configuration from its environment configuration;
 * If this environment isn't configured, it will load from onebox environment by default;
 * 2):  configDir:      This is the override configuration file, if it is set,
 * it will override the same property in the configuration data, and we will watch this file;
 * if it isn't set, use configuration data.
 */
@SuppressWarnings("unused")
public class ConfigServiceImpl implements com.junbo.configuration.ConfigService {
    //region private fields
    private static final String CONFIG_PROPERTY_FILE = "configuration.properties";
    private static final String CONFIG_PASSWORD_KEY = "crypto.core.key";
    private static final String CONFIG_DIR_OPTS = "configDir";
    private static final String CONFIG_DIR_DEFAULT = "/etc/silkcloud;./conf";
    private static final String ACTIVE_ENV_OPTS = "environment";
    private static final String ACTIVE_DC_OPTS = "datacenter";
    private static final String ACTIVE_SHARD_RANGE_OPTS = "shardrange";
    private static final String ACTIVE_SUBNET_OPTS = "subnet";
    private static final String SUFFIX_PROPERTY_FILE = ".properties";
    private static final String DEFAULT_FOLDER = "_default";
    private static final String DEFAULT_PROPERTIES_FILE = "_default.properties";
    private static final String DEFAULT_ENVIRONMENT = "onebox";
    private static final String CONFIG_PATH = "junbo/conf";
    private static final String FILE_FORMAT = "utf-8";

    private static final String CRYPTO_SUFFIX = ".encrypted";

    private static Logger logger = LoggerFactory.getLogger(ConfigServiceImpl.class);

    private String configPath;
    private ConfigContext configContext;

    private Properties jarProperties = new Properties();
    private Properties overrideProperties = new Properties();
    private Map<String, String> finalProperties = new HashMap<>();

    private ConfigChangeListenerMap listeners = new ConfigChangeListenerMap();
    private CipherService cipherService;
    private String keyStr;
    //endregion

    public ConfigServiceImpl() {
        this.loadConfig();
        this.watch();
    }

    @Override
    public String getConfigPath() {
        return configPath;
    }

    @Override
    public ConfigContext getConfigContext() {
        return configContext;
    }

    @Override
    public String getConfigValue(String configKey) {
        return finalProperties.get(configKey);
    }

    @Override
    public Integer getConfigValueAsInt(String configKey, Integer defaultValue) {
        String value = getConfigValue(configKey);
        if (StringUtils.isEmpty(value)) {
            return defaultValue;
        }
        return Integer.parseInt(value);
    }

    @Override
    public Boolean getConfigValueAsBool(String configKey, Boolean defaultValue) {
        String value = getConfigValue(configKey);
        if (StringUtils.isEmpty(value)) {
            return defaultValue;
        }
        return Boolean.parseBoolean(value);
    }

    @Override
    public Map<String, String> getAllConfigItems() {
        return Collections.unmodifiableMap(finalProperties);
    }

    @Override
    public Map<String, String> getAllConfigItemsMasked() {

        Map<String, String> properties = new HashMap<>();

        for (Map.Entry entry : finalProperties.entrySet()) {
            String key = entry.getKey().toString();
            String value = entry.getValue().toString();

            if (key.endsWith(CRYPTO_SUFFIX)) {
                // ignored
            } else if (finalProperties.containsKey(key + CRYPTO_SUFFIX)) {
                value = "*****";
                properties.put(key, value);
            } else {
                // replace all URL passwords included in the URL
                value = value.replaceAll("://(?<username>[^/@:]+):(?<password>[^/@]*)@", "://$1:*****@");
                properties.put(key, value);
            }
        }

        return properties;
    }

    @Override
    public void addListener(String configKey, ConfigListener listener) {
        listeners.addListener(configKey, listener);
    }

    //region private methods

    private ConfigContext readConfigContext(Properties properties) {
        String environment = readConfigContext(ACTIVE_ENV_OPTS, ACTIVE_ENV_OPTS, properties, DEFAULT_ENVIRONMENT);
        return new ConfigContext(environment);
    }

    private String readConfigContext(String settingName, String settingKey, Properties properties, String defaultValue) {
        String result = System.getProperty(settingKey);
        if (StringUtils.isEmpty(result) && properties != null) {
            result = properties.getProperty(settingKey);
        }

        if (StringUtils.isEmpty(result)) {
            logger.info("No configContext {} configured, will use default {}='{}'", settingName, settingKey, defaultValue);
            result = defaultValue;
        } else {
            logger.info("ConfigContext {} is configured as {}='{}'", settingName, settingKey, result);
        }
        properties.setProperty(settingKey, result);
        return result;
    }

    // This path is the configurationData first level Path
    private Properties readJarProperties() {
        Properties properties = new Properties();

        try {
            // Load sequence as below, so the priority as reverse
            // Where {env} = {baseEnv}_{subEnv}
            // 1) _default/_default.properties
            // 2) _default/**/*.properties
            // 3) {baseEnv}/_default.properties
            // 4) {baseEnv}/**/*.properties
            // 5) {env}/_default.properties
            // 4) {env}/**/*.properties

            // 1) _default/_default.properties
            InputStream inputStream = this.getClass().getClassLoader().
                    getResourceAsStream(CONFIG_PATH + "/" + DEFAULT_FOLDER + "/" + DEFAULT_PROPERTIES_FILE);
            if (inputStream != null) {
                properties.load(inputStream);
                check(properties);
            }

            // 2) _default/**/*.properties
            Enumeration<URL> en = this.getClass().getClassLoader().getResources(
                    CONFIG_PATH + "/" + DEFAULT_FOLDER);

            if (en.hasMoreElements()) {
                URL url = en.nextElement();
                Properties temp = new Properties();
                loadJarProperties(url.getPath(), temp);
                check(temp);
                merge(properties, temp);
            }

            String baseEnv = this.getConfigContext().getBaseEnvironment();
            if (baseEnv != null) {
                // 3) {baseEnv}/_default.properties
                inputStream = this.getClass().getClassLoader().
                        getResourceAsStream(CONFIG_PATH + "/" + baseEnv + "/" + DEFAULT_PROPERTIES_FILE);
                if (inputStream != null) {
                    Properties temp = new Properties();
                    temp.load(inputStream);
                    check(temp);
                    merge(properties, temp);
                }

                // 4) {baseEnv}/**/*.properties
                en = this.getClass().getClassLoader().getResources(
                        CONFIG_PATH + "/" + baseEnv);

                if (en.hasMoreElements()) {
                    URL url = en.nextElement();
                    Properties temp = new Properties();
                    loadJarProperties(url.getPath(), temp);
                    check(temp);
                    merge(properties, temp);
                }
            }

            // 5) {env}/_default.properties
            String env = this.getConfigContext().getEnvironment();
            inputStream = this.getClass().getClassLoader().
                    getResourceAsStream(CONFIG_PATH + "/" + env + "/" + DEFAULT_PROPERTIES_FILE);
            if (inputStream != null) {
                Properties temp = new Properties();
                temp.load(inputStream);
                check(temp);
                merge(properties, temp);
            }

            // 6) {env}/**/*.properties
            en = this.getClass().getClassLoader().getResources(
                    CONFIG_PATH + "/" + env);

            if (en.hasMoreElements()) {
                URL url = en.nextElement();
                Properties temp = new Properties();
                loadJarProperties(url.getPath(), temp);
                check(temp);
                merge(properties, temp);
            }
        } catch (IOException ex) {
            logger.error("Failed to read jar file.", ex);
            throw new RuntimeException("Failed to read configuration jar file.", ex);
        }

        return properties;
    }

    private void loadJarProperties(String jarPath, Properties properties) {
        List<String> resourcePathes = new ArrayList<String>();
        String[] jarInfo = jarPath.split("!");
        if (jarInfo.length == 1) {
            // this is loading from class file, not jar package
            loadFolderProperties(jarInfo[0], properties);
            return;
        }

        String jarFilePath = jarInfo[0].substring(jarInfo[0].indexOf("/"));
        String packagePath = jarInfo[1].substring(1);

        // make sure the path ends with /
        if (!packagePath.endsWith("/")) {
            packagePath = packagePath + "/";
        }
        try {
            JarFile jarFile = new JarFile(jarFilePath);
            Enumeration<JarEntry> entries = jarFile.entries();
            while (entries.hasMoreElements()) {
                JarEntry jarEntry = entries.nextElement();
                String entryName = jarEntry.getName();
                if (entryName.startsWith(packagePath) && entryName.endsWith(SUFFIX_PROPERTY_FILE)) {
                    resourcePathes.add(entryName);
                }
            }
        } catch (Exception ex) {
            throw new RuntimeException("Failed to read from jar path: " + jarPath, ex);
        }

        for (String string : resourcePathes) {
            try (InputStream fileStream = this.getClass().getClassLoader().getResourceAsStream(string)) {
                Properties newProperties = new Properties();
                newProperties.load(fileStream);
                check(newProperties);
                merge(properties, newProperties);
            } catch (IOException ex) {
                throw new RuntimeException("Failed to read property file from resource: " + string, ex);
            }
        }
    }

    private void loadFolderProperties(String folder, Properties properties) {
        for (File file : new File(folder).listFiles()) {
            if (file.isDirectory()) {
                loadFolderProperties(file.getAbsolutePath(), properties);
            } else if (file.isFile()) {
                try (InputStream fileStream = new FileInputStream(file.getAbsoluteFile())) {
                    Properties temp = new Properties();
                    temp.load(fileStream);
                    check(temp);
                    merge(properties, temp);
                } catch (IOException ex) {
                    throw new RuntimeException("Failed to read file: " + file.getAbsolutePath(), ex);
                }
            }
        }
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
        } catch (Exception ex) {
            throw new RuntimeException("Unable to load properties from path [" + entryName + "]", ex);
        } finally {
            try {
                if (stream != null) {
                    stream.close();
                }
            } catch (IOException ex) {
                // ignored
            }
        }
    }

    private Properties readProperties(Path path) {
        logger.info("Reading override properties from: " + path);
        if (Files.exists(path)) {
            try (InputStream in = new BufferedInputStream(new FileInputStream(path.toString()))) {
                Properties properties = new Properties();
                properties.load(in);
                return properties;
            } catch (IOException ex) {
                throw new RuntimeException("Failed to read property file: " + path, ex);
            }
        }
        return new Properties();
    }

    private void loadConfig() {
        String configDirs = System.getProperty(CONFIG_DIR_OPTS);
        if (StringUtils.isEmpty(configDirs)) {
            configDirs = CONFIG_DIR_DEFAULT;
        }

        Path configFilePath = findFile(configDirs, CONFIG_PROPERTY_FILE);
        if (configFilePath != null) {
            overrideProperties = readProperties(configFilePath);
            check(overrideProperties);
            configPath = configFilePath.getParent().toString();
            logger.info("Monitored configPath set to " + configPath);
        }

        configContext = readConfigContext(overrideProperties);

        // Read jar configuration files
        jarProperties = readJarProperties();
        check(jarProperties);

        Properties commandLineProperties = System.getProperties();
        check(commandLineProperties);

        keyStr = loadPasswordKey(commandLineProperties, overrideProperties, jarProperties);

        cipherService = new AESCipherServiceImpl(keyStr);

        Properties finalProperties = new Properties();
        merge(finalProperties, jarProperties);
        merge(finalProperties, overrideProperties);
        merge(finalProperties, commandLineProperties);
        finalProperties = decryptProperties(finalProperties);

        configContext.complete(
                finalProperties.getProperty(ACTIVE_DC_OPTS),
                finalProperties.getProperty(ACTIVE_SUBNET_OPTS),
                finalProperties.getProperty(ACTIVE_SHARD_RANGE_OPTS));

        this.finalProperties = new HashMap<>();
        for (String key : finalProperties.stringPropertyNames()) {
            this.finalProperties.put(key, finalProperties.getProperty(key));
        }
    }

    private void check(Properties properties) {
        // Same file should not has the same properties defined multiple times
        // such as in file conf.properties
        // a.password.encrypt=aaaa
        // a.password = bbbbb, This will be treated as bad case
        if (properties == null) {
            return;
        }

        List<String> keys = new ArrayList<>();
        for (Map.Entry<Object, Object> entry : properties.entrySet()) {
            String key = entry.getKey().toString();

            if (key.endsWith(CRYPTO_SUFFIX)) {
                int endIndex = key.lastIndexOf(CRYPTO_SUFFIX);
                String newKey = key.substring(0, endIndex);

                if (keys.contains(newKey)) {
                    throw new IllegalStateException("Already exists key: " + newKey);
                }
                keys.add(newKey);
            } else {
                if (keys.contains(key)) {
                    throw new IllegalStateException("Already exists key: " + key);
                }
                keys.add(key);
            }
        }
    }

    private Properties merge(Properties source, Properties override) {
        if (override == null) {
            return source;
        }

        for (Map.Entry<Object, Object> entry : override.entrySet()) {
            String key = entry.getKey().toString();

            if (key.endsWith(CRYPTO_SUFFIX)) {
                int endIndex = key.lastIndexOf(CRYPTO_SUFFIX);
                String newKey = key.substring(0, endIndex);
                source.remove(newKey);
            } else {
                String newKey = key + CRYPTO_SUFFIX;
                source.remove(newKey);
            }
            source.put(entry.getKey(), entry.getValue());
        }

        return source;
    }

    private Path findFile(String configDirs, String fileName) {
        if (StringUtils.isEmpty(configDirs)) {
            return null;
        }

        logger.info("Scanning configuration file {} from configDirs: {}", fileName, configDirs);
        String[] configDirArr = configDirs.split(";");
        for (String configDir : configDirArr) {
            if (StringUtils.isEmpty(configDir)) continue;
            Path path = Paths.get(configDir, fileName);
            if (Files.exists(path)) {
                logger.info("Found configuration file {} from configDir: {}", fileName, configDir);
                FileUtils.checkPermission600(path);
                return path;
            }
        }
        logger.warn("Configuration file {} is not found", fileName);
        return null;
    }

    private Properties decryptProperties(Properties properties) {
        if (properties == null || properties.isEmpty()) {
            return properties;
        }

        Properties newProperties = new Properties();

        for (Map.Entry<Object, Object> entry : properties.entrySet()) {
            String key = entry.getKey().toString();
            String value = entry.getValue().toString();

            if (key.endsWith(CRYPTO_SUFFIX)) {
                int endIndex = key.lastIndexOf(CRYPTO_SUFFIX);
                String newKey = key.substring(0, endIndex);

                try {
                    newProperties.put(newKey, decrypt(value));
                } catch (Exception ex) {
                    logger.error("Failed to decrypt " + key);
                    throw new RuntimeException("Failed to decrypt " + key, ex);
                }
            }

            newProperties.put(key, value);
        }

        return newProperties;
    }

    private String loadPasswordKey(Properties... properties) {
        for (Properties propertyBag : properties) {
            if (propertyBag == null) continue;
            if (propertyBag.containsKey(CONFIG_PASSWORD_KEY)) {
                String value = propertyBag.getProperty(CONFIG_PASSWORD_KEY);
                propertyBag.remove(CONFIG_PASSWORD_KEY);
                return value;
            }
        }
        // not found
        throw new RuntimeException(CONFIG_PASSWORD_KEY + " is not specified.");
    }

    private void watch() {
        if (!StringUtils.isEmpty(configPath)) {
            FileWatcher.getInstance().addListener(Paths.get(configPath), new FileWatcher.FileListener() {
                @Override
                public void onFileChanged(Path path, WatchEvent.Kind<Path> kind) {
                    loadConfig();
                }
            });
        }
    }

    private String decrypt(String encryptedValue) {
        if (StringUtils.isEmpty(encryptedValue)) {
            return encryptedValue;
        }

        if (StringUtils.isEmpty(keyStr)) {
            throw new RuntimeException("Key is missing.");
        }

        return cipherService.decrypt(encryptedValue);
    }

    private String readKeyFile(File file) throws IOException {
        FileInputStream fis = new FileInputStream(file);

        BufferedReader br = new BufferedReader(new InputStreamReader(fis));

        List<String> list = new ArrayList<>();
        String line = null;
        while ((line = br.readLine()) != null) {
            list.add(line);
        }

        br.close();
        if (CollectionUtils.isEmpty(list) || list.size() > 2) {
            throw new RuntimeException("Invalid file format");
        }
        return list.get(0);
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
                } else {
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
