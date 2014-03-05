package com.junbo.payment.core.mock;


import com.junbo.langur.core.promise.Promise;
import com.junbo.payment.core.provider.PaymentProviderService;
import com.junbo.payment.spec.enums.CreditCardType;
import com.junbo.payment.spec.enums.PIStatus;
import com.junbo.payment.spec.model.ChargeInfo;
import com.junbo.payment.spec.model.PaymentInstrument;
import com.junbo.payment.spec.model.PaymentTransaction;

import java.util.Date;


public class MockPaymentProviderServiceImpl implements PaymentProviderService {
    private static final String providerName = "BrainTree";
    public static final String piExternalToken = "123";
    public static final String authExternalToken = "1234";
    public static final String chargeExternalToken = "12345";

    @Override
    public String getProviderName() {
        return providerName;
    }

    @Override
    public Promise<PaymentInstrument> add(PaymentInstrument request) {
        request.setAccountNum("1111");
        request.setStatus(PIStatus.ACTIVE.toString());
        request.getCreditCardRequest().setExternalToken(piExternalToken);
        request.getCreditCardRequest().setType(CreditCardType.Visa.toString());
        request.getCreditCardRequest().setCommercial("UNKNOW");
        request.getCreditCardRequest().setDebit("UNKNOW");
        request.getCreditCardRequest().setPrepaid("UNKNOW");
        request.getCreditCardRequest().setIssueCountry("UNKNOW");
        request.getCreditCardRequest().setLastBillingDate(new Date());
        if(request.getIsValidated() == true){
            request.setLastValidatedTime(new Date());
        }
        return Promise.pure(request);
    }

    @Override
    public Promise<Void> delete(String token) {
        return null;
    }

    @Override
    public Promise<PaymentTransaction> authorize(String piToken, PaymentTransaction paymentRequest) {
        paymentRequest.setExternalToken(authExternalToken);
        return Promise.pure(paymentRequest);
    }

    @Override
    public Promise<PaymentTransaction> capture(String transactionId, PaymentTransaction paymentRequest) {
        return Promise.pure(paymentRequest);

    }

    @Override
    public Promise<PaymentTransaction> charge(String piToken, PaymentTransaction paymentRequest) {
        paymentRequest.setExternalToken(chargeExternalToken);
        return Promise.pure(paymentRequest);
    }

    @Override
    public Promise<Void> reverse(String transactionId) {
        return null;
    }

    @Override
    public void refund(String transactionId, PaymentTransaction request) {

    }
}
