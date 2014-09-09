/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.common.model;

import java.util.Date;

/**
 * The base interface for all SQL entity classes.
 */
public interface EntityAdminInfo {
    Date getCreatedTime();
    void setCreatedTime(Date createdTime);

    Long getCreatedBy();
    void setCreatedBy(Long createdBy);

    String getCreatedByClient();
    void setCreatedByClient(String createdByClient);

    Date getUpdatedTime();
    void setUpdatedTime(Date updatedTime);

    Long getUpdatedBy();
    void setUpdatedBy(Long updatedBy);

    String getUpdatedByClient();
    void setUpdatedByClient(String updatedByClient);
}
