/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.payment.db.repo.cloudant;

import com.junbo.payment.db.repo.PaymentTransactionRepository;
import com.junbo.payment.spec.model.PaymentTransaction;
import com.junbo.sharding.repo.BaseCloudantRepositoryForDualWrite;

/**
 * Created by haomin on 14-6-19.
 */
public class PaymentTransactionRepositoryCloudantImpl extends BaseCloudantRepositoryForDualWrite<PaymentTransaction, Long> implements PaymentTransactionRepository{
}
