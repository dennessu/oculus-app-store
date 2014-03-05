package com.junbo.idea.codenarc;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.Project;
import com.junbo.idea.codenarc.ui.CodeNarcConfigPanel;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.junbo.idea.codenarc.checker.CheckerFactory;
import com.junbo.idea.codenarc.util.IDEAUtilities;
import com.junbo.idea.codenarc.util.ModuleClassPathBuilder;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.io.IOException;
import java.util.List;

public class CodeNarcConfigurable implements Configurable {
    private static final Log LOG = LogFactory.getLog(CodeNarcConfigurable.class);

    private final Project project;

    private CodeNarcConfigPanel configPanel;

    public CodeNarcConfigurable(@NotNull final Project project) {
        this.project = project;

        configPanel = new CodeNarcConfigPanel(project);
    }

    public String getDisplayName() {
        return IDEAUtilities.getResource("plugin.configuration-name", "CodeNarc Plugin");
    }

    public String getHelpTopic() {
        return null;
    }

    public JComponent createComponent() {
        if (configPanel == null) {
            return null;
        }

        reset();

        return configPanel;
    }

    public boolean isModified() {
        try {
            return configPanel != null && configPanel.isModified();

        } catch (IOException e) {
            LOG.error("Failed to read properties from one of " + configPanel.getConfigurationLocations(), e);
            IDEAUtilities.showError(project,
                    IDEAUtilities.getResource("codenarc.file-not-found", "The CodeNarc file could not be read."));
            return true;
        }
    }

    public void apply() throws ConfigurationException {
        if (configPanel == null) {
            return;
        }

        final CodeNarcConfiguration configuration = getConfiguration();
        configuration.setConfigurationLocations(configPanel.getConfigurationLocations());
        configuration.setActiveConfiguration(configPanel.getActiveLocation());

        configuration.setScanningTestClasses(configPanel.isScanTestClasses());
        configuration.setScanningNonGroovyFiles(configPanel.isScanNonGroovyFiles());
        configuration.setSuppressingErrors(configPanel.isSuppressingErrors());

        final List<String> thirdPartyClasspath
                = configPanel.getThirdPartyClasspath();
        configuration.setThirdPartyClassPath(thirdPartyClasspath);

        reset(); // save current data as unmodified

        getCheckerFactory().invalidateCache();

        resetModuleClassBuilder();
    }

    private void resetModuleClassBuilder() {
        final ModuleClassPathBuilder moduleClassPathBuilder = ServiceManager.getService(project, ModuleClassPathBuilder.class);
        if (moduleClassPathBuilder != null) {
            moduleClassPathBuilder.reset();
        }
    }

    private CodeNarcConfiguration getConfiguration() {
        return ServiceManager.getService(project, CodeNarcConfiguration.class);
    }

    private CheckerFactory getCheckerFactory() {
        return ServiceManager.getService(CheckerFactory.class);
    }

    public void reset() {
        if (configPanel == null) {
            return;
        }

        final CodeNarcConfiguration configuration = getConfiguration();
        configPanel.setConfigurationLocations(configuration.getConfigurationLocations());
        configPanel.setPresetLocations(configuration.getPresetLocations());
        configPanel.setActiveLocation(configuration.getActiveConfiguration());
        configPanel.setScanTestClasses(configuration.isScanningTestClasses());
        configPanel.setScanNonGroovyFiles(configuration.isScanningNonGroovyFiles());
        configPanel.setSuppressingErrors(configuration.isSuppressingErrors());
        configPanel.setThirdPartyClasspath(configuration.getThirdPartyClassPath());
    }

    public void disposeUIResources() {

    }

}
