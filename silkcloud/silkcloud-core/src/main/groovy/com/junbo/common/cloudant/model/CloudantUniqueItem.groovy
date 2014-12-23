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

    @CloudantProperty("_deleted")
    Boolean deleted

    @Override
    String getId() {
        return id
    }

    @Override
    void setId(String id) {
        this.id = id
    }

    @Override
    Boolean isDeleted() {
        return deleted
    }

    @Override
    void setDeleted(Boolean deleted) {
        this.deleted = deleted
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

    @Override
    String getCreatedByClient() {
        return null
    }

    @Override
    void setCreatedByClient(String client) {

    }

    @Override
    String getUpdatedByClient() {
        return null
    }

    @Override
    void setUpdatedByClient(String client) {

    }
}
