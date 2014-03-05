package com.junbo.idea.codenarc.model;

import com.intellij.openapi.project.Project;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.junbo.idea.codenarc.util.FileUtils;
import org.jetbrains.annotations.NotNull;

import java.io.File;

/**
 * A configuration file on a mounted file system which will always be referred to
 * by a path relative to the project path.
 */
public class RelativeFileConfigurationLocation extends FileConfigurationLocation {

    private static final Log LOG = LogFactory.getLog(RelativeFileConfigurationLocation.class);

    RelativeFileConfigurationLocation(final Project project) {
        super(project, ConfigurationType.PROJECT_RELATIVE);
    }

    @Override
    public void setLocation(final String location) {
        if (location == null || location.trim().length() == 0) {
            throw new IllegalArgumentException("A non-blank location is required");
        }

        super.setLocation(tokenisePath(makeProjectRelative(detokenisePath(location))));
    }

    private String makeProjectRelative(@NotNull final String path) {
        final File projectPath = getProjectPath();
        if (projectPath == null) {
            LOG.debug("Couldn't find project path, returning full path: " + path);
            return path;
        }

        try {
            final String basePath = projectPath.getAbsolutePath() + File.separator;
            return basePath + FileUtils.getRelativePath(path, basePath, File.separator);

        } catch (FileUtils.PathResolutionException e) {
            LOG.debug("No common path was found between " + path + " and " + projectPath.getAbsolutePath());
            return path;

        } catch (Exception e) {
            throw new RuntimeException("Failed to make relative: " + path, e);
        }
    }

    @Override
    public Object clone() {
        return cloneCommonPropertiesTo(new RelativeFileConfigurationLocation(getProject()));
    }
}
