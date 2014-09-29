/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.common.enumid;

import com.junbo.common.id.IdResourcePath;

/**
 * Created by minhao on 5/8/14.
 */
@IdResourcePath("/age-rating-boards/{0}")
public class RatingBoardId extends EnumId {
    public RatingBoardId() {
    }

    public RatingBoardId(String value) {
        super(value);
    }
}
