package com.junbo.idea.codenarc.analyzer;

import org.codenarc.analyzer.AbstractSourceAnalyzer;
import org.codenarc.results.DirectoryResults;
import org.codenarc.results.FileResults;
import org.codenarc.results.Results;
import org.codenarc.rule.Rule;
import org.codenarc.ruleset.RuleSet;
import org.codenarc.source.SourceFile;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by kg on 1/27/14.
 */
public class FilesSourceAnalyzer extends AbstractSourceAnalyzer {

    private final List<File> files;

    public FilesSourceAnalyzer(final List<File> files) {
        this.files = files;
    }

    @Override
    public Results analyze(final RuleSet ruleSet) {

        DirectoryResults directoryResults = new DirectoryResults();


        for (File file : files) {
            SourceFile sourceFile = new SourceFile(file);

            directoryResults.addChild(
                    new FileResults(file.getAbsolutePath(), collectViolations(sourceFile, ruleSet))
            );
        }

        return directoryResults;
    }

    @Override
    public List getSourceDirectories() {
        return new ArrayList();
    }
}
