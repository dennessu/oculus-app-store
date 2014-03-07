/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.core;

import com.junbo.catalog.spec.model.promotion.Promotion;
import org.springframework.transaction.annotation.Transactional;

/**
 * Promotion service definition.
 */
@Transactional
public interface PromotionService extends BaseService<Promotion> {
}
