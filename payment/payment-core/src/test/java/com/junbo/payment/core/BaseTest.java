package com.junbo.payment.core;

import com.junbo.langur.core.transaction.AsyncTransactionTemplate;
import com.junbo.payment.spec.enums.PIType;
import com.junbo.payment.spec.model.Address;
import com.junbo.payment.spec.model.PaymentInstrument;
import com.junbo.payment.spec.model.TypeSpecificDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.UUID;
import java.util.concurrent.ExecutionException;

@ContextConfiguration(locations = {"classpath:spring/context-test.xml"})
@TransactionConfiguration(defaultRollback = false)
@Transactional("transactionManager")
public abstract class BaseTest extends AbstractTestNGSpringContextTests {
    public final Long userId = 1493188608L;
    @Autowired
    protected PlatformTransactionManager transactionManager;
    @Autowired
    public void setPiService(@Qualifier("mockPaymentInstrumentService")PaymentInstrumentService piService) {
        this.piService = piService;
    }
    @Autowired
    public void setPaymentService(@Qualifier("mockPaymentService")PaymentTransactionService paymentService) {
        this.paymentService = paymentService;
    }

    protected PaymentInstrumentService piService;
    protected PaymentTransactionService paymentService;

    protected long generateLong() {
        return System.currentTimeMillis();
    }

    protected UUID generateUUID() {
        return UUID.randomUUID();
    }

    //commit addPI since there is standalone commit in payment transaction, so that PI is available fir them
    public PaymentInstrument addPI(final PaymentInstrument request){
        AsyncTransactionTemplate template = new AsyncTransactionTemplate(transactionManager);
        template.setPropagationBehavior(TransactionTemplate.PROPAGATION_REQUIRES_NEW);
        return template.execute(new TransactionCallback<PaymentInstrument>() {
            public PaymentInstrument doInTransaction(TransactionStatus txnStatus) {
                try {
                    return piService.add(request).wrapped().get();
                } catch (InterruptedException e) {
                    return null;
                } catch (ExecutionException e) {
                    return null;
                }
            }
        });
    }

    public PaymentInstrument buildPIRequest() {
        PaymentInstrument request = buildBasePIRequest();
        request.setType(PIType.CREDITCARD.getId());
        request.setTypeSpecificDetails(new TypeSpecificDetails() {
            {
                setEncryptedCvmCode("111");
                setExpireDate("2025-11");
            }
        });
        return request;
    }

    public PaymentInstrument buildBasePIRequest(){
        PaymentInstrument request = new PaymentInstrument();
        request.setUserId(userId);
        request.setTrackingUuid(generateUUID());
        request.setAccountName("ut");
        request.setIsValidated(true);
        request.setAccountNum("4111111111111111");
        request.setBillingAddressId(123L);
        request.setPhoneNumber(12344555L);
        return request;
    }
}
