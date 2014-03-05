package com.junbo.idea.codenarc.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.content.Content;
import com.junbo.idea.codenarc.CodeNarcConstants;
import com.junbo.idea.codenarc.CodeNarcPlugin;
import com.junbo.idea.codenarc.exception.CodeNarcPluginException;
import com.junbo.idea.codenarc.toolwindow.CodeNarcToolWindowPanel;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.junbo.idea.codenarc.model.ConfigurationLocation;

import java.util.ResourceBundle;

/**
 * Base class for plug-in actions.
 */
public abstract class BaseAction extends AnAction {

    private static final Log LOG = LogFactory.getLog(
            BaseAction.class);

    @Override
    public void update(final AnActionEvent event) {
        try {
            final Project project = DataKeys.PROJECT.getData(event.getDataContext());
            final Presentation presentation = event.getPresentation();

            // check a project is loaded
            if (project == null) {
                presentation.setEnabled(false);
                presentation.setVisible(false);

                return;

            }

            final CodeNarcPlugin codeNarcPlugin
                    = project.getComponent(CodeNarcPlugin.class);
            if (codeNarcPlugin == null) {
                throw new IllegalStateException("Couldn't get CodeNarc plugin");
            }

            // check if tool window is registered
            final ToolWindow toolWindow = ToolWindowManager.getInstance(
                    project).getToolWindow(CodeNarcConstants.ID_TOOLWINDOW);
            if (toolWindow == null) {
                presentation.setEnabled(false);
                presentation.setVisible(false);

                return;
            }

            // enable
            presentation.setEnabled(toolWindow.isAvailable());
            presentation.setVisible(true);

        } catch (Throwable e) {
            final CodeNarcPluginException processed
                    = CodeNarcPlugin.processError(null, e);
            if (processed != null) {
                LOG.error("Action update failed", processed);
            }
        }
    }

    protected void setProgressText(final ToolWindow toolWindow, final String progressTextKey) {
        final Content content = toolWindow.getContentManager().getContent(0);
        // the content instance will be a JLabel while the component initialises
        if (content != null && content.getComponent() instanceof CodeNarcToolWindowPanel) {
            final ResourceBundle resources = ResourceBundle.getBundle(CodeNarcConstants.RESOURCE_BUNDLE);
            final CodeNarcToolWindowPanel panel = (CodeNarcToolWindowPanel) content.getComponent();
            panel.setProgressText(resources.getString(progressTextKey));
        }
    }

    protected ConfigurationLocation getSelectedOverride(final ToolWindow toolWindow) {
        final Content content = toolWindow.getContentManager().getContent(0);
        // the content instance will be a JLabel while the component initialises
        if (content != null && content.getComponent() instanceof CodeNarcToolWindowPanel) {
            return ((CodeNarcToolWindowPanel) content.getComponent()).getSelectedOverride();
        }
        return null;
    }
}
