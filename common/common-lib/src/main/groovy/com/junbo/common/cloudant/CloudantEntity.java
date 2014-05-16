/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.common.cloudant;

import java.util.Date;

/**
 * The interface with common fields used by CloudantClient.
 */
public interface CloudantEntity {

    String getCloudantId();
    void setCloudantId(String id);

    String getCloudantRev();
    void setCloudantRev(String rev);

    Integer getResourceAge();
    void setResourceAge(Integer resourceAge);

    Date getCreatedTime();
    void setCreatedTime(Date date);

    Date getUpdatedTime();
    void setUpdatedTime(Date date);

    Long getCreatedBy();
    void setCreatedBy(Long createdBy);

    Long getUpdatedBy();
    void setUpdatedBy(Long updatedBy);

    // TODO: track client
    // String getCreatedByClient()
    // void setCreatedByClient(String client)

    // String getUpdatedByClient()
    // void setUpdatedByClient(String client)
}
