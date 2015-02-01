package com.junbo.payment.db.repo.cloudant;

import com.junbo.common.cloudant.CloudantClient;
import com.junbo.langur.core.promise.Promise;
import com.junbo.payment.db.repo.PaymentProviderIdMappingRepository;
import com.junbo.payment.spec.model.PaymentProviderIdMapping;

import java.util.List;

/**
 * Created by wenzhumac on 1/31/15.
 */
public class PaymentProviderIdMappingRepoImpl extends CloudantClient<PaymentProviderIdMapping> implements PaymentProviderIdMappingRepository{
    @Override
    public Promise<PaymentProviderIdMapping> get(String id) {
        return cloudantGet(id);
    }

    @Override
    public Promise<PaymentProviderIdMapping> create(PaymentProviderIdMapping model) {
        return cloudantPost(model);
    }

    @Override
    public Promise<PaymentProviderIdMapping> update(PaymentProviderIdMapping model, PaymentProviderIdMapping oldModel) {
        return cloudantPut(model, oldModel);
    }

    @Override
    public Promise<Void> delete(String id) {
        return cloudantDelete(id);
    }
}
