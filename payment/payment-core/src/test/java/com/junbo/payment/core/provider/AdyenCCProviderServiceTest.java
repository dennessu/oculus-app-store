package com.junbo.payment.core.provider;

import com.junbo.payment.core.BaseTest;
import com.junbo.payment.core.PaymentCallbackService;
import com.junbo.payment.core.PaymentInstrumentService;
import com.junbo.payment.core.PaymentTransactionService;
import com.junbo.payment.core.provider.adyen.AdyenCCProivderServiceImpl;
import com.junbo.payment.core.provider.adyen.AdyenProviderServiceImpl;
import com.junbo.payment.spec.enums.PaymentStatus;
import com.junbo.payment.spec.model.ChargeInfo;
import com.junbo.payment.spec.model.PaymentInstrument;
import com.junbo.payment.spec.model.PaymentTransaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.math.BigDecimal;

/**
 * Created by Administrator on 14-5-20.
 */
public class AdyenCCProviderServiceTest  extends BaseTest {

    private AdyenCCProivderServiceImpl adyenProviderService;

    private PaymentInstrumentService mockAdyenPIService;

    protected PaymentTransactionService mockAdyenPaymentService;

    public void setAdyenProviderService(AdyenCCProivderServiceImpl adyenProviderService) {
        this.adyenProviderService = adyenProviderService;
    }
    @Autowired
    public void setMockAdyenPIService(@Qualifier("mockAdyenPaymentInstrumentService")PaymentInstrumentService mockAdyenPIService) {
        this.mockAdyenPIService = mockAdyenPIService;
    }
    @Autowired
    public void setMockAdyenPaymentService(@Qualifier("mockAdyenPaymentService")PaymentTransactionService mockAdyenPaymentService) {
        this.mockAdyenPaymentService = mockAdyenPaymentService;
    }
    @Test(enabled = false)
    public void testAddPI(){
        PaymentInstrument request = buildPIRequest();
        request.setBillingAddressId(null);
        request.getTypeSpecificDetails().setExpireDate("2016-06");
        request.getTypeSpecificDetails().setEncryptedCvmCode("737");
        String encrypteData = "adyenjs_0_1_4$ZrYktq0qF3lD5iQvV2OQQRP1PpsnJ7qVyc4VxIVXAy+PSFpP75RDHCSTnfbmRoM4f6+cv9yFP2GBF6IAec1mwsoWU2gDwdq1X2i2+i/mnJVCLOjrcVuQFFtghbwoxeimReG7Aj9RRgtpPjcPNPQSeFLG4AGA6wepz/pISN2V6/V9YTxApg/B1b0ioCTa6Pm6ZX1b76/1FR4Yp+HI/7cBPEeHTOYRf5gSlQRz5CHM+9OaG18iYA3iP9lPU7RorMfoUjAGJTCDp8KQGgqRuKGHxRGcZJytHNkxnvHYojJgUMHik9fg2GkSg7ZF+i7VJqrvQ31Ia7vSMIqboHPPl/kjDA==$5csR6BSyZCcgAOUq+NEbOd8TyIB9Z/1eN0atMLBKrnKdt/aifJ6Ep47u54WRFyQZLB11cn+s0hj+8Tyn0CEgtmzIAhtGcLwIg1vXkVsGDYIGFGlRbSzg9BfrWzGKuW/fvAigMPzwGg/cfW5sCjSBYGJTrHUIU69jsfGuHA6lvE8tydHWL69tvYND803u3R0JYL08/YXFZnXqW539MrfSahzbfwcBfsNf";
        //AccountNumber is the js encrypted value for card : 4111111111111111, 737, 2016-06request.setAccountNum("adyenjs_0_1_4$ETMz9n0SL1qOEVhAdvR5EMCqInhdy3oaz67zu1A5RAOhQU8XXbVSl7YfAUkFhJtRb8r5xL6PyFWGRYzY8y7vvqfSEuYz4N1zLykXUJJPTyB+3YSktR+gMevky2OATe1mvJFfMu3hfhSz7oMPgEW7VTDOU5E/FbYTZSNImTiZUoiHFMuIj2h1uk3L2jq6E3OBWPWynmnD1t7A8A6M/RJN/RlDT+YKzt4Bi/LQP11YmG9V+L8vSGX5Um0EnI3h2XIwYEUwI94f9yn3IDEPdM/GULefxwBsz/1mELJAt0jzelKN8FLhgnjvgZL2gZAEdbsfhaprWqLq6KhPIJ82G6Ncww==$qIS/ZEhUhtLuDgXRU6iuIul/zSdQg8mcvQiV2RkOSl2oFkqM6Rs34gbBZc1QKN8EbjNLxWqmwG9OMJfz8EaDGttEOkCGThuljE8Z+co+pqydtNaHmGBbKDHa3JkdfSHJBYMyx6G+c7JMMvNkQ2F1cG+XjwqCM1mMC13gIcg7I5Rc6tzd4x+uWgYaIFrpEnyPZyN7KWFJ0UczfO1BOQzQL7dMzPlE/y6T");
        PaymentInstrument result = null;
        result = mockAdyenPIService.add(request).get();
        Assert.assertNotNull(result.getExternalToken());
    }

