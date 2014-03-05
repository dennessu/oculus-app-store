package com.junbo.idea.codenarc.checker;

import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.junbo.idea.codenarc.CodeNarcPlugin;
import com.junbo.idea.codenarc.exception.CodeNarcPluginException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.junbo.idea.codenarc.util.ModuleClassPathBuilder;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

public class ScanFilesThread extends AbstractCheckerThread {

    /**
     * Logger for this class.
     */
    private static final Log LOG = LogFactory.getLog(ScanFilesThread.class);

    /**
     * Scan Files and store results.
     *
     * @param codeNarcPlugin       reference to the CodeNarcPlugin
     * @param moduleClassPathBuilder the class path builder.
     * @param vFiles                 files to scan
     * @param results                Map to store scan results
     */
    public ScanFilesThread(final CodeNarcPlugin codeNarcPlugin,
                           final ModuleClassPathBuilder moduleClassPathBuilder,
                           final List<VirtualFile> vFiles,
                           final Map<PsiFile, List<ProblemDescriptor>> results) {
        super(codeNarcPlugin, moduleClassPathBuilder, vFiles, null);
        this.setFileResults(results);
    }

    /**
     * Run scan against files.
     */
    public void run() {
        setRunning(true);

        try {
            this.processFilesForModuleInfoAndScan();

        } catch (final Throwable e) {
            final CodeNarcPluginException processedError = CodeNarcPlugin.processError(
                    "An error occurred during a file scan.", e);

            if (processedError != null) {
                LOG.error("An error occurred while scanning a file.",
                        processedError);
            }
        }
    }


    public void runFileScanner(final FileScanner fileScanner) throws InterruptedException, InvocationTargetException {
        fileScanner.run();
    }

}
