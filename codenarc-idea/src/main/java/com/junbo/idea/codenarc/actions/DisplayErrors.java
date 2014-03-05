package com.junbo.idea.codenarc.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.actionSystem.ToggleAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.content.Content;
import com.junbo.idea.codenarc.CodeNarcConstants;
import com.junbo.idea.codenarc.CodeNarcPlugin;
import com.junbo.idea.codenarc.toolwindow.CodeNarcToolWindowPanel;

/**
 * Action to toggle error display in tool window.
 */
public class DisplayErrors extends ToggleAction {

    @Override
    public boolean isSelected(final AnActionEvent event) {
        final Project project = DataKeys.PROJECT.getData(event.getDataContext());
        if (project == null) {
            return false;
        }

        final CodeNarcPlugin codeNarcPlugin
                = project.getComponent(CodeNarcPlugin.class);
        if (codeNarcPlugin == null) {
            throw new IllegalStateException("Couldn't get CodeNarc plugin");
        }

        final ToolWindow toolWindow = ToolWindowManager.getInstance(
                project).getToolWindow(CodeNarcConstants.ID_TOOLWINDOW);

        final Content content = toolWindow.getContentManager().getContent(0);
        if (content != null) {
            final CodeNarcToolWindowPanel panel = (CodeNarcToolWindowPanel) content.getComponent();
            return panel.isDisplayingErrors();
        }

        return false;
    }

    @Override
    public void setSelected(final AnActionEvent event, final boolean selected) {
        final Project project = DataKeys.PROJECT.getData(event.getDataContext());
        if (project == null) {
            return;
        }

        final CodeNarcPlugin codeNarcPlugin
                = project.getComponent(CodeNarcPlugin.class);
        if (codeNarcPlugin == null) {
            throw new IllegalStateException("Couldn't get CodeNarc plugin");
        }

        final ToolWindow toolWindow = ToolWindowManager.getInstance(
                project).getToolWindow(CodeNarcConstants.ID_TOOLWINDOW);

        final Content content = toolWindow.getContentManager().getContent(0);
        if (content != null) {
            final CodeNarcToolWindowPanel panel = (CodeNarcToolWindowPanel) content.getComponent();
            panel.setDisplayingErrors(selected);
            panel.filterDisplayedResults();
        }
    }
}
