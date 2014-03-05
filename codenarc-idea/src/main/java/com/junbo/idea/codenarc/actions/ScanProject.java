package com.junbo.idea.codenarc.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.junbo.idea.codenarc.CodeNarcConstants;
import com.junbo.idea.codenarc.CodeNarcPlugin;

/**
 * Action to execute a CodeNarc scan on the current project.
 */
public class ScanProject extends BaseAction {

    @Override
    public void actionPerformed(final AnActionEvent event) {
        try {
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
            toolWindow.activate(new Runnable() {
                @Override
                public void run() {
                    try {
                        setProgressText(toolWindow, "plugin.status.in-progress.current");

                        final ProjectRootManager projectRootManager = ProjectRootManager.getInstance(project);
                        final VirtualFile[] sourceRoots = projectRootManager.getContentSourceRoots();

                        if (sourceRoots != null && sourceRoots.length > 0) {
                            ApplicationManager.getApplication().runReadAction(
                                    new ScanSourceRootsAction(project, sourceRoots, getSelectedOverride(toolWindow)));
                        }

                    } catch (Throwable e) {
                        CodeNarcPlugin.processErrorAndLog("Project scan", e);
                    }
                }
            });

        } catch (Throwable e) {
            CodeNarcPlugin.processErrorAndLog("Project scan", e);
        }
    }

    @Override
    public void update(final AnActionEvent event) {
        super.update(event);

        try {
            final Presentation presentation = event.getPresentation();

            final Project project = DataKeys.PROJECT.getData(event.getDataContext());
            if (project == null) { // check if we're loading...
                presentation.setEnabled(false);
                return;
            }

            final CodeNarcPlugin codeNarcPlugin
                    = project.getComponent(CodeNarcPlugin.class);
            if (codeNarcPlugin == null) {
                throw new IllegalStateException("Couldn't get CodeNarc plugin");
            }

            final ProjectRootManager projectRootManager
                    = ProjectRootManager.getInstance(project);
            final VirtualFile[] sourceRoots
                    = projectRootManager.getContentSourceRoots();

            // disable if no files are selected
            if (sourceRoots == null || sourceRoots.length == 0) {
                presentation.setEnabled(false);

            } else {
                presentation.setEnabled(!codeNarcPlugin.isScanInProgress());
            }
        } catch (Throwable e) {
            CodeNarcPlugin.processErrorAndLog("Project button update", e);
        }
    }

}
