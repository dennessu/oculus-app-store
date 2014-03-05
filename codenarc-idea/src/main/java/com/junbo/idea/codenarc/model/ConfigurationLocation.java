package com.junbo.idea.codenarc.model;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;

/**
 * Bean encapsulating a configuration source.
 */
public abstract class ConfigurationLocation implements Cloneable {

    private static final Log LOG = LogFactory.getLog(ConfigurationLocation.class);

    private static final long BLACKLIST_TIME_MS = 1000 * 60;

    private final ConfigurationType type;
    private String location;
    private String description;
    private Map<String, String> properties = new HashMap<String, String>();

    private boolean propertiesCheckedThisSession;
    private long blacklistedUntil;

    public ConfigurationLocation(final ConfigurationType type) {
        if (type == null) {
            throw new IllegalArgumentException("A type is required");
        }

        this.type = type;
    }

    /**
     * Get the base directory for this CodeNarc file. If null then the project directory is assumed.
     *
     * @return the base directory for the file, or null if not applicable to the location type.
     */
    public File getBaseDir() {
        return null;
    }

    public ConfigurationType getType() {
        return type;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(final String location) {
        if (location == null || location.trim().length() == 0) {
            throw new IllegalArgumentException("A non-blank location is required");
        }

        this.location = location;

        this.propertiesCheckedThisSession = false;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(@Nullable final String description) {
        if (description == null) {
            this.description = location;
        } else {
            this.description = description;
        }
    }

    public Map<String, String> getProperties() throws IOException {
        if (!propertiesCheckedThisSession) {
            resolveFile();
        }

        return Collections.unmodifiableMap(properties);
    }

    public void setProperties(final Map<String, String> newProperties) {
        properties.clear();

        if (newProperties == null) {
            return;
        }

        properties.putAll(newProperties);

        this.propertiesCheckedThisSession = false;
    }

    /**
     * Resolve this location to a file.
     *
     * @return the file to load.
     * @throws IOException if the file cannot be loaded.
     */
    public URL resolve() throws IOException {
        URL url = resolveFile();

        if (!propertiesCheckedThisSession) {

            propertiesCheckedThisSession = true;
        }

        return url;
    }

    public final boolean hasChangedFrom(final ConfigurationLocation configurationLocation) throws IOException {
        return configurationLocation == null
                || !equals(configurationLocation)
                || !getProperties().equals(configurationLocation.getProperties());

    }

    public String getDescriptor() {
        assert type != null;
        assert location != null;
        assert description != null;

        return type + ":" + location + ":" + description;
    }

    /**
     * Resolve this location to a file.
     *
     * @return the file to load.
     * @throws IOException if the file cannot be loaded.
     */
    protected abstract URL resolveFile() throws IOException;

    @Override
    public abstract Object clone();

    ConfigurationLocation cloneCommonPropertiesTo(final ConfigurationLocation cloned) {
        cloned.setDescription(getDescription());
        cloned.setLocation(getLocation());
        try {
            cloned.setProperties(new HashMap<String, String>(getProperties()));
        } catch (IOException e) {
            throw new RuntimeException("Failed to resolve properties for " + this);
        }
        return cloned;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final ConfigurationLocation that = (ConfigurationLocation) o;

        if (description != null ? !description.equals(that.description) : that.description != null) {
            return false;
        }
        if (location != null ? !location.equals(that.location) : that.location != null) {
            return false;
        }
        if (type != that.type) {
            return false;
        }

        return true;
    }

    @Override
    public final int hashCode() {
        int result = type != null ? type.hashCode() : 0;
        result = 31 * result + (location != null ? location.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        assert description != null;

        return description;
    }

    public boolean isBlacklisted() {
        return blacklistedUntil > System.currentTimeMillis();
    }

    public void blacklist() {
        blacklistedUntil = System.currentTimeMillis() + BLACKLIST_TIME_MS;
    }

    public void removeFromBlacklist() {
        blacklistedUntil = 0L;
    }
}
