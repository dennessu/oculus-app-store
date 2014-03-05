package com.junbo.idea.codenarc.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.junbo.idea.codenarc.CodeNarcConstants;
import com.junbo.idea.codenarc.CodeNarcPlugin;

/**
 * Action to execute a CodeNarc scan on the current module.
 */
public class ScanModule extends BaseAction {

    @Override
    public final void actionPerformed(final AnActionEvent event) {
        try {
            final Project project = DataKeys.PROJECT.getData(event.getDataContext());
            if (project == null) {
                return;
            }

            final ToolWindow toolWindow = ToolWindowManager.getInstance(
                    project).getToolWindow(CodeNarcConstants.ID_TOOLWINDOW);

            final VirtualFile[] selectedFiles
                    = FileEditorManager.getInstance(project).getSelectedFiles();
            if (selectedFiles.length == 0) {
                setProgressText(toolWindow, "plugin.status.in-progress.no-file");
                return;
            }

            final Module module = ModuleUtil.findModuleForFile(
                    selectedFiles[0], project);
            if (module == null) {
                setProgressText(toolWindow, "plugin.status.in-progress.no-module");
                return;
            }

            final CodeNarcPlugin codeNarcPlugin
                    = project.getComponent(CodeNarcPlugin.class);
            if (codeNarcPlugin == null) {
                throw new IllegalStateException("Couldn't get CodeNarc plugin");
            }

            toolWindow.activate(new Runnable() {
                @Override
                public void run() {
                    try {
                        setProgressText(toolWindow, "plugin.status.in-progress.current");

                        final VirtualFile[] moduleSourceRoots = ModuleRootManager.getInstance(module).getSourceRoots();
                        if (moduleSourceRoots.length > 0) {
                            ApplicationManager.getApplication().runReadAction(
                                    new ScanSourceRootsAction(project, moduleSourceRoots, getSelectedOverride(toolWindow)));
                        }

                    } catch (Throwable e) {
                        CodeNarcPlugin.processErrorAndLog("Current Module scan", e);
                    }
                }
            });

        } catch (Throwable e) {
            CodeNarcPlugin.processErrorAndLog("Current Module scan", e);
        }
    }

    @Override
    public final void update(final AnActionEvent event) {
        super.update(event);

        try {
            final Presentation presentation = event.getPresentation();

            final Project project = DataKeys.PROJECT.getData(event.getDataContext());
            if (project == null) { // check if we're loading...
                presentation.setEnabled(false);
                return;
            }

            final VirtualFile[] selectedFiles
                    = FileEditorManager.getInstance(project).getSelectedFiles();
            if (selectedFiles.length == 0) {
                return;
            }

            final Module module = ModuleUtil.findModuleForFile(
                    selectedFiles[0], project);
            if (module == null) {
                return;
            }

            final CodeNarcPlugin codeNarcPlugin
                    = project.getComponent(CodeNarcPlugin.class);
            if (codeNarcPlugin == null) {
                throw new IllegalStateException("Couldn't get CodeNarc plugin");
            }

            final ModuleRootManager moduleRootManager
                    = ModuleRootManager.getInstance(module);
            final VirtualFile[] moduleFiles = moduleRootManager.getSourceRoots();

            // disable if no files are selected
            if (moduleFiles.length == 0) {
                presentation.setEnabled(false);

            } else {
                presentation.setEnabled(!codeNarcPlugin.isScanInProgress());
            }
        } catch (Throwable e) {
            CodeNarcPlugin.processErrorAndLog("Current Module button update", e);
        }
    }
}
