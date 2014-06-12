/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.rating.spec.fusion;

import java.util.Date;

/**
 * Created by lizwu on 6/11/14.
 */
public class Properties {
    private boolean isPurchasable;
    private Date releaseDate;

    public Properties(boolean isPurchasable, Date releaseDate) {
        this.isPurchasable = isPurchasable;
        this.releaseDate = releaseDate;
    }

    public boolean isPurchasable() {
        return isPurchasable;
    }

    public void setPurchasable(boolean isPurchasable) {
        this.isPurchasable = isPurchasable;
    }

    public Date getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(Date releaseDate) {
        this.releaseDate = releaseDate;
    }
}
