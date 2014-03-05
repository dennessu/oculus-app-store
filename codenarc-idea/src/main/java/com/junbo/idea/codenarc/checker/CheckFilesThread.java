package com.junbo.idea.codenarc.checker;

import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.junbo.idea.codenarc.CodeNarcPlugin;
import com.junbo.idea.codenarc.exception.CodeNarcPluginException;
import com.junbo.idea.codenarc.toolwindow.CodeNarcToolWindowPanel;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.junbo.idea.codenarc.model.ConfigurationLocation;
import com.junbo.idea.codenarc.util.ModuleClassPathBuilder;

import javax.swing.*;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;

public class CheckFilesThread extends AbstractCheckerThread {
    private static final Log LOG = LogFactory.getLog(CheckFilesThread.class);

    /**
     * Create a thread to check the given files.
     *
     * @param codeNarcPlugin       CodeNarcPlugin.
     * @param moduleClassPathBuilder the class path builder.
     * @param virtualFiles           the files to check.
     * @param overrideConfigLocation if non-null this configuration will be used in preference to the normal configuration.
     */
    public CheckFilesThread(final CodeNarcPlugin codeNarcPlugin,
                            final ModuleClassPathBuilder moduleClassPathBuilder,
                            final List<VirtualFile> virtualFiles,
                            final ConfigurationLocation overrideConfigLocation) {
        super(codeNarcPlugin, moduleClassPathBuilder, virtualFiles, overrideConfigLocation);
        this.setFileResults(new HashMap<PsiFile, List<ProblemDescriptor>>());
    }

    @Override
    public void runFileScanner(final FileScanner fileScanner) throws InterruptedException, InvocationTargetException {
        ApplicationManager.getApplication().runReadAction(fileScanner);
    }

    /**
     * Execute the file check.
     */
    @Override
    public void run() {
        setRunning(true);

        try {
            // set progress bar
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    final CodeNarcToolWindowPanel toolWindowPanel = toolWindowPanel();
                    if (toolWindowPanel != null) {
                        toolWindowPanel.displayInProgress(getFiles().size());
                    }
                }
            });

            this.processFilesForModuleInfoAndScan();

            // invoke Swing fun in Swing thread.
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    final CodeNarcToolWindowPanel toolWindowPanel = toolWindowPanel();
                    if (toolWindowPanel != null) {
                        switch (getConfigurationLocationStatus()) {
                            case NOT_PRESENT:
                                toolWindowPanel.displayWarningResult("plugin.results.no-rules-file");
                                break;
                            case BLACKLISTED:
                                toolWindowPanel.displayWarningResult("plugin.results.rules-blacklist");
                                break;
                            default:
                                toolWindowPanel.displayResults(getFileResults());
                        }
                    }
                    markThreadComplete();
                }
            });

        } catch (final Throwable e) {
            final CodeNarcPluginException processedError = CodeNarcPlugin.processError(
                    "An error occurred during a file scan.", e);

            if (processedError != null) {
                LOG.error("An error occurred while scanning a file.", processedError);

                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        final CodeNarcToolWindowPanel toolWindowPanel = toolWindowPanel();
                        if (toolWindowPanel != null) {
                            toolWindowPanel.displayErrorResult(processedError);
                        }
                        markThreadComplete();
                    }
                });
            }
        }
    }

}
