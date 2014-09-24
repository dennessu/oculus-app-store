package com.junbo.payment.core.mock;

import com.junbo.common.id.PIType;
import com.junbo.payment.core.provider.PaymentProvider;
import com.junbo.payment.core.provider.PaymentProviderService;
import com.junbo.payment.core.provider.ProviderRoutingService;
import com.junbo.payment.core.provider.adyen.AdyenCCProivderServiceImpl;
import com.junbo.payment.core.provider.adyen.AdyenProviderServiceImpl;
import com.junbo.payment.core.provider.facebook.FacebookCCProviderServiceImpl;
import com.junbo.payment.core.provider.paypal.PayPalProviderServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by wenzhu on 9/19/14.
 */
public class MockFBProviderRoutingServiceImpl  implements ProviderRoutingService {


    public void setFacebookCCProviderService(FacebookCCProviderServiceImpl facebookCCProviderService) {
        this.facebookCCProviderService = facebookCCProviderService;
    }

    private FacebookCCProviderServiceImpl facebookCCProviderService;
    @Autowired
    private MockWalletServiceImpl mockWalletService;
    @Autowired
    private PayPalProviderServiceImpl paypalProviderService;
    @Autowired
    private AdyenProviderServiceImpl adyenProviderService;
    @Override
    public PaymentProviderService getPaymentProvider(PIType piType) {
        if(piType.equals(PIType.CREDITCARD)){
            return facebookCCProviderService;
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
    public PaymentProviderService getProviderByName(String provider) {
        if(provider.equalsIgnoreCase(PaymentProvider.BrainTree.toString())){
            return facebookCCProviderService;
        }else if(provider.equalsIgnoreCase(PaymentProvider.Wallet.toString())){
            return mockWalletService;
        }else if(provider.equalsIgnoreCase(PaymentProvider.PayPal.toString())){
            return paypalProviderService;
        }else if(provider.equalsIgnoreCase(PaymentProvider.Adyen.toString())){
            return adyenProviderService;
        }else if(provider.equalsIgnoreCase(PaymentProvider.AdyenCC.toString())){
            return facebookCCProviderService;
        }else if(provider.equalsIgnoreCase(PaymentProvider.FacebookCC.toString())){
            return facebookCCProviderService;
        }
        return facebookCCProviderService;
    }

    @Override
    public void updatePaymentConfiguration() {

    }
}
