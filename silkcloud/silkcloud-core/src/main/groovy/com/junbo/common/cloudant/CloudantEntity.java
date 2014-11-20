/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.common.cloudant;

import com.junbo.common.util.Identifiable;

import java.util.Date;

/**
 * The interface with common fields used by CloudantClient.
 */
public interface CloudantEntity<K> extends Identifiable<K> {

    K getId();
    void setId(K id);

    Boolean isDeleted();
    void setDeleted(Boolean deleted);

    String getCloudantId();
    void setCloudantId(String id);

    String getCloudantRev();
    void setCloudantRev(String rev);

    Date getCreatedTime();
    void setCreatedTime(Date date);

    Date getUpdatedTime();
    void setUpdatedTime(Date date);

    Long getCreatedBy();
    void setCreatedBy(Long createdBy);

    Long getUpdatedBy();
    void setUpdatedBy(Long updatedBy);

    // only used in dual-write resources
    Integer getResourceAge();
    void setResourceAge(Integer resourceAge);

    String getCreatedByClient();
    void setCreatedByClient(String client);

    String getUpdatedByClient();
    void setUpdatedByClient(String client);
}
