package com.junbo.payment.core.mock;

import com.junbo.common.id.PIType;
import com.junbo.payment.core.provider.PaymentProviderService;
import com.junbo.payment.core.provider.ProviderRoutingService;
import com.junbo.payment.core.provider.adyen.AdyenProviderServiceImpl;
import com.junbo.payment.core.provider.paypal.PayPalProviderServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;


public class MockProviderRoutingServiceImpl implements ProviderRoutingService {
    @Autowired
    private MockPaymentProviderServiceImpl mockPaymentProviderService;
    @Autowired
    private MockWalletServiceImpl mockWalletService;
    @Autowired
    private PayPalProviderServiceImpl paypalProviderService;
    @Autowired
    private AdyenProviderServiceImpl adyenProviderService;
    @Override
    public PaymentProviderService getPaymentProvider(PIType piType) {
        if(piType.equals(PIType.CREDITCARD)){
            return mockPaymentProviderService;
        } else if(piType.equals(PIType.STOREDVALUE)){
            return mockWalletService;
        }else if(piType.equals(PIType.PAYPAL)){
            return paypalProviderService;
        }else if(piType.equals(PIType.OTHERS)){
            return adyenProviderService;
        }
        return null;
    }

    @Override
    public void updatePaymentConfiguration() {

    }
}