package com.junbo.common.cloudant

/**
 * Created by haomin on 14-4-23.
 */
interface CloudantEntity {
    String get_id()
    void set_id(String id)
    String get_rev()
    void set_rev(String rev)
    Date getCreatedTime()
    void setCreatedTime(Date date)
    String getCreatedBy()
    void setCreatedBy(String createdBy)
    String getResourceAge()
    void setResourceAge(String resourceAge)
}