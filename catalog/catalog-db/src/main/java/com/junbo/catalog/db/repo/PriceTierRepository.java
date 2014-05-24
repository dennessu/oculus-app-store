/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.db.repo;

import com.junbo.catalog.spec.model.pricetier.PriceTier;

import java.util.List;

/**
 * Price tier repository.
 */
public interface PriceTierRepository {
    PriceTier create(PriceTier priceTier);
    PriceTier get(Long id);
    List<PriceTier> getPriceTiers(int start, int size);
    PriceTier update(PriceTier priceTier);
    void delete(Long tierId);
}
