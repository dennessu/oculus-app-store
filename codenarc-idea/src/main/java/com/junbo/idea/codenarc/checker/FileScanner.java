package com.junbo.idea.codenarc.checker;

import com.intellij.codeInspection.InspectionManager;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.openapi.application.AccessToken;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleServiceManager;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.junbo.idea.codenarc.CodeNarcModuleConfiguration;
import com.junbo.idea.codenarc.analyzer.FilesSourceAnalyzer;
import com.junbo.idea.codenarc.exception.CodeNarcPluginException;
import com.junbo.idea.codenarc.toolwindow.CodeNarcToolWindowPanel;
import com.junbo.idea.codenarc.util.CodeNarcUtilities;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codenarc.results.Results;
import org.codenarc.ruleset.RuleSet;
import com.junbo.idea.codenarc.CodeNarcPlugin;
import com.junbo.idea.codenarc.model.ConfigurationLocation;
import com.junbo.idea.codenarc.util.ScannableFile;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Runnable for scanning an individual file.
 */
final class FileScanner implements Runnable {

    private static final Log LOG = LogFactory.getLog(FileScanner.class);

    private final CodeNarcPlugin plugin;
    private final Set<PsiFile> filesToScan;
    private final ClassLoader moduleClassLoader;
    private final ConfigurationLocation overrideConfigLocation;

    private ConfigurationLocationStatus configurationLocationStatus = ConfigurationLocationStatus.PRESENT;
    private Map<PsiFile, List<ProblemDescriptor>> results;
    private Throwable error;

    /**
     * Create a new file scanner.
     *
     * @param codeNarcPlugin       CodeNarcPlugin.
     * @param filesToScan            the files to scan.
     * @param moduleClassLoader      the class loader for the file's module
     * @param overrideConfigLocation if non-null this configuration will be used in preference to the normal configuration.
     */
    public FileScanner(final CodeNarcPlugin codeNarcPlugin,
                       final Set<PsiFile> filesToScan,
                       final ClassLoader moduleClassLoader,
                       final ConfigurationLocation overrideConfigLocation) {
        this.plugin = codeNarcPlugin;
        this.filesToScan = filesToScan;
        this.moduleClassLoader = moduleClassLoader;
        this.overrideConfigLocation = overrideConfigLocation;
    }

    public void run() {
        try {
            results = checkPsiFile(filesToScan, overrideConfigLocation);

            final CodeNarcToolWindowPanel toolWindowPanel = CodeNarcToolWindowPanel.panelFor(plugin.getProject());
            if (toolWindowPanel != null) {
                toolWindowPanel.incrementProgressBarBy(filesToScan.size());
            }
        } catch (Throwable e) {
            error = e;
        }
    }

    /**
     * Get the results of the scan.
     *
     * @return the results of the scan.
     */
    public Map<PsiFile, List<ProblemDescriptor>> getResults() {
        if (results != null) {
            return Collections.unmodifiableMap(results);
        }

        return Collections.emptyMap();
    }

    /**
     * Get any error that may have occurred during the scan.
     *
     * @return any error that may have occurred during the scan
     */
    public Throwable getError() {
        return error;
    }

    ConfigurationLocationStatus getConfigurationLocationStatus() {
        return configurationLocationStatus;
    }

    /**
     * Scan a PSI file with CodeNarc.
     *
     * @param psiFilesToScan the PSI psiFilesToScan to scan. These will be
     *                       ignored if not a java file and not from the same module.
     * @param override       if non-null this location will be used in preference to the configured rules file.
     * @return a list of tree nodes representing the result tree for this
     *         file, an empty list or null if this file is invalid or
     *         has no errors.
     * @throws Throwable if the
     */
    private Map<PsiFile, List<ProblemDescriptor>> checkPsiFile(final Set<PsiFile> psiFilesToScan,
                                                               final ConfigurationLocation override)
            throws Throwable {
        if (psiFilesToScan == null || psiFilesToScan.isEmpty()) {
            LOG.debug("No elements were specified");
            return null;
        }

        Module module = null;

        final List<ScannableFile> tempFiles = new ArrayList<ScannableFile>();
        final Map<String, PsiFile> filesToElements = new HashMap<String, PsiFile>();

        final boolean checkTestClasses = this.plugin.getConfiguration().isScanningTestClasses();
        final boolean scanOnlyJavaFiles = !plugin.getConfiguration().isScanningNonGroovyFiles();

        try {
            final AccessToken readAccessToken = ApplicationManager.getApplication().acquireReadActionLock();
            try {
                for (final PsiFile psiFile : psiFilesToScan) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Processing " + describe(psiFile));
                    }

                    if (psiFile == null || !psiFile.getVirtualFile().isValid() || !psiFile.isPhysical()) {
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("Skipping as invalid type: " + describe(psiFile));
                        }
                        continue;
                    }

                    if (module == null) {
                        module = ModuleUtil.findModuleForPsiElement(psiFile);
                    } else {
                        final Module elementModule = ModuleUtil.findModuleForPsiElement(psiFile);
                        if (!elementModule.equals(module)) {
                            if (LOG.isDebugEnabled()) {
                                LOG.debug("Skipping as modules do not match: " + describe(psiFile)
                                        + " : " + elementModule + " does not match " + module);
                            }
                            continue;
                        }
                    }

                    if (!checkTestClasses && isTestClass(psiFile)) {
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("Skipping test class " + psiFile.getName());
                        }
                        continue;
                    }

