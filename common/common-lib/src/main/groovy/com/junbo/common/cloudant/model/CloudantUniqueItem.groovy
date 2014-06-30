package com.junbo.common.cloudant.model

import com.junbo.common.cloudant.CloudantEntity
import com.junbo.common.cloudant.json.annotations.CloudantProperty
import groovy.transform.CompileStatic

/**
 * CloudantResponse.
 */
@CompileStatic
class CloudantUniqueItem implements CloudantEntity<String> {
    @CloudantProperty("_id")
    String id

    String doc

    @CloudantProperty("_rev")
    String rev

    @Override
    String getId() {
        return id
    }

    @Override
    void setId(String id) {
        this.id = id
    }

    @Override
    String getCloudantId() {
        return id
    }

    @Override
    void setCloudantId(String id) {
        this.id = id
    }

    @Override
    String getCloudantRev() {
        return rev
    }

    @Override
    void setCloudantRev(String rev) {
        this.rev = rev
    }

    @Override
    Date getCreatedTime() {
        return null
    }

    @Override
    void setCreatedTime(Date date) {

    }

    @Override
    Date getUpdatedTime() {
        return null
    }

    @Override
    void setUpdatedTime(Date date) {

    }

    @Override
    Long getCreatedBy() {
        return null
    }

    @Override
    void setCreatedBy(Long createdBy) {

    }

    @Override
    Long getUpdatedBy() {
        return null
    }

    @Override
    void setUpdatedBy(Long updatedBy) {

    }

    @Override
    Integer getResourceAge() {
        return null
    }

    @Override
    void setResourceAge(Integer resourceAge) {

    }
}
