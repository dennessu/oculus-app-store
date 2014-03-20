/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.order.spec.error;

/**
 * Created by chriszhu on 3/20/14.
 */
public class CatalogErrorCode {
    private CatalogErrorCode() {}

    // catalog error code starts from 35000

    public static final String OFFER_NOT_FOUND = "35000";

    public static final String CATALOG_CONNECTION_ERROR = "35001";
}
