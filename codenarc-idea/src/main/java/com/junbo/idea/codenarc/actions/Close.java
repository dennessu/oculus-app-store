package com.junbo.idea.codenarc.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.junbo.idea.codenarc.CodeNarcConstants;
import com.junbo.idea.codenarc.CodeNarcPlugin;

/**
 * Action to close the tool window.
 */
public class Close extends BaseAction {

    @Override
    public void actionPerformed(final AnActionEvent event) {
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
        toolWindow.hide(null);
    }

}
