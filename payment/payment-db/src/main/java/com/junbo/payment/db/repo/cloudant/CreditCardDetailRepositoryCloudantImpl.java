/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.payment.db.repo.cloudant;

import com.junbo.payment.db.repo.CreditCardDetailRepository;
import com.junbo.payment.spec.model.CreditCardDetail;
import com.junbo.sharding.repo.BaseCloudantRepositoryForDualWrite;

/**
 * Created by haomin on 14-6-19.
 */
public class CreditCardDetailRepositoryCloudantImpl extends BaseCloudantRepositoryForDualWrite<CreditCardDetail, Long> implements CreditCardDetailRepository{
}
