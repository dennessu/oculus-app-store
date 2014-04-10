package com.junbo.payment.core.mock;

import com.junbo.payment.core.provider.PaymentProviderService;
import com.junbo.payment.core.provider.ProviderRoutingService;
import com.junbo.payment.spec.enums.PIType;
import org.springframework.beans.factory.annotation.Autowired;


public class MockProviderRoutingServiceImpl implements ProviderRoutingService {
    @Autowired
    private MockPaymentProviderServiceImpl mockPaymentProviderService;
    @Autowired
    private MockWalletServiceImpl mockWalletService;

    @Override
    public PaymentProviderService getPaymentProvider(PIType piType) {
        if(piType.equals(PIType.CREDITCARD)){
            return mockPaymentProviderService;
        } else if(piType.equals(PIType.WALLET)){
            return mockWalletService;
        }
        return null;
    }

    @Override
    public void updatePaymentConfiguration() {

    }
}