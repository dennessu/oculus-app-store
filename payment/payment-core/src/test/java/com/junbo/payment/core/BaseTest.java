package com.junbo.payment.core;

import com.junbo.common.id.PIType;
import com.junbo.langur.core.transaction.AsyncTransactionTemplate;
import com.junbo.payment.core.provider.ProviderRoutingService;
import com.junbo.payment.spec.model.PaymentInstrument;
import com.junbo.payment.spec.model.TypeSpecificDetails;
import com.junbo.sharding.IdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;

import java.util.UUID;

@ContextConfiguration(locations = {"classpath:spring/context-test.xml"})
@TransactionConfiguration(defaultRollback = false)
@TestExecutionListeners(TransactionalTestExecutionListener.class)
@Transactional("transactionManager")
public abstract class BaseTest extends AbstractTestNGSpringContextTests {
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

    @Autowired
    @Qualifier("oculus48IdGenerator")
    protected IdGenerator idGenerator;

    protected PaymentInstrumentService piService;
    protected PaymentTransactionService paymentService;
    @Autowired
    protected ProviderRoutingService providerRoutingService;

    protected long generateUserId() {
        return idGenerator.nextId();
    }

    protected UUID generateUUID() {
        return UUID.randomUUID();
    }

    @BeforeTest
    @SuppressWarnings("deprecation")
    public void setup() {

    }

    @AfterTest
    @SuppressWarnings("deprecation")
    public void cleanup() {

    }

    //commit addPI since there is standalone commit in payment transaction, so that PI is available fir them
    public PaymentInstrument addPI(final PaymentInstrument request){
        AsyncTransactionTemplate template = new AsyncTransactionTemplate(transactionManager);
        template.setPropagationBehavior(TransactionTemplate.PROPAGATION_REQUIRES_NEW);
        return template.execute(new TransactionCallback<PaymentInstrument>() {
            public PaymentInstrument doInTransaction(TransactionStatus txnStatus) {
                return piService.add(request).get();
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
        request.setUserId(generateUserId());
        request.setTrackingUuid(generateUUID());
        request.setAccountName("ut");
        request.setIsValidated(true);
        request.setAccountNum("4111111111111111");
        request.setBillingAddressId(123L);
        request.setPhoneNumber(12344555L);
        return request;
    }
}
