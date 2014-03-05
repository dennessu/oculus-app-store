package com.junbo.idea.codenarc.handlers;

import com.intellij.CommonBundle;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.openapi.progress.ProcessCanceledException;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vcs.CheckinProjectPanel;
import com.intellij.openapi.vcs.changes.CommitExecutor;
import com.intellij.openapi.vcs.checkin.CheckinHandler;
import com.intellij.openapi.vcs.ui.RefreshableOnComponent;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.util.PairConsumer;
import com.intellij.util.ui.UIUtil;
import com.junbo.idea.codenarc.CodeNarcConfiguration;
import com.junbo.idea.codenarc.CodeNarcPlugin;
import com.junbo.idea.codenarc.toolwindow.CodeNarcToolWindowPanel;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.junbo.idea.codenarc.util.IDEAUtilities.getResource;

/**
 * Before Checkin Handler to scan files with CodeNarc.
 */
public class ScanFilesBeforeCheckinHandler extends CheckinHandler {
    private static final Log LOG = LogFactory.getLog(ScanFilesBeforeCheckinHandler.class);

    private final CheckinProjectPanel checkinPanel;

    public ScanFilesBeforeCheckinHandler(final CheckinProjectPanel myCheckinPanel) {
        if (myCheckinPanel == null) {
            throw new IllegalArgumentException("CheckinPanel is required");
        }

        this.checkinPanel = myCheckinPanel;
    }

    @Nullable
    public RefreshableOnComponent getBeforeCheckinConfigurationPanel() {
        final JCheckBox checkBox = new JCheckBox(getResource("handler.before.checkin.checkbox", "Scan with CodeNarc"));

        return new RefreshableOnComponent() {
            public JComponent getComponent() {
                final JPanel panel = new JPanel(new BorderLayout());
                panel.add(checkBox);
                return panel;
            }

            public void refresh() {
            }

            public void saveState() {
                getSettings().setScanFilesBeforeCheckin(checkBox.isSelected());
            }

            public void restoreState() {
                checkBox.setSelected(getSettings().isScanFilesBeforeCheckin());
            }
        };
    }

    @Override
    public ReturnResult beforeCheckin(@Nullable final CommitExecutor executor,
                                      final PairConsumer<Object, Object> additionalDataConsumer) {
        final Project project = checkinPanel.getProject();
        if (project == null) {
            LOG.error("Could not get project for check-in panel, skipping");
            return ReturnResult.COMMIT;
        }

        final CodeNarcPlugin plugin = project.getComponent(CodeNarcPlugin.class);
        if (plugin == null) {
            LOG.error("Could not get CodeNarc Plug-in, skipping");
            return ReturnResult.COMMIT;
        }

        if (plugin.getConfiguration().isScanFilesBeforeCheckin()) {
            try {
                final Map<PsiFile, List<ProblemDescriptor>> scanResults = new HashMap<PsiFile, List<ProblemDescriptor>>();
                new Task.Modal(project, getResource("handler.before.checkin.scan.text", "CodeNarc is Scanning"), false) {
                    public void run(@NotNull final ProgressIndicator progressIndicator) {
                        progressIndicator.setText(getResource("handler.before.checkin.scan.in-progress", "Scanning..."));
                        progressIndicator.setIndeterminate(true);
                        plugin.scanFiles(new ArrayList<VirtualFile>(checkinPanel.getVirtualFiles()), scanResults);
                    }
                }.queue();

                if (!scanResults.isEmpty()) {
                    return processScanResults(scanResults, executor, plugin);
                }
                return ReturnResult.COMMIT;

            } catch (ProcessCanceledException e) {
                return ReturnResult.CANCEL;
            }

        } else {
            return ReturnResult.COMMIT;
        }
    }

    private CodeNarcConfiguration getSettings() {
        final Project project = checkinPanel.getProject();
        if (project == null) {
            LOG.error("Could not get project for check-in panel");
            return null;
        }

        final CodeNarcPlugin plugin = project.getComponent(CodeNarcPlugin.class);
        if (plugin == null) {
            LOG.error("Could not get CodeNarc Plug-in, skipping");
            return null;
        }

        return plugin.getConfiguration();
    }

    private ReturnResult processScanResults(final Map<PsiFile, List<ProblemDescriptor>> results,
                                            final CommitExecutor executor,
                                            final CodeNarcPlugin plugin) {
        final int errorCount = results.keySet().size();

        final int answer = promptUser(plugin, errorCount, executor);
        if (answer == Messages.OK) {
            showResultsInToolWindow(results, plugin);
            return ReturnResult.CLOSE_WINDOW;

        } else if (answer == Messages.CANCEL || answer < 0) {
            return ReturnResult.CANCEL;
        }

        return ReturnResult.COMMIT;
    }

    private int promptUser(final CodeNarcPlugin plugin,
                           final int errorCount,
                           final CommitExecutor executor) {
        String commitButtonText;
        if (executor != null) {
            commitButtonText = executor.getActionText();
        } else {
            commitButtonText = checkinPanel.getCommitActionName();
        }

        if (commitButtonText.endsWith("...")) {
            commitButtonText = commitButtonText.substring(0, commitButtonText.length() - 3);
        }

        final String[] buttons = new String[]{getResource("handler.before.checkin.error.review", "Review"),
                commitButtonText, CommonBundle.getCancelButtonText()};

        final MessageFormat errorFormat = new MessageFormat(getResource("handler.before.checkin.error.text", "{0} files contain problems"));
        return Messages.showDialog(plugin.getProject(), errorFormat.format(new Object[]{errorCount}),
                getResource("handler.before.checkin.error.title", "CodeNarc Scan"),
                buttons, 0, UIUtil.getWarningIcon());
    }

    private void showResultsInToolWindow(final Map<PsiFile, List<ProblemDescriptor>> results,
                                         final CodeNarcPlugin plugin) {
        final CodeNarcToolWindowPanel toolWindowPanel = CodeNarcToolWindowPanel.panelFor(plugin.getProject());
        if (toolWindowPanel != null) {
            toolWindowPanel.displayResults(results);
            toolWindowPanel.showToolWindow();
        }
    }

}
