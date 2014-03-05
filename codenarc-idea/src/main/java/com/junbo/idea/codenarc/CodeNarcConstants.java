package com.junbo.idea.codenarc;

/**
 * Cross-application constants for the CodeNarc plug-in.
 */
public final class CodeNarcConstants {

    /**
     * The name of the application resource bundle.
     */
    public static final String RESOURCE_BUNDLE
            = "com.junbo.idea.codenarc.resource";

    /**
     * Plug-in identifier.
     */
    public static final String ID_PLUGIN = "CodeNarc-IDEA";

    /**
     * Inspection identifier.
     */
    public static final String ID_INSPECTION = "CodeNarcIDEAInspection";

    /**
     * Plug-in module identifier.
     */
    public static final String ID_MODULE_PLUGIN = "CodeNarc-IDEA-Module";

    /**
     * Tool Window identifier.
     */
    public static final String ID_TOOLWINDOW = "CodeNarc";

    /**
     * Constant used to represent project directory.
     */
    public static final String PROJECT_DIR = "$PRJ_DIR$";

    /**
     * Constant used to represent legacy project directory.
     */
    public static final String LEGACY_PROJECT_DIR = "$PROJECT_DIR$";

    /**
     * This is a constants class and cannot be instantiated.
     */
    private CodeNarcConstants() {
        // constants class
    }

}
