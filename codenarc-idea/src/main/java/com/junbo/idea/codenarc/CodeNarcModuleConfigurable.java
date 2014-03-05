package com.junbo.idea.codenarc;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleServiceManager;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.junbo.idea.codenarc.ui.CodeNarcModuleConfigPanel;
import com.junbo.idea.codenarc.util.IDEAUtilities;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class CodeNarcModuleConfigurable implements Configurable {

    private final Module module;

    public CodeNarcModuleConfigurable(@NotNull final Module module) {
        this.module = module;
    }

    private CodeNarcModuleConfigPanel configPanel;

    public String getDisplayName() {
        return IDEAUtilities.getResource("plugin.configuration-name", "CodeNarc Plugin");
    }

    public String getHelpTopic() {
        return null;
    }

    public JComponent createComponent() {
        if (configPanel == null) {
            configPanel = new CodeNarcModuleConfigPanel();
        }

        reset();

        return configPanel;
    }

    public boolean isModified() {
        return configPanel != null && configPanel.isModified();
    }

    public void apply() throws ConfigurationException {
        if (configPanel == null) {
            return;
        }

        final CodeNarcModuleConfiguration configuration = getConfiguration();
        configuration.setActiveConfiguration(configPanel.getActiveLocation());
        configuration.setExcluded(configPanel.isExcluded());

        reset();
    }

    private CodeNarcModuleConfiguration getConfiguration() {
        return ModuleServiceManager.getService(module, CodeNarcModuleConfiguration.class);
    }

    public void reset() {
        if (configPanel == null) {
            return;
        }

        final CodeNarcModuleConfiguration configuration = getConfiguration();

        configPanel.setConfigurationLocations(configuration.getConfigurationLocations());

        if (configuration.isExcluded()) {
            configPanel.setExcluded(true);
        } else if (configuration.isUsingModuleConfiguration()) {
            configPanel.setActiveLocation(configuration.getActiveConfiguration());
        } else {
            configPanel.setActiveLocation(null);
        }
    }

    public void disposeUIResources() {
        configPanel = null;
    }
}
