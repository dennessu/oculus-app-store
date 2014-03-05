package com.junbo.payment.core.mock;

import com.junbo.payment.core.provider.PaymentProviderService;
import com.junbo.payment.core.provider.ProviderRoutingService;
import com.junbo.payment.spec.enums.PIType;
import org.springframework.beans.factory.annotation.Autowired;


public class MockProviderRoutingServiceImpl implements ProviderRoutingService {
    @Autowired
    private MockPaymentProviderServiceImpl mockPaymentProviderService;

    @Override
    public PaymentProviderService getPaymentProvider(PIType piType) {
        //TODO: hard code braintree first
        return mockPaymentProviderService;
    }

    @Override
    public void updatePaymentConfiguration() {

    }
}