    @Test(enabled = false)
    public void testAddPIAndAuthCapture(){
        PaymentInstrument request = buildPIRequest();
        request.setBillingAddressId(null);
        request.getTypeSpecificDetails().setExpireDate("2016-06");
        request.getTypeSpecificDetails().setEncryptedCvmCode("737");
        //AccountNumber is the js encrypted value for card : 4111111111111111, 737, 2016-06
        request.setAccountNum("adyenjs_0_1_4$F7wAyg3KGFWTZx49/JEqbhJfUtHW6pGzgpQuVcXKS0imvTVY7YJMO7tRi7ABYpwCGo8b9pvRcg7139vJOpDxqDKAXtKSU4G6PdI6MPFsHzaBaslGUvOZ4X1W+jwjruHizk/9O5a1sn8kE976g4ulhrrjJbf6DKI95UMmE6zGd7Osq//NbTnZGAELlBUpiv7qHCDQUay59uiJ/Missgr9ZYZfZPOMePc1y6bIE4754rfsQwycRY1czD9nG74vgRYHVplung0pJzNnd6EnYkji4j0yc/ZPfkOHpnJ9e0IBpE3U1Ijvm8TQiF19kmjqcPqh0j+UNysKyl1rp2c3nwqNtg==$jT6Yj594M+XkdUvZa9d/2QCDI8d+KYy7uCEFdpKiGZtTO0R54dBjr8G4oHKpL3XTU6gQqv/NMNpM47etpJbtxURQ6WAgtzpLLvf06Gfl7R443uNCRKlbMsEwwwUTA+WwNWhriKLXRH/t");
        PaymentInstrument result = null;
        result = mockAdyenPIService.add(request).get();
        Assert.assertNotNull(result.getExternalToken());

        PaymentTransaction payment = new PaymentTransaction();
        payment.setBillingRefId("123");
        payment.setUserId(userId);
        payment.setPaymentInstrumentId(result.getId());
        payment.setTrackingUuid(generateUUID());
        payment.setChargeInfo(new ChargeInfo() {
            {
                setCurrency("USD");
                setAmount(new BigDecimal(1500.00));
            }
        });
        PaymentTransaction autResult = mockAdyenPaymentService.authorize(payment).get();
        Assert.assertNotNull(autResult.getExternalToken());
        Assert.assertEquals(autResult.getStatus(), PaymentStatus.AUTHORIZED.toString());

        PaymentTransaction capResult = mockAdyenPaymentService.capture(autResult.getId(), payment).get();
        Assert.assertEquals(capResult.getStatus(), PaymentStatus.SETTLEMENT_SUBMITTED.toString());
    }

    @Test(enabled = false)
    public void testAddPIAndChargeRefund(){
        PaymentInstrument request = buildPIRequest();
        request.setBillingAddressId(null);
        request.getTypeSpecificDetails().setExpireDate("2016-06");
        request.getTypeSpecificDetails().setEncryptedCvmCode("737");
        //AccountNumber is the js encrypted value for card : 4111111111111111, 737, 2016-06
        request.setAccountNum("adyenjs_0_1_4$F7wAyg3KGFWTZx49/JEqbhJfUtHW6pGzgpQuVcXKS0imvTVY7YJMO7tRi7ABYpwCGo8b9pvRcg7139vJOpDxqDKAXtKSU4G6PdI6MPFsHzaBaslGUvOZ4X1W+jwjruHizk/9O5a1sn8kE976g4ulhrrjJbf6DKI95UMmE6zGd7Osq//NbTnZGAELlBUpiv7qHCDQUay59uiJ/Missgr9ZYZfZPOMePc1y6bIE4754rfsQwycRY1czD9nG74vgRYHVplung0pJzNnd6EnYkji4j0yc/ZPfkOHpnJ9e0IBpE3U1Ijvm8TQiF19kmjqcPqh0j+UNysKyl1rp2c3nwqNtg==$jT6Yj594M+XkdUvZa9d/2QCDI8d+KYy7uCEFdpKiGZtTO0R54dBjr8G4oHKpL3XTU6gQqv/NMNpM47etpJbtxURQ6WAgtzpLLvf06Gfl7R443uNCRKlbMsEwwwUTA+WwNWhriKLXRH/t");
        PaymentInstrument result = null;
        result = mockAdyenPIService.add(request).get();
        Assert.assertNotNull(result.getExternalToken());

        PaymentTransaction payment = new PaymentTransaction();
        payment.setBillingRefId("123");
        payment.setUserId(userId);
        payment.setPaymentInstrumentId(result.getId());
        payment.setTrackingUuid(generateUUID());
        payment.setChargeInfo(new ChargeInfo() {
            {
                setCurrency("USD");
                setAmount(new BigDecimal(1500.00));
            }
        });
        PaymentTransaction autResult = mockAdyenPaymentService.charge(payment).get();
        Assert.assertNotNull(autResult.getExternalToken());
        Assert.assertEquals(autResult.getStatus(), PaymentStatus.SETTLEMENT_SUBMITTED.toString());
        payment.setChargeInfo(null);
        PaymentTransaction cancelResult = mockAdyenPaymentService.reverse(autResult.getId(), payment).get();
        Assert.assertEquals(cancelResult.getStatus(), PaymentStatus.REVERSED.toString());
    }
}
