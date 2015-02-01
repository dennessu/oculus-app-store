package com.junbo.payment.spec.model;

import com.junbo.common.cloudant.CloudantEntity;
import com.junbo.common.model.ResourceMeta;

/**
 * Created by wenzhumac on 1/31/15.
 */
public class PaymentProviderIdMapping extends ResourceMeta<String> {
    private Long paymentId;
    private String externalId;

    public Long getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(Long paymentId) {
        this.paymentId = paymentId;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    @Override
    public String getId() {
        return externalId;
    }

    @Override
    public void setId(String id) {
        this.externalId = id;
    }
}
