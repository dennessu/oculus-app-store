package com.junbo.payment.core.mock;


import com.junbo.langur.core.promise.Promise;
import com.junbo.payment.core.provider.AbstractPaymentProviderService;
import com.junbo.payment.core.provider.PaymentProviderService;
import com.junbo.payment.spec.enums.CreditCardType;
import com.junbo.payment.spec.enums.PIStatus;
import com.junbo.payment.spec.model.ChargeInfo;
import com.junbo.payment.spec.model.PaymentInstrument;
import com.junbo.payment.spec.model.PaymentTransaction;

import javax.ws.rs.core.Response;
import java.util.Date;
import java.util.List;


public class MockPaymentProviderServiceImpl extends AbstractPaymentProviderService {
    private static final String providerName = "BrainTree";
    public static final String piExternalToken = "123";
    public static final String authExternalToken = "1234";
    public static final String chargeExternalToken = "12345";

    @Override
    public String getProviderName() {
        return providerName;
    }

    @Override
    public void clonePIResult(PaymentInstrument source, PaymentInstrument target) {
        target.setAccountNum(source.getAccountNum());
        target.getCreditCardRequest().setExternalToken(source.getCreditCardRequest().getExternalToken());
        target.getCreditCardRequest().setType(source.getCreditCardRequest().getType());
        target.getCreditCardRequest().setCommercial(source.getCreditCardRequest().getCommercial());
        target.getCreditCardRequest().setDebit(source.getCreditCardRequest().getDebit());
        target.getCreditCardRequest().setPrepaid(source.getCreditCardRequest().getPrepaid());
        target.getCreditCardRequest().setIssueCountry(source.getCreditCardRequest().getIssueCountry());
    }

    @Override
    public void cloneTransactionResult(PaymentTransaction source, PaymentTransaction target) {
        target.setExternalToken(source.getExternalToken());
    }

    @Override
    public Promise<PaymentInstrument> add(PaymentInstrument request) {
        request.setAccountNum("1111");
        request.setStatus(PIStatus.ACTIVE.toString());
        request.getCreditCardRequest().setExternalToken(piExternalToken);
        request.getCreditCardRequest().setType(CreditCardType.VISA.toString());
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
    public Promise<Response> delete(PaymentInstrument request) {
        return null;
    }

    @Override
    public Promise<PaymentTransaction> authorize(PaymentInstrument request, PaymentTransaction paymentRequest) {
        paymentRequest.setExternalToken(authExternalToken);
        return Promise.pure(paymentRequest);
    }

    @Override
    public Promise<PaymentTransaction> capture(String transactionId, PaymentTransaction paymentRequest) {
        return Promise.pure(paymentRequest);

    }

    @Override
    public Promise<PaymentTransaction> charge(PaymentInstrument request, PaymentTransaction paymentRequest) {
        paymentRequest.setExternalToken(chargeExternalToken);
        return Promise.pure(paymentRequest);
    }

    @Override
    public Promise<PaymentTransaction> reverse(String transactionId, PaymentTransaction request) {
        return Promise.pure(request);
    }

    @Override
    public Promise<PaymentTransaction> refund(String transactionId, PaymentTransaction request) {
        return Promise.pure(request);
    }

    @Override
    public List<PaymentTransaction> getByBillingRefId(String orderId) {
        return null;
    }

    @Override
    public Promise<PaymentTransaction> getByTransactionToken(String token) {
        return null;
    }
}
