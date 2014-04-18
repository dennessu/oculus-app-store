package com.junbo.identity.data.repository.impl

import groovy.transform.CompileStatic

/**
 * Created by haomin on 14-4-18.
 */
@CompileStatic
public enum PersistentMode {
    CLOUDANT_READ_WRITE,
    CLOUDANT_READ_DUAL_WRITE_CLOUDANT_PRIMARY,
    CLOUDANT_READ_DUAL_WRITE_SQL_PRIMARY
}
