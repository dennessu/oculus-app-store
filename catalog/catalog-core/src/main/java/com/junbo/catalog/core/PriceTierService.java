/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.core;

import com.junbo.catalog.spec.model.pricetier.PriceTier;
import com.junbo.catalog.spec.model.pricetier.PriceTiersGetOptions;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Price tier service definition.
 */
@Transactional
public interface PriceTierService {
    PriceTier getPriceTier(Long tierId);
    List<PriceTier> getPriceTiers(PriceTiersGetOptions options);
    PriceTier create(PriceTier priceTier);
    PriceTier update(Long tierId, PriceTier priceTier);
    void delete(Long tierId);
}
