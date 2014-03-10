package com.junbo.payment.db;

import com.junbo.payment.db.dao.TrackingUuidDao;
import com.junbo.payment.db.entity.paymentinstrument.AddressEntity;
import com.junbo.payment.db.entity.paymentinstrument.PaymentInstrumentEntity;
import com.junbo.payment.db.entity.paymentinstrument.PhoneEntity;
import com.junbo.payment.db.repository.PaymentInstrumentRepository;
import com.junbo.payment.spec.enums.CreditCardType;
import com.junbo.payment.spec.enums.PIStatus;
import com.junbo.payment.spec.enums.PIType;
import com.junbo.payment.spec.enums.PhoneType;
import com.junbo.payment.spec.model.Address;
import com.junbo.payment.spec.model.CreditCardRequest;
import com.junbo.payment.spec.model.PaymentInstrument;
import com.junbo.payment.spec.model.Phone;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Date;

public class PaymentRepoTest extends BaseTest {
    @Autowired
    private PaymentInstrumentRepository piRepo;

    private static final Long userId = 123l;

    @Test
    public void testRepo() {
        PaymentInstrument pi = buildPIRequest();
        piRepo.save(pi);
        PaymentInstrument result = piRepo.getByPIId(pi.getId());
        Assert.assertEquals(pi.getAccountName(), result.getAccountName());
        Assert.assertEquals(pi.getAccountNum(), result.getAccountNum());
        Assert.assertEquals(pi.getAddress().getAddressLine1(), result.getAddress().getAddressLine1());
        Assert.assertEquals(pi.getPhone().getNumber(), result.getPhone().getNumber());
    }

    private PaymentInstrument buildPIRequest(){
        PaymentInstrument pi = new PaymentInstrument();
        pi.setUserId(userId);
        pi.setType(PIType.CREDITCARD.toString());
        pi.setAccountName("David");
        pi.setAccountNum("1111");
        pi.setStatus(PIStatus.ACTIVE.toString());
        pi.setAddress(new Address(){
            {
                setAddressLine1("third street");
                setCountry("US");
                setPostalCode("12345");
            }
        });
        pi.setPhone(new Phone(){
            {
                setType(PhoneType.Home.toString());
                setNumber("12334455");
            }
        });
        pi.setCreditCardRequest(new CreditCardRequest(){
            {
                setExpireDate("2025-10-12");
                setExternalToken("123");
                setType(CreditCardType.VISA.toString());
                setLastBillingDate(new Date());
            }
        });
        return pi;
    }
}
