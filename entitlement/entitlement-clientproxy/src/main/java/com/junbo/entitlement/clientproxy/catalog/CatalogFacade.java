/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.entitlement.clientproxy.catalog;

/**
 * Interface wrapper to call from catalog.
 */
public interface CatalogFacade {
    boolean exists(Long offerId);
}
