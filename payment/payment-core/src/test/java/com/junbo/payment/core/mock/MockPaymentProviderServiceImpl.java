package com.junbo.payment.core.mock;


import com.junbo.langur.core.promise.Promise;
import com.junbo.payment.common.CommonUtil;
import com.junbo.payment.core.provider.AbstractPaymentProviderService;
import com.junbo.payment.spec.enums.CreditCardType;
import com.junbo.payment.spec.enums.PaymentStatus;
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
        target.setExternalToken(source.getExternalToken());
        target.getCreditCardRequest().setType(source.getCreditCardRequest().getType());
        target.getCreditCardRequest().setCommercial(source.getCreditCardRequest().getCommercial());
        target.getCreditCardRequest().setDebit(source.getCreditCardRequest().getDebit());
        target.getCreditCardRequest().setPrepaid(source.getCreditCardRequest().getPrepaid());
        target.getCreditCardRequest().setIssueCountry(source.getCreditCardRequest().getIssueCountry());
    }

    @Override
    public void cloneTransactionResult(PaymentTransaction source, PaymentTransaction target) {
        target.setExternalToken(source.getExternalToken());
        if(!CommonUtil.isNullOrEmpty(source.getStatus())){
            target.setStatus(source.getStatus());
        }
    }

    @Override
    public Promise<PaymentInstrument> add(PaymentInstrument request) {
        request.setAccountNum("1111");
        request.setIsActive(true);
        request.setExternalToken(piExternalToken);
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
        paymentRequest.setStatus(PaymentStatus.AUTHORIZED.toString());
        return Promise.pure(paymentRequest);
    }

    @Override
    public Promise<PaymentTransaction> capture(String transactionId, PaymentTransaction paymentRequest) {
        paymentRequest.setStatus(PaymentStatus.SETTLEMENT_SUBMITTED.toString());
        return Promise.pure(paymentRequest);

    }

    @Override
    public Promise<PaymentTransaction> charge(PaymentInstrument request, PaymentTransaction paymentRequest) {
        paymentRequest.setExternalToken(chargeExternalToken);
        paymentRequest.setStatus(PaymentStatus.SETTLEMENT_SUBMITTED.toString());
        return Promise.pure(paymentRequest);
    }

    @Override
    public Promise<PaymentTransaction> reverse(String transactionId, PaymentTransaction request) {
        request.setStatus(PaymentStatus.REVERSED.toString());
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
        return Promise.pure(null);
    }
}
