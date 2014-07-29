package com.junbo.payment.db;

import com.junbo.common.id.PIType;
import com.junbo.payment.db.repo.facade.PaymentInstrumentRepositoryFacade;
import com.junbo.payment.spec.enums.CreditCardType;
import com.junbo.payment.spec.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Date;

public class PaymentRepoTest extends BaseTest {
    @Autowired
    private PaymentInstrumentRepositoryFacade piRepo;

    @Test
    public void testRepo() {
        PaymentInstrument pi = buildPIRequest();
        piRepo.save(pi);
        PaymentInstrument result = piRepo.getByPIId(pi.getId());
        Assert.assertEquals(pi.getAccountName(), result.getAccountName());
        Assert.assertEquals(pi.getAccountNumber(), result.getAccountNumber());
    }

    private PaymentInstrument buildPIRequest(){
        Long userId = generateShardId();
        PaymentInstrument pi = new PaymentInstrument();
        pi.setUserId(userId);
        pi.setType(PIType.CREDITCARD.getId());
        pi.setAccountName("David");
        pi.setAccountNumber("1111");
        pi.setIsActive(true);
        pi.setBillingAddressId(generateShardId(userId));
        pi.setPhoneNumber(12345676L);
        pi.setTypeSpecificDetails(new TypeSpecificDetails() {
            {
                setExpireDate("2025-10-12");
                setCreditCardType(CreditCardType.VISA.toString());
                setLastBillingDate(new Date());
            }
        });
        pi.setExternalToken("123");
        return pi;
    }
}