                    if (scanOnlyJavaFiles && !CodeNarcUtilities.isGroovyFile(psiFile.getFileType())) {
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("Skipping non-Java file " + psiFile.getName());
                        }
                        continue;
                    }

                    final ScannableFile tempFile = createTemporaryFile(psiFile);
                    if (tempFile != null) {
                        tempFiles.add(tempFile);
                        filesToElements.put(tempFile.getAbsolutePath(), psiFile);
                    }
                }
            } finally {
                readAccessToken.finish();
            }

            if (module == null || filesToElements.isEmpty()) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("No valid files were supplied");
                }
                return null;
            }

            return performCodeNarcScan(module, tempFiles, filesToElements, override);

        } finally {
            for (final ScannableFile tempFile : tempFiles) {
                if (tempFile != null) {
                    tempFile.deleteIfRequired();
                }
            }
        }
    }

    private String describe(final PsiFile psiFile) {
        if (psiFile != null) {
            return psiFile.getName();
        }
        return null;
    }

    @SuppressWarnings("SynchronizationOnLocalVariableOrMethodParameter")
    private Map<PsiFile, List<ProblemDescriptor>> performCodeNarcScan(final Module module,
                                                                        final List<ScannableFile> tempFiles,
                                                                        final Map<String, PsiFile> filesToElements,
                                                                        final ConfigurationLocation override) {
        final InspectionManager manager = InspectionManager.getInstance(module.getProject());

        final ConfigurationLocation location = getConfigurationLocation(module, override);
        if (location == null) {
            configurationLocationStatus = ConfigurationLocationStatus.NOT_PRESENT;
            return null;
        }

        if (location.isBlacklisted()) {
            configurationLocationStatus = ConfigurationLocationStatus.BLACKLISTED;
            return null;
        }

        final RuleSet checker = getChecker(module, moduleClassLoader, location);
        if (checker == null) {
            return Collections.emptyMap();
        }

        FilesSourceAnalyzer analyzer = new FilesSourceAnalyzer(asListOfFiles(tempFiles));
        Results results = analyzer.analyze(checker);

        final boolean suppressingErrors = plugin.getConfiguration().isSuppressingErrors();
        final ResultsProcessor listener = new ResultsProcessor(filesToElements, manager, true, suppressingErrors);

        for (Results fileResults : (List<Results>) (results).getChildren()) {
            listener.addError(fileResults);
        }
        listener.process();

        return listener.getAllProblems();
    }

    private List<File> asListOfFiles(final List<ScannableFile> tempFiles) {
        final List<File> listOfFiles = new ArrayList<File>();
        for (ScannableFile tempFile : tempFiles) {
            listOfFiles.add(tempFile.getFile());
        }
        return listOfFiles;
    }

    private ScannableFile createTemporaryFile(final PsiFile psiFile) {
        ScannableFile tempFile = null;
        try {
            // we need to copy to a file as IntelliJ may not have
            // saved the file recently...
            final CreateScannableFileAction fileAction
                    = new CreateScannableFileAction(psiFile);
            ApplicationManager.getApplication().runReadAction(fileAction);

            // rethrow any error from the thread.
            //noinspection ThrowableResultOfMethodCallIgnored
            if (fileAction.getFailure() != null) {
                throw fileAction.getFailure();
            }

            tempFile = fileAction.getFile();
            if (tempFile == null) {
                throw new IllegalStateException("Failed to create temporary file.");
            }

        } catch (IOException e) {
            LOG.error("Failure when creating temp file", e);
        }

        return tempFile;
    }

    private RuleSet getChecker(final Module module,
                               final ClassLoader classLoader,
                               final ConfigurationLocation location) {
        LOG.debug("Getting CodeNarc checker with location " + location);

        try {
            return getCheckerFactory().getChecker(location, module, classLoader);

        } catch (Exception e) {
            throw new CodeNarcPluginException("Couldn't create Checker", e);
        }
    }

    private ConfigurationLocation getConfigurationLocation(final Module module,
                                                           final ConfigurationLocation override) {
        if (override != null) {
            return override;
        }

        if (module != null) {
            final CodeNarcModuleConfiguration moduleConfiguration
                    = ModuleServiceManager.getService(module, CodeNarcModuleConfiguration.class);
            if (moduleConfiguration == null) {
                throw new IllegalStateException("Couldn't get CodeNarc module configuration");
            }

            if (moduleConfiguration.isExcluded()) {
                return null;
            }
            return moduleConfiguration.getActiveConfiguration();

        }
        return plugin.getConfiguration().getActiveConfiguration();
    }

    private CheckerFactory getCheckerFactory() {
        return ServiceManager.getService(CheckerFactory.class);
    }

    private boolean isTestClass(final PsiElement element) {
        final VirtualFile elementFile = element.getContainingFile().getVirtualFile();
        if (elementFile == null) {
            return false;
        }

        final Module module = ModuleUtil.findModuleForFile(elementFile, plugin.getProject());
        if (module == null) {
            return false;
        }

        final ModuleRootManager moduleRootManager = ModuleRootManager.getInstance(module);
        return moduleRootManager != null && moduleRootManager.getFileIndex().isInTestSourceContent(elementFile);
    }
}
