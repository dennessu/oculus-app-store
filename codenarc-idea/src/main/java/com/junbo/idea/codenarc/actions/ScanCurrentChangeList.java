package com.junbo.idea.codenarc.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vcs.changes.Change;
import com.intellij.openapi.vcs.changes.ChangeListManager;
import com.intellij.openapi.vcs.changes.LocalChangeList;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.junbo.idea.codenarc.CodeNarcConstants;
import com.junbo.idea.codenarc.CodeNarcPlugin;
import com.junbo.idea.codenarc.exception.CodeNarcPluginException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.*;

/**
 * Scan files in the current change-list.
 */
public class ScanCurrentChangeList extends BaseAction {

    private static final Log LOG = LogFactory.getLog(
            ScanCurrentChangeList.class);

    @Override
    public final void actionPerformed(final AnActionEvent event) {
        try {
            final Project project = DataKeys.PROJECT.getData(event.getDataContext());
            if (project == null) {
                return;
            }

            final ToolWindow toolWindow = ToolWindowManager.getInstance(
                    project).getToolWindow(CodeNarcConstants.ID_TOOLWINDOW);

            final ChangeListManager changeListManager = ChangeListManager.getInstance(project);
            project.getComponent(CodeNarcPlugin.class).checkFiles(filesFor(changeListManager.getDefaultChangeList()), getSelectedOverride(toolWindow));

        } catch (Throwable e) {
            final CodeNarcPluginException processed = CodeNarcPlugin.processError(null, e);
            if (processed != null) {
                LOG.error("Modified files scan failed", processed);
            }
        }
    }

    private List<VirtualFile> filesFor(final LocalChangeList changeList) {
        if (changeList == null || changeList.getChanges() == null) {
            return Collections.emptyList();
        }

        final Collection<VirtualFile> filesInChanges = new HashSet<VirtualFile>();
        for (Change change : changeList.getChanges()) {
            if (change.getVirtualFile() != null) {
                filesInChanges.add(change.getVirtualFile());
            }
        }

        return new ArrayList<VirtualFile>(filesInChanges);
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

            final LocalChangeList changeList = ChangeListManager.getInstance(project).getDefaultChangeList();
            if (changeList == null
                    || changeList.getChanges() == null
                    || changeList.getChanges().size() == 0) {
                presentation.setEnabled(false);

            } else {
                presentation.setEnabled(!codeNarcPlugin.isScanInProgress());
            }

        } catch (Throwable e) {
            final CodeNarcPluginException processed
                    = CodeNarcPlugin.processError(null, e);
            if (processed != null) {
                LOG.error("Button update failed.", processed);
            }
        }
    }
}
