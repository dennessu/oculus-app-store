package com.junbo.payment.core.mock;

import com.junbo.langur.core.promise.Promise;
import com.junbo.payment.core.provider.AbstractPaymentProviderService;
import com.junbo.payment.spec.enums.PaymentStatus;
import com.junbo.payment.spec.model.PaymentInstrument;
import com.junbo.payment.spec.model.PaymentTransaction;

import javax.ws.rs.core.Response;
import java.util.List;

/**
 * mock wallet service.
 */
public class MockWalletServiceImpl extends AbstractPaymentProviderService {
    private static final String PROVIDER_NAME = "Wallet";
    private static final String WALLET_ACCT = "12345";
    private static final String TRANSACTION_ID = "54321";
    @Override
    public String getProviderName() {
        return PROVIDER_NAME;
    }

    @Override
    public void clonePIResult(PaymentInstrument source, PaymentInstrument target) {
        target.setAccountNumber(source.getAccountNumber());
    }

    @Override
    public void cloneTransactionResult(PaymentTransaction source, PaymentTransaction target) {
        target.setExternalToken(source.getExternalToken());
        target.setStatus(source.getStatus());
    }

    @Override
    public Promise<PaymentInstrument> add(PaymentInstrument request) {
        request.setAccountNumber(WALLET_ACCT);
        return Promise.pure(request);
    }

    @Override
    public Promise<Response> delete(PaymentInstrument pi) {
        return null;
    }

    @Override
    public Promise<PaymentInstrument> getByInstrumentToken(String token) {
        return Promise.pure(null);
    }

    @Override
    public Promise<PaymentTransaction> authorize(PaymentInstrument pi, PaymentTransaction paymentRequest) {
        return null;
    }

    @Override
    public Promise<PaymentTransaction> capture(String transactionId, PaymentTransaction paymentRequest) {
        return null;
    }

    @Override
    public Promise<PaymentTransaction> charge(PaymentInstrument pi, PaymentTransaction paymentRequest) {
        paymentRequest.setExternalToken(TRANSACTION_ID);
        paymentRequest.setStatus(PaymentStatus.SETTLED.toString());
        return Promise.pure(paymentRequest);
    }

    @Override
    public Promise<PaymentTransaction> reverse(String transactionId, PaymentTransaction paymentRequest) {
        return null;
    }

    @Override
    public Promise<PaymentTransaction> refund(String transactionId, PaymentTransaction request) {
        return null;
    }

    @Override
    public List<PaymentTransaction> getByBillingRefId(String orderId) {
        return null;
    }

    @Override
    public Promise<PaymentTransaction> getByTransactionToken(PaymentTransaction request) {
        return Promise.pure(null);
    }
}
