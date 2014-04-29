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

    String getResourceAge();
    void setResourceAge(String resourceAge);

    Date getCreatedTime();
    void setCreatedTime(Date date);

    Date getUpdatedTime();
    void setUpdatedTime(Date date);

    String getCreatedBy();
    void setCreatedBy(String createdBy);

    String getUpdatedBy();
    void setUpdatedBy(String updatedBy);

    // TODO: track client
    // String getCreatedByClient()
    // void setCreatedByClient(String client)

    // String getUpdatedByClient()
    // void setUpdatedByClient(String client)
}
