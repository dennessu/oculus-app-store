package com.junbo.idea.codenarc.model;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * A configuration file accessible via the IDE classpath.
 */
public class ClassPathConfigurationLocation extends ConfigurationLocation {

    /**
     * Create a new classpath configuration.
     */
    ClassPathConfigurationLocation() {
        super(ConfigurationType.CLASSPATH);
    }

    protected URL resolveFile() throws IOException {
        final URL in = ClassPathConfigurationLocation.class.getResource(getLocation());
        if (in == null) {
            throw new IOException("Invalid classpath location: " + getLocation());
        }

        return in;
    }

    @Override
    public Object clone() {
        return cloneCommonPropertiesTo(new ClassPathConfigurationLocation());
    }
}
