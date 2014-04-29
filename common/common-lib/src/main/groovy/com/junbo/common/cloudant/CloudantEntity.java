/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.common.cloudant;

import com.junbo.common.id.UserId;

import java.util.Date;

/**
 * The interface with common fields used by CloudantClient.
 */
public interface CloudantEntity {
    String getCloudantId();
    void setCloudantId(String id);

    String getCloudantRev();
    void setCloudantRev(String rev);

    String getResourceAge();
    void setResourceAge(String resourceAge);

    Date getCreatedTime();
    void setCreatedTime(Date date);

    Date getUpdatedTime();
    void setUpdatedTime(Date date);

    UserId getCreatedBy();
    void setCreatedBy(UserId createdBy);

    UserId getUpdatedBy();
    void setUpdatedBy(UserId updatedBy);

    // TODO: track client
    // String getCreatedByClient()
    // void setCreatedByClient(String client)

    // String getUpdatedByClient()
    // void setUpdatedByClient(String client)
}
