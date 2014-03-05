package com.junbo.idea.codenarc;

import com.intellij.codeInspection.InspectionManager;
import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleServiceManager;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.progress.ProcessCanceledException;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.junbo.idea.codenarc.analyzer.FilesSourceAnalyzer;
import com.junbo.idea.codenarc.checker.ResultsProcessor;
import com.junbo.idea.codenarc.exception.CodeNarcPluginException;
import com.junbo.idea.codenarc.ui.CodeNarcInspectionPanel;
import com.junbo.idea.codenarc.util.CodeNarcUtilities;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codenarc.results.Results;
import org.codenarc.ruleset.RuleSet;
import com.junbo.idea.codenarc.checker.CheckerFactory;
import com.junbo.idea.codenarc.model.ConfigurationLocation;
import com.junbo.idea.codenarc.util.IDEAUtilities;
import com.junbo.idea.codenarc.util.ScannableFile;
import org.intellij.lang.annotations.Pattern;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Inspection for CodeNarc integration for IntelliJ IDEA.
 */
public class CodeNarcInspection extends LocalInspectionTool {

    private static final Log LOG = LogFactory.getLog(CodeNarcInspection.class);

    private final CodeNarcInspectionPanel configPanel = new CodeNarcInspectionPanel();

    /**
     * Produce a CodeNarc checker.
     *
     * @param codeNarcPlugin the plugin.
     * @param module           the current module. May be null.
     * @return a checker.
     */
    private RuleSet getChecker(final CodeNarcPlugin codeNarcPlugin,
                               @Nullable final Module module) {
        LOG.debug("Getting CodeNarc checker for inspection.");

        if (module == null) {
            return null;
        }

        ConfigurationLocation configurationLocation = null;
        try {
            configurationLocation = getConfigurationLocation(module, codeNarcPlugin);
            if (configurationLocation == null) {
                return null;
            }

            if (configurationLocation.isBlacklisted()) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Configuration is blacklisted, skipping: " + configurationLocation);
                }
                return null;
            }

