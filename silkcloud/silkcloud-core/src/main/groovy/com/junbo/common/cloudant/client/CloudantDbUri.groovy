package com.junbo.common.cloudant.client

import com.junbo.common.util.Utils
import groovy.transform.CompileStatic

/**
 * CloudantUri.
 */
@CompileStatic
class CloudantDbUri {
    CloudantUri cloudantUri
    String dbName
    String fullDbName

    boolean equals(o) {
        if (this.is(o)) return true
        if (getClass() != o.class) return false

        CloudantDbUri that = (CloudantDbUri) o

        if (cloudantUri != that.cloudantUri) return false
        if (dbName != that.dbName) return false
        if (fullDbName != that.fullDbName) return false

        return true
    }

    int hashCode() {
        int result
        result = (cloudantUri != null ? cloudantUri.hashCode() : 0)
        result = 31 * result + dbName.hashCode()
        result = 31 * result + fullDbName.hashCode()
        return result
    }

    @Override
    public String toString() {
        return Utils.combineUrl(cloudantUri.value, fullDbName);
    }
}
