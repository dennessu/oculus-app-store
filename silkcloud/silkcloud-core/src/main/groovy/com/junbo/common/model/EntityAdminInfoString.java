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
public interface EntityAdminInfoString {
    Date getCreatedTime();
    void setCreatedTime(Date createdTime);

    String getCreatedBy();
    void setCreatedBy(String createdBy);

    String getCreatedByClient();
    void setCreatedByClient(String createdByClient);

    Date getUpdatedTime();
    void setUpdatedTime(Date updatedTime);

    String getUpdatedBy();
    void setUpdatedBy(String updatedBy);

    String getUpdatedByClient();
    void setUpdatedByClient(String updatedByClient);
}
