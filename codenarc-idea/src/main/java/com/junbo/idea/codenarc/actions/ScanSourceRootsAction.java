package com.junbo.idea.codenarc.actions;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.junbo.idea.codenarc.CodeNarcPlugin;
import com.junbo.idea.codenarc.model.ConfigurationLocation;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

class ScanSourceRootsAction implements Runnable {
    private final Project project;
    private final VirtualFile[] sourceRoots;
    private final ConfigurationLocation selectedOverride;

    public ScanSourceRootsAction(@NotNull final Project project,
                                 @NotNull final VirtualFile[] sourceRoots,
                                 final ConfigurationLocation selectedOverride) {
        this.project = project;
        this.sourceRoots = sourceRoots;
        this.selectedOverride = selectedOverride;
    }

    public void run() {
        project.getComponent(CodeNarcPlugin.class).checkFiles(flattenFiles(sourceRoots), selectedOverride);
    }

    /**
     * Flatten a nested list of files, returning all files in the array.
     *
     * @param files the top level of files.
     * @return the flattened list of files.
     */
    private List<VirtualFile> flattenFiles(final VirtualFile[] files) {
        final List<VirtualFile> flattened = new ArrayList<VirtualFile>();

        if (files != null) {
            for (final VirtualFile file : files) {
                flattened.add(file);

                if (file.getChildren() != null) {
                    flattened.addAll(flattenFiles(file.getChildren()));
                }
            }
        }

        return flattened;
    }
}
