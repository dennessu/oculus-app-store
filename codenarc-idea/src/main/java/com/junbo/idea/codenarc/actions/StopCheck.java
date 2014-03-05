package com.junbo.idea.codenarc.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.junbo.idea.codenarc.CodeNarcConstants;
import com.junbo.idea.codenarc.CodeNarcPlugin;

/**
 * Action to stop a check in progress.
 */
public class StopCheck extends BaseAction {

    @Override
    public void actionPerformed(final AnActionEvent event) {
        final Project project = DataKeys.PROJECT.getData(event.getDataContext());
        if (project == null) {
            return;
        }

        try {
            final CodeNarcPlugin codeNarcPlugin
                    = project.getComponent(CodeNarcPlugin.class);
            if (codeNarcPlugin == null) {
                throw new IllegalStateException("Couldn't get CodeNarc plugin");
            }

            final ToolWindow toolWindow = ToolWindowManager.getInstance(
                    project).getToolWindow(CodeNarcConstants.ID_TOOLWINDOW);
            toolWindow.activate(new Runnable() {
                @Override
                public void run() {
                    setProgressText(toolWindow, "plugin.status.in-progress.current");

                    codeNarcPlugin.stopChecks();

                    setProgressText(toolWindow, "plugin.status.aborted");
                }
            });

        } catch (Throwable e) {
            CodeNarcPlugin.processErrorAndLog("Abort Scan", e);
        }
    }

    @Override
    public void update(final AnActionEvent event) {
        super.update(event);

        try {
            final Project project = DataKeys.PROJECT.getData(event.getDataContext());
            if (project == null) { // check if we're loading...
                return;
            }

            final CodeNarcPlugin codeNarcPlugin
                    = project.getComponent(CodeNarcPlugin.class);
            if (codeNarcPlugin == null) {
                throw new IllegalStateException("Couldn't get CodeNarc plugin");
            }

            final Presentation presentation = event.getPresentation();
            presentation.setEnabled(codeNarcPlugin.isScanInProgress());

        } catch (Throwable e) {
            CodeNarcPlugin.processErrorAndLog("Abort button update", e);
        }
    }
}
