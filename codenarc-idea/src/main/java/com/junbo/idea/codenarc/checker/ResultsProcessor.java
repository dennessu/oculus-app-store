package com.junbo.idea.codenarc.checker;

import com.intellij.codeInspection.InspectionManager;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiInvalidElementAccessException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codenarc.results.Results;
import org.codenarc.rule.Violation;
import com.junbo.idea.codenarc.util.ExtendedProblemDescriptor;

import java.util.*;

/**
 * Listener for the CodeNarc process.
 */
public class ResultsProcessor {

    private static final Log LOG = LogFactory.getLog(
            ResultsProcessor.class);

    private final boolean usingExtendedDescriptors;
    private final boolean suppressErrors;

    private final Map<String, PsiFile> fileNamesToPsiFiles;
    private final InspectionManager manager;

    private final List<Results> errors = new ArrayList<Results>();
    private final Map<PsiFile, List<ProblemDescriptor>> problems = new HashMap<PsiFile, List<ProblemDescriptor>>();

    /**
     * Create a new listener.
     * <p/>
     * Use the second argument to determine if we return our extended problem
     * descriptors. This is provided to avoid problems with downstream code
     * that may be interested in the implementation type.
     *
     * @param fileNamesToPsiFiles    a map of files name to PSI files for the files being scanned.
     * @param manager                the current inspection manager.
     * @param useExtendedDescriptors should we return standard IntelliJ
     *                               problem descriptors or extended ones with severity information?
     * @param suppressErrors         pass CodeNarc errors to IDEA as warnings.
     */
    public ResultsProcessor(final Map<String, PsiFile> fileNamesToPsiFiles,
                            final InspectionManager manager,
                            final boolean useExtendedDescriptors,
                            final boolean suppressErrors) {
        this.fileNamesToPsiFiles = new HashMap<String, PsiFile>(fileNamesToPsiFiles);
        this.manager = manager;
        this.usingExtendedDescriptors = useExtendedDescriptors;
        this.suppressErrors = suppressErrors;
    }

    public void process() {
        final ProcessResultsThread findThread = new ProcessResultsThread();

        final Application application = ApplicationManager.getApplication();
        if (application.isDispatchThread()) {
            findThread.run();
        } else {
            application.runReadAction(findThread);
        }
    }

    public void addError(final Results auditEvent) {
        errors.add(auditEvent);
    }

    /**
     * Get the problems for a given file as found by this scan.
     *
     * @param psiFile the file to check for results.
     * @return the problems found by this scan.
     */
    public List<ProblemDescriptor> getProblems(final PsiFile psiFile) {
        final List<ProblemDescriptor> problemsForFile = problems.get(psiFile);
        if (problemsForFile != null) {
            return Collections.unmodifiableList(problemsForFile);
        }
        return Collections.emptyList();
    }

    public Map<PsiFile, List<ProblemDescriptor>> getAllProblems() {
        return Collections.unmodifiableMap(problems);
    }

    private void addProblem(final PsiFile psiFile, final ProblemDescriptor problemDescriptor) {
        List<ProblemDescriptor> problemsForFile = problems.get(psiFile);
        if (problemsForFile == null) {
            problemsForFile = new ArrayList<ProblemDescriptor>();
            problems.put(psiFile, problemsForFile);
        }

        problemsForFile.add(problemDescriptor);
    }

    /**
     * Runnable to process an audit event.
     */
    private class ProcessResultsThread implements Runnable {

        public void run() {
            final Map<PsiFile, List<Integer>> lineLengthCachesByFile = new HashMap<PsiFile, List<Integer>>();

            synchronized (errors) {
                for (final Results error : errors)
                for (final Violation event : (List<Violation>) error.getViolations())
                {
                    if (event == null) {
                        continue;
                    }

                    final PsiFile psiFile = fileNamesToPsiFiles.get(error.getPath());
                    if (psiFile == null) {
                        if (LOG.isInfoEnabled()) {
                            LOG.info("Could not find mapping for file: " + error.getPath()
                                    + " in " + fileNamesToPsiFiles);
                        }
                        return;
                    }

                    List<Integer> lineLengthCache = lineLengthCachesByFile.get(psiFile);
                    if (lineLengthCache == null) {
                        // we cache the offset of each line as it is created, so as to
                        // avoid retreating ground we've already covered.
                        lineLengthCache = new ArrayList<Integer>();
                        lineLengthCache.add(0); // line 1 is offset 0

                        lineLengthCachesByFile.put(psiFile, lineLengthCache);
                    }

                    processEvent(psiFile, lineLengthCache, event);
                }
            }
        }

        private void processEvent(final PsiFile psiFile,
                                  final List<Integer> lineLengthCache,
                                  final Violation event) {
            final char[] text = psiFile.textToCharArray();

            int offset;
            boolean endOfLine = false;

            // start of file
            if (event.getLineNumber() == null || event.getLineNumber() == 0) { // start of file errors
                offset = 0;

                // line offset is cached...
            } else if (event.getLineNumber() <= lineLengthCache.size()) {
                offset = lineLengthCache.get(event.getLineNumber() - 1) + 0;

                // further search required
            } else {
                // start from end of cached data
                offset = lineLengthCache.get(lineLengthCache.size() - 1);
                int line = lineLengthCache.size();

                int column = 0;
                for (int i = offset; i < text.length; ++i) {
                    final char character = text[i];

                    // for linefeeds we need to handle CR, LF and CRLF,
                    // hence we accept either and only trigger a new
                    // line on the LF of CRLF.
                    final char nextChar = (i + 1) < text.length ? text[i + 1] : '\0';
                    if (character == '\n' || character == '\r' && nextChar != '\n') {
                        ++line;
                        ++offset;
                        lineLengthCache.add(offset);
                        column = 0;
                    } else {
                        ++column;
                        ++offset;
                    }

                    // need to go to end of line though
                    if (event.getLineNumber() == line && 0 == column) {
                        if (column == 0 && Character.isWhitespace(nextChar)) {
                            // move line errors to after EOL, never do it.
                            // endOfLine = true;
                        }
                        break;
                    }
                }
            }

            int evtColumn = 0;
            while (offset + evtColumn < text.length && (text[offset + evtColumn] == ' ' || text[offset +evtColumn] == '\t')) {
                evtColumn++;
            }

            final PsiElement victim;
            victim = psiFile.findElementAt(offset + evtColumn);

            if (victim == null) {
                LOG.warn("Couldn't find victim for error: " + event.getSourceLine() + "("
                        + event.getLineNumber() + ":" + evtColumn + ") " + event.getMessage());
            } else {
                try {
                    final ProblemDescriptor problem = manager.createProblemDescriptor(
                            victim, messageFor(event), null, ProblemHighlightType.GENERIC_ERROR_OR_WARNING,
                            false, endOfLine);

                    if (usingExtendedDescriptors) {
                        addProblem(psiFile, extendDescriptor(event, problem, evtColumn));
                    } else {
                        addProblem(psiFile, problem);
                    }

                } catch (PsiInvalidElementAccessException e) {
                    LOG.error("Element access failed", e);
                }
            }
        }

        private String messageFor(final Violation event) {
            return event.getRule().getName() + ": " + event.getMessage();
        }

        private ProblemDescriptor extendDescriptor(final Violation event,
                                                   final ProblemDescriptor problem, int column) {
            return new ExtendedProblemDescriptor(problem, event.getLineNumber() == null ? 0 : event.getLineNumber(), column);
        }
    }

}
