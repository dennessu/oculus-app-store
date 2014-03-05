package com.junbo.idea.codenarc.actions;

import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.junbo.idea.codenarc.CodeNarcConstants;
import com.junbo.idea.codenarc.CodeNarcPlugin;
import com.junbo.idea.codenarc.util.CodeNarcUtilities;

import java.util.Arrays;

/**
 * Action to execute a CodeNarc scan on the current editor file.
 */
public class ScanCurrentFile extends BaseAction {

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
                    try {
                        setProgressText(toolWindow, "plugin.status.in-progress.current");

                        final VirtualFile selectedFile = getSelectedFile(project);
                        if (selectedFile != null) {
                            project.getComponent(CodeNarcPlugin.class).checkFiles(
                                    Arrays.asList(selectedFile), getSelectedOverride(toolWindow));
                        }

                    } catch (Throwable e) {
                        CodeNarcPlugin.processErrorAndLog("Current File scan", e);
                    }
                }
            });

        } catch (Throwable e) {
            CodeNarcPlugin.processErrorAndLog("Current File scan", e);
        }
    }

    private VirtualFile getSelectedFile(final Project project) {
        final Editor selectedTextEditor = FileEditorManager.getInstance(project).getSelectedTextEditor();
        if (selectedTextEditor != null) {
            final VirtualFile selectedFile = FileDocumentManager.getInstance().getFile(selectedTextEditor.getDocument());
            if (selectedFile != null) {
                return selectedFile;
            }
        }

        // this is the preferred solution, but it doesn't respect the focus of split editors at present
        final VirtualFile[] selectedFiles = FileEditorManager.getInstance(project).getSelectedFiles();
        if (selectedFiles != null && selectedFiles.length > 0) {
            return selectedFiles[0];
        }

        return null;
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

            final boolean scanOnlyJavaFiles = !codeNarcPlugin.getConfiguration().isScanningNonGroovyFiles();
            final VirtualFile selectedFile = getSelectedFile(project);

            // disable if no files are selected
            final Presentation presentation = event.getPresentation();
            if (selectedFile == null) {
                presentation.setEnabled(false);

            } else if (scanOnlyJavaFiles) {
                // check files are valid
                if (!CodeNarcUtilities.isGroovyFile(selectedFile.getFileType())) {
                    presentation.setEnabled(false);
                    return;
                }

                presentation.setEnabled(!codeNarcPlugin.isScanInProgress());
            }
        } catch (Throwable e) {
            CodeNarcPlugin.processErrorAndLog("Current File button update", e);
        }
    }
}
