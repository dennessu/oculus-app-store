package com.junbo.idea.codenarc.util;

import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.StdFileTypes;

/**
 * General utilities to make life easier with regards to CodeNarc.
 */
public final class CodeNarcUtilities {

    /**
     * This is a utility class and cannot be instantiated.
     */
    private CodeNarcUtilities() {

    }

    /**
     * Is this file type supported by CodeNarc?
     *
     * @param fileType the file type to test.
     * @return true if this file is supported by CodeNarc.
     */
    public static boolean isGroovyFile(final FileType fileType) {
        return fileType != null && fileType.getName().equals("Groovy");
    }
}
