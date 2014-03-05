package com.junbo.idea.codenarc;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleComponent;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jetbrains.annotations.NotNull;

/**
 * The module level plugin.
 * <p/>
 * This is used to allow modules to override the chosen CodeNarc configuration.
 */
public class CodeNarcModulePlugin implements ModuleComponent {

    private static final Log LOG = LogFactory.getLog(CodeNarcModulePlugin.class);

    public CodeNarcModulePlugin(@NotNull final Module module) {
        LOG.info("CodeNarc Module Plugin loaded for module: \"" + module.getName() + "\"");
    }

    public void projectOpened() {
        LOG.debug("Project opened.");
    }

    public void projectClosed() {
        LOG.debug("Project closed.");
    }

    public void moduleAdded() {
        LOG.debug("Module added.");
    }

    @NotNull
    public String getComponentName() {
        return CodeNarcConstants.ID_MODULE_PLUGIN;
    }

    public void initComponent() {
    }

    public void disposeComponent() {
    }

}
