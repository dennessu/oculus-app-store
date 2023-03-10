package com.junbo.common.cloudant.client

import groovy.transform.CompileStatic

/**
 * CloudantUri.
 */
@CompileStatic
class CloudantUri {
    String dc
    String value
    String username
    String password
    String account

    boolean equals(o) {
        if (this.is(o)) return true
        if (getClass() != o.class) return false

        CloudantUri that = (CloudantUri) o

        if (dc != that.dc) return false
        if (password != that.password) return false
        if (username != that.username) return false
        if (value != that.value) return false
        if (account != that.account) return false

        return true
    }

    int hashCode() {
        int result
        result = (dc != null ? dc.hashCode() : 0)
        result = 31 * result + value.hashCode()
        result = 31 * result + (username != null ? username.hashCode() : 0)
        result = 31 * result + (password != null ? password.hashCode() : 0)
        result = 31 * result + (account != null ? account.hashCode() : 0)
        return result
    }


    @Override
    public String toString() {
        return value;
    }

    public String getDetail() {
        return String.format("URL [%s] | DC [%s] | Username [%s] | Account [%s]", value, dc, username, account);
    }

    public String getKey() {
        return String.format("%s#%s#%s", value, username, account);
    }
}