            LOG.info("Loading configuration from " + configurationLocation);
            return getCheckerFactory().getChecker(configurationLocation, module);

        } catch (Exception e) {
            LOG.error("Checker could not be created.", e);

            if (configurationLocation != null) {
                configurationLocation.blacklist();
            }

            throw new CodeNarcPluginException("Couldn't create Checker", e);
        }
    }

    private CodeNarcPlugin getPlugin(final Project project) {
        final CodeNarcPlugin codeNarcPlugin = project.getComponent(CodeNarcPlugin.class);
        if (codeNarcPlugin == null) {
            throw new IllegalStateException("Couldn't get CodeNarc plugin");
        }
        return codeNarcPlugin;
    }

    private CheckerFactory getCheckerFactory() {
        return ServiceManager.getService(CheckerFactory.class);
    }

    private ConfigurationLocation getConfigurationLocation(final Module module,
                                                           final CodeNarcPlugin codeNarcPlugin) {
        final ConfigurationLocation configurationLocation;
        if (module != null) {
            final CodeNarcModuleConfiguration moduleConfiguration
                    = ModuleServiceManager.getService(module, CodeNarcModuleConfiguration.class);
            if (moduleConfiguration == null) {
                throw new IllegalStateException("Couldn't get CodeNarc module configuration");
            }

            if (moduleConfiguration.isExcluded()) {
                configurationLocation = null;
            } else {
                configurationLocation = moduleConfiguration.getActiveConfiguration();
            }

        } else {
            configurationLocation = codeNarcPlugin.getConfiguration().getActiveConfiguration();
        }
        return configurationLocation;
    }

    @Nullable
    public JComponent createOptionsPanel() {
        return configPanel;
    }

    @NotNull
    public String getGroupDisplayName() {
        return IDEAUtilities.getResource("plugin.group", "CodeNarc");
    }

    @NotNull
    public String getDisplayName() {
        return IDEAUtilities.getResource("plugin.display-name",
                "Real-time scan");
    }

    @Pattern("[a-zA-Z_0-9.]+")
    @NotNull
    @Override
    public String getID() {
        return CodeNarcConstants.ID_INSPECTION;
    }

    @NotNull
    @NonNls
    public String getShortName() {
        return CodeNarcConstants.ID_PLUGIN;
    }

    @Nullable
    public ProblemDescriptor[] checkFile(@NotNull final PsiFile psiFile,
                                         @NotNull final InspectionManager manager,
                                         final boolean isOnTheFly) {
        LOG.debug("Inspection has been invoked.");

        try {
            if (!psiFile.isValid() || !psiFile.isPhysical()) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Skipping file as invalid: " + psiFile.getName());
                }
                return null;
            }

            final CodeNarcPlugin codeNarcPlugin = getPlugin(manager.getProject());

            if (!codeNarcPlugin.getConfiguration().isScanningNonGroovyFiles()
                    && !CodeNarcUtilities.isGroovyFile(psiFile.getFileType())) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Skipping as file is not a Groovy file: " + psiFile.getName());
                }
                return null;
            }

            final Module module = ModuleUtil.findModuleForPsiElement(psiFile);

            final boolean checkTestClasses = codeNarcPlugin.getConfiguration().isScanningTestClasses();
            if (!checkTestClasses && module != null) {
                final VirtualFile elementFile = psiFile.getContainingFile().getVirtualFile();
                if (elementFile != null) {
                    final ModuleRootManager moduleRootManager = ModuleRootManager.getInstance(module);
                    if (moduleRootManager != null && moduleRootManager.getFileIndex().isInTestSourceContent(elementFile)) {
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("Skipping test class " + psiFile.getName());
                        }
                        return null;
                    }
                }
            }

            return scanFile(psiFile, manager, codeNarcPlugin, module);

        } catch (ProcessCanceledException e) {
            LOG.warn("Process cancelled when scanning: " + psiFile.getName());
            return null;

        } catch (AssertionError e) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Assertion error caught, exiting quietly", e);
            }
            return null;

        } catch (Throwable e) {
            final CodeNarcPluginException processed = CodeNarcPlugin.processError(
                    "The inspection could not be executed.", e);
            LOG.error("The inspection could not be executed.", processed);

            return null;
        }
    }

    private ProblemDescriptor[] scanFile(final PsiFile psiFile,
                                         final InspectionManager manager,
                                         final CodeNarcPlugin codeNarcPlugin,
                                         final Module module)
            throws IOException {
        ScannableFile scannableFile = null;
        try {
            final RuleSet checker = getChecker(codeNarcPlugin, module);
            if (checker == null) {
                return new ProblemDescriptor[0];
            }

            final Document fileDocument = PsiDocumentManager.getInstance(
                    manager.getProject()).getDocument(psiFile);
            if (fileDocument == null) {
                LOG.debug("Skipping check - file is binary or has no document: " + psiFile.getName());
                return null;
            }

            scannableFile = new ScannableFile(psiFile);

            final Map<String, PsiFile> filesToScan = Collections.singletonMap(scannableFile.getAbsolutePath(), psiFile);

            final boolean suppressingErrors = codeNarcPlugin.getConfiguration().isSuppressingErrors();


            FilesSourceAnalyzer analyzer = new FilesSourceAnalyzer(Arrays.asList(scannableFile.getFile()));
            Results results = analyzer.analyze(checker);

            final ResultsProcessor listener = new ResultsProcessor(filesToScan, manager, false, suppressingErrors);
            for (Results fileResults : (List<Results>) (results).getChildren()) {
                listener.addError(fileResults);
            }
            listener.process();

            final List<ProblemDescriptor> problems = listener.getProblems(psiFile);
            return problems.toArray(new ProblemDescriptor[problems.size()]);

        } finally {
            if (scannableFile != null) {
                scannableFile.deleteIfRequired();
            }
        }
    }

}
