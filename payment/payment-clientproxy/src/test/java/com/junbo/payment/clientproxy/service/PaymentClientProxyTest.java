package com.junbo.payment.clientproxy.service;

import com.junbo.common.enumid.CountryId;
import com.junbo.common.id.PIType;
import com.junbo.common.id.PaymentId;
import com.junbo.common.id.PaymentInstrumentId;
import com.junbo.common.id.UserId;
import com.junbo.common.json.ObjectMapperProvider;
import com.junbo.common.model.Results;
import com.junbo.ewallet.spec.model.CreditRequest;
import com.junbo.ewallet.spec.resource.WalletResource;
import com.junbo.identity.spec.v1.model.Address;
import com.junbo.identity.spec.v1.model.*;
import com.junbo.identity.spec.v1.resource.UserPersonalInfoResource;
import com.junbo.identity.spec.v1.resource.UserResource;
import com.junbo.payment.clientproxy.BaseTest;
import com.junbo.payment.clientproxy.PersonalInfoFacade;
import com.junbo.payment.spec.enums.PaymentEventType;
import com.junbo.payment.spec.enums.PaymentStatus;
import com.junbo.payment.spec.model.*;
import com.junbo.payment.spec.resource.proxy.PaymentInstrumentResourceClientProxy;
import com.junbo.payment.spec.resource.proxy.PaymentTransactionResourceClientProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;

public class PaymentClientProxyTest extends BaseTest {
    private static String BILLING_REF_ID = "123";
    @Autowired
    private PaymentInstrumentResourceClientProxy piClient;
    @Autowired
    private PaymentTransactionResourceClientProxy paymentClient;
    @Autowired
    @Qualifier("walletProxy")
    private WalletResource walletClient;
    @Autowired
    private PersonalInfoFacade personalInfoFacade;
    @Autowired
    private UserPersonalInfoResource piiClient;
    @Autowired
    private UserResource userClient;

    @Test(enabled = false)
    public void addPIAndUpdatePI() throws ExecutionException, InterruptedException {
        PaymentInstrument pi = getPaymentInstrument();
        final UserId userId = new UserId(pi.getUserId());
        final PaymentInstrument result = piClient.postPaymentInstrument(pi).get();
        Assert.assertNotNull(result.getId());
        final PaymentInstrument getResult = piClient.getById(new PaymentInstrumentId(result.getId())).get();
        Assert.assertEquals(result.getUserId(), getResult.getUserId());
        Assert.assertEquals(result.getId(), getResult.getId());
        Long newPIIId = 123456L;
        getResult.setBillingAddressId(newPIIId);
        getResult.setPhoneNumber(newPIIId);
        PaymentInstrumentId piid = new PaymentInstrumentId(getResult.getId());
        final PaymentInstrument updateResult =  piClient.update(piid,getResult).get();
        Assert.assertEquals(updateResult.getBillingAddressId(), newPIIId);
        Assert.assertEquals(updateResult.getPhoneNumber(), newPIIId);
        //updated PI should be able to auth as well:
        PaymentTransaction trx = new PaymentTransaction(){
            {
                setTrackingUuid(generateUUID());
                setUserId(userId.getValue());
                setPaymentInstrumentId(result.getId());
                setBillingRefId(BILLING_REF_ID);
                setChargeInfo(new ChargeInfo(){
                    {
                        setCurrency("USD");
                        setAmount(new BigDecimal(11.00));
                    }
                });
            }
        };
        PaymentTransaction paymentResult = paymentClient.postPaymentAuthorization(trx).get();
        Assert.assertEquals(paymentResult.getStatus().toUpperCase(), PaymentStatus.AUTHORIZED.toString());

        final PaymentInstrument getUpdatedResult = piClient.getById(new PaymentInstrumentId(result.getId())).get();
        Assert.assertEquals(updateResult.getBillingAddressId(), newPIIId);
        Assert.assertEquals(updateResult.getPhoneNumber(), newPIIId);
        final PaymentInstrument result2 = piClient.postPaymentInstrument(pi).get();
        Assert.assertNotNull(result2.getId());
        PaymentInstrumentSearchParam searchParam = new PaymentInstrumentSearchParam();
        searchParam.setUserId(userId);
        Results<PaymentInstrument> results = piClient.searchPaymentInstrument(searchParam,new PageMetaData()).get();
        Assert.assertTrue(results.getItems().size() > 1);
        piClient.delete(new PaymentInstrumentId(result.getId())).get();
        searchParam.setType(PIType.CREDITCARD.toString());
        Results<PaymentInstrument> results2 = piClient.searchPaymentInstrument(searchParam,new PageMetaData()).get();
        Assert.assertEquals(results.getItems().size(), results2.getItems().size() + 1);
    }

    @Test(enabled = false)
    public void addPIAndAuthSettle() throws ExecutionException, InterruptedException {
        PaymentInstrument pi = getPaymentInstrument();
        final UserId userId = new UserId(pi.getUserId());
        final PaymentInstrument result = piClient.postPaymentInstrument(pi).get();
        Assert.assertNotNull(result.getId());
        final PaymentInstrument getResult = piClient.getById(new PaymentInstrumentId(result.getId())).get();
        Assert.assertEquals(result.getUserId(), getResult.getUserId());
        Assert.assertEquals(result.getId(), getResult.getId());
        PaymentTransaction trx = new PaymentTransaction(){
            {
                setTrackingUuid(generateUUID());
                setUserId(userId.getValue());
                setPaymentInstrumentId(result.getId());
                setBillingRefId(BILLING_REF_ID);
                setChargeInfo(new ChargeInfo(){
                    {
                        setCurrency("USD");
                        setAmount(new BigDecimal(11.00));
                    }
                });
            }
        };
        PaymentTransaction paymentResult = paymentClient.postPaymentAuthorization(trx).get();
        Assert.assertEquals(paymentResult.getStatus().toUpperCase(), PaymentStatus.AUTHORIZED.toString());
        PaymentTransaction getAuth = paymentClient.getPayment(new PaymentId(paymentResult.getId())).get();
        Assert.assertEquals(getAuth.getPaymentInstrumentId(), paymentResult.getPaymentInstrumentId());
        Assert.assertEquals(getAuth.getStatus().toUpperCase(), PaymentStatus.AUTHORIZED.toString());
        PaymentTransaction captureTrx = new PaymentTransaction(){
            {
                setTrackingUuid(generateUUID());
                setUserId(userId.getValue());
                setBillingRefId(BILLING_REF_ID);
            }
        };
        PaymentTransaction captureResult = paymentClient.postPaymentCapture(new PaymentId(paymentResult.getId()), captureTrx).get();
        Assert.assertEquals(captureResult.getStatus().toUpperCase(), PaymentStatus.SETTLEMENT_SUBMITTED.toString());

    }

    @Test(enabled = false)
    public void chargeDifferentCurrency() throws ExecutionException, InterruptedException {
        PaymentInstrument pi = getPaymentInstrument();
        final UserId userId = new UserId(pi.getUserId());
        final PaymentInstrument result = piClient.postPaymentInstrument(pi).get();
        Assert.assertNotNull(result.getId());
        final PaymentInstrument getResult = piClient.getById(new PaymentInstrumentId(result.getId())).get();
        Assert.assertEquals(result.getUserId(), getResult.getUserId());
        Assert.assertEquals(result.getId(), getResult.getId());
        PaymentTransaction trx = new PaymentTransaction(){
            {
                setTrackingUuid(generateUUID());
                setUserId(userId.getValue());
                setPaymentInstrumentId(result.getId());
                setBillingRefId(BILLING_REF_ID);
                setChargeInfo(new ChargeInfo(){
                    {
                        setCurrency("EUR");
                        setAmount(new BigDecimal(11.00));
                    }
                });
            }
        };
        PaymentTransaction paymentResult = paymentClient.postPaymentAuthorization(trx).get();
        Assert.assertEquals(paymentResult.getStatus().toUpperCase(), PaymentStatus.AUTHORIZED.toString());
        PaymentTransaction getAuth = paymentClient.getPayment(new PaymentId(paymentResult.getId())).get();
        Assert.assertEquals(getAuth.getPaymentInstrumentId(), paymentResult.getPaymentInstrumentId());
        Assert.assertEquals(getAuth.getStatus().toUpperCase(), PaymentStatus.AUTHORIZED.toString());
        PaymentTransaction captureTrx = new PaymentTransaction(){
            {
                setTrackingUuid(generateUUID());
                setUserId(userId.getValue());
                setBillingRefId(BILLING_REF_ID);
            }
        };
        PaymentTransaction captureResult = paymentClient.postPaymentCapture(new PaymentId(paymentResult.getId()), captureTrx).get();
        Assert.assertEquals(captureResult.getStatus().toUpperCase(), PaymentStatus.SETTLEMENT_SUBMITTED.toString());

    }

    @Test(enabled = false)
    public void addSVPIAndCharge() throws ExecutionException, InterruptedException {
        PaymentInstrument pi = getPaymentInstrument();
        final UserId userId = new UserId(pi.getUserId());
        pi.setType(PIType.STOREDVALUE.getId());
        pi.getTypeSpecificDetails().setStoredValueCurrency("usd");
        final PaymentInstrument result = piClient.postPaymentInstrument(pi).get();
        Assert.assertNotNull(result.getId());
        //credit balance
        CreditRequest cr = new CreditRequest();
        cr.setUserId(userId.getValue());
        BigDecimal amount = new BigDecimal("1000.00");
        cr.setAmount(amount);
        cr.setCurrency("usd");
        walletClient.credit(cr).get();
        PaymentInstrument getResult =  piClient.getById(new PaymentInstrumentId(result.getId())).get();
        Assert.assertTrue(getResult.getTypeSpecificDetails().getStoredValueBalance().equals(amount));
        PaymentTransaction trx = new PaymentTransaction(){
            {
                setTrackingUuid(generateUUID());
                setUserId(userId.getValue());
                setPaymentInstrumentId(result.getId());
                setBillingRefId(BILLING_REF_ID);
                setChargeInfo(new ChargeInfo(){
                    {
                        setCurrency("USD");
                        setAmount(new BigDecimal("15.00"));
                    }
                });
            }
        };
        PaymentTransaction paymentResult = paymentClient.postPaymentCharge(trx).get();
        Assert.assertEquals(paymentResult.getStatus().toUpperCase(), PaymentStatus.SETTLED.toString());
        //get Balance again
        PaymentInstrument getResult2 =  piClient.getById(new PaymentInstrumentId(result.getId())).get();
        Assert.assertTrue(getResult2.getTypeSpecificDetails().getStoredValueBalance().equals(new BigDecimal("985.00")));
        //Credit Balance and get again
        trx.getChargeInfo().setAmount(new BigDecimal("1000.00"));
        paymentResult = paymentClient.postPaymentCredit(trx).get();
        Assert.assertEquals(paymentResult.getStatus().toUpperCase(), PaymentStatus.SETTLED.toString());
        PaymentInstrument getResult3 =  piClient.getById(new PaymentInstrumentId(result.getId())).get();
        Assert.assertTrue(getResult3.getTypeSpecificDetails().getStoredValueBalance().equals(new BigDecimal("1985.00")));
        PaymentInstrument pi2 = getPaymentInstrument();
        pi2.setUserId(pi.getUserId());
        final PaymentInstrument result2 = piClient.postPaymentInstrument(pi2).get();
        PaymentInstrumentSearchParam searchParam = new PaymentInstrumentSearchParam();
        searchParam.setUserId(userId);
        Results<PaymentInstrument> results = piClient.searchPaymentInstrument(searchParam,new PageMetaData()).get();
        Assert.assertEquals(results.getItems().size(), 2);
        PaymentInstrument walletPI;
        if(results.getItems().get(0).getType().equals(PIType.STOREDVALUE.getId())){
            walletPI = results.getItems().get(0);
        }else{
            walletPI = results.getItems().get(1);
        }
        Assert.assertTrue(walletPI.getTypeSpecificDetails().getStoredValueBalance().equals(new BigDecimal("1985.00")));
    }

    private PaymentInstrument getPaymentInstrument() {
        User user = new User();
        user.setUsername("utTest" + getLongId());
        user = userClient.create(user).get();
        final Long userId = user.getId().getValue();
        final Email email = new Email();
        email.setInfo("uttest" + userId + "@junbo.com");
        UserPersonalInfo emailInfo = new UserPersonalInfo(){
            {
                setUserId(new UserId(userId));
                setType("EMAIL");
                setValue(ObjectMapperProvider.instance().valueToTree(email));
            }
        };
        UserPersonalInfo emailPerInfo = piiClient.create(emailInfo).get();

        final UserName userName = new UserName();
        userName.setFamilyName("ut"+ userId);
        userName.setGivenName("payment");
        UserPersonalInfo userNameInfo = new UserPersonalInfo(){
            {
                setUserId(new UserId(userId));
                setType("NAME");
                setValue(ObjectMapperProvider.instance().valueToTree(userName));
            }
        };
        UserPersonalInfo userNamePerInfo = piiClient.create(userNameInfo).get();
        UserPersonalInfoLink nameLink = new UserPersonalInfoLink();
        nameLink.setValue(userNamePerInfo.getId());
        nameLink.setUserId(new UserId(userId));
        user.setName(nameLink);
        UserPersonalInfoLink emailLink = new UserPersonalInfoLink();
        emailLink.setValue(emailPerInfo.getId());
        emailLink.setUserId(new UserId(userId));
        emailLink.setIsDefault(true);
        user.setEmails(Arrays.asList(emailLink));
        userClient.put(new UserId(userId), user).get();

        com.junbo.identity.spec.v1.model.Address address = new Address();
        address.setCountryId(new CountryId("US"));
        address.setStreet1("1st street");
        address.setPostalCode("12345");
        final Long addressId = personalInfoFacade.createBillingAddress(userId, address).get().getValue();
        return new PaymentInstrument(){
                {
                    setAccountName("ut");
                    setAccountNum("4111111111111111");
                    setIsValidated(false);
                    setType(0L);
                    setTrackingUuid(generateUUID());
                    setUserId(userId);
                    setLabel("my first card");
                    setBillingAddressId(addressId);
                    setTypeSpecificDetails(new TypeSpecificDetails() {
                        {
                            setExpireDate("2025-12");
                            setEncryptedCvmCode("111");
                        }
                    });
                }
            };
    }

    @Test(enabled = false)
    public void addPIAndAuthPartialSettle() throws ExecutionException, InterruptedException {
        PaymentInstrument pi = getPaymentInstrument();
        final UserId userId = new UserId(pi.getUserId());
        final PaymentInstrument result = piClient.postPaymentInstrument(pi).get();
        Assert.assertNotNull(result.getId());
        PaymentTransaction trx = new PaymentTransaction(){
            {
                setTrackingUuid(generateUUID());
                setUserId(userId.getValue());
                setPaymentInstrumentId(result.getId());
                setBillingRefId(BILLING_REF_ID);
                setChargeInfo(new ChargeInfo(){
                    {
                        setCurrency("USD");
                        setAmount(new BigDecimal(12.00));
                    }
                });
            }
        };
        PaymentTransaction paymentResult = paymentClient.postPaymentAuthorization(trx).get();
        Assert.assertEquals(paymentResult.getStatus().toUpperCase(), PaymentStatus.AUTHORIZED.toString());
        PaymentTransaction captureTrx = new PaymentTransaction(){
            {
                setTrackingUuid(generateUUID());
                setUserId(userId.getValue());
                setBillingRefId(BILLING_REF_ID);
                setChargeInfo(new ChargeInfo(){
                    {
                        setCurrency("USD");
                        setAmount(new BigDecimal(10.00));
                    }
                });
            }
        };
        PaymentTransaction captureResult = paymentClient.postPaymentCapture(new PaymentId(paymentResult.getId()), captureTrx).get();
        Assert.assertEquals(captureResult.getStatus().toUpperCase(), PaymentStatus.SETTLEMENT_SUBMITTED.toString());
    }

    @Test(enabled = false)
    public void settleRefundCreditCard() throws ExecutionException, InterruptedException {
        //manual step needed: find a settled/settling transaction from BrainTree, and update a transaction in local
        // DB with external_token equals to the BT's transaction's ID
        Long trasnactionId = 402849792L;
        final Long userID = 1493188608L;
        PaymentTransaction trx = new PaymentTransaction(){
            {
                setTrackingUuid(generateUUID());
                setUserId(userID);
                setChargeInfo(new ChargeInfo(){
                    {
                        setBillingRefId("123");
                        setCurrency("USD");
                        setAmount(new BigDecimal(3.00));
                    }
                });
            }
        };
        PaymentTransaction paymentResult = paymentClient.refundPayment(new PaymentId(trasnactionId), trx).get();
        Assert.assertEquals(paymentResult.getStatus().toUpperCase(), PaymentStatus.REFUNDED.toString());
        //Continue Partial Refund
        trx.setTrackingUuid(generateUUID());
        paymentResult = paymentClient.refundPayment(new PaymentId(trasnactionId), trx).get();
        Assert.assertEquals(paymentResult.getStatus().toUpperCase(), PaymentStatus.REFUNDED.toString());
        //Continue Refund all the left
        trx.setChargeInfo(null);
        trx.setTrackingUuid(generateUUID());
        paymentResult = paymentClient.refundPayment(new PaymentId(trasnactionId), trx).get();
        PaymentTransaction getResult = paymentClient.getPayment(new PaymentId(paymentResult.getId())).get();
        Assert.assertEquals(getResult.getStatus().toUpperCase(), PaymentStatus.REFUNDED.toString());
        boolean exception = false;
        try{
            trx.setTrackingUuid(generateUUID());
            paymentResult = paymentClient.refundPayment(new PaymentId(trasnactionId), trx).get();
        }catch(Exception ex){
            //exprcted exception as all the money has been refunded
            exception = true;
        }
        Assert.assertTrue(exception);
    }

    @Test(enabled = false)
    public void settleRefundSV() throws ExecutionException, InterruptedException {
        PaymentInstrument pi = getPaymentInstrument();
        final UserId userId = new UserId(pi.getUserId());
        pi.setType(PIType.STOREDVALUE.getId());
        pi.getTypeSpecificDetails().setStoredValueCurrency("usd");
        final PaymentInstrument result = piClient.postPaymentInstrument(pi).get();
        Assert.assertNotNull(result.getId());
        //credit balance
        CreditRequest cr = new CreditRequest();
        cr.setUserId(userId.getValue());
        BigDecimal amount = new BigDecimal("1000.00");
        cr.setAmount(amount);
        cr.setCurrency("usd");
        walletClient.credit(cr).get();
        PaymentInstrument getResult =  piClient.getById(new PaymentInstrumentId(result.getId())).get();
        Assert.assertTrue(getResult.getTypeSpecificDetails().getStoredValueBalance().equals(amount));
        PaymentTransaction trx = new PaymentTransaction(){
            {
                setTrackingUuid(generateUUID());
                setUserId(userId.getValue());
                setPaymentInstrumentId(result.getId());
                setBillingRefId(BILLING_REF_ID);
                setChargeInfo(new ChargeInfo(){
                    {
                        setCurrency("USD");
                        setAmount(new BigDecimal("15.00"));
                    }
                });
            }
        };
        PaymentTransaction paymentResult = paymentClient.postPaymentCharge(trx).get();
        Assert.assertEquals(paymentResult.getStatus().toUpperCase(), PaymentStatus.SETTLED.toString());
        //get Balance again
        PaymentInstrument getResult2 =  piClient.getById(new PaymentInstrumentId(result.getId())).get();
        Assert.assertTrue(getResult2.getTypeSpecificDetails().getStoredValueBalance().equals(new BigDecimal("985.00")));
        //Credit Balance and get again
        trx.getChargeInfo().setAmount(new BigDecimal("1000.00"));
        PaymentTransaction creditResult = paymentClient.postPaymentCredit(trx).get();
        Assert.assertEquals(creditResult.getStatus().toUpperCase(), PaymentStatus.SETTLED.toString());
        PaymentInstrument getResult3 =  piClient.getById(new PaymentInstrumentId(result.getId())).get();
        Assert.assertTrue(getResult3.getTypeSpecificDetails().getStoredValueBalance().equals(new BigDecimal("1985.00")));
        trx.getChargeInfo().setAmount(new BigDecimal("15.00"));
        trx.setTrackingUuid(generateUUID());
        PaymentTransaction refundResult = paymentClient.refundPayment(new PaymentId(paymentResult.getId()), trx).get();
        Assert.assertEquals(refundResult.getStatus().toUpperCase(), PaymentStatus.REFUNDED.toString());
        PaymentInstrument getResult4 =  piClient.getById(new PaymentInstrumentId(result.getId())).get();
        Assert.assertTrue(getResult4.getTypeSpecificDetails().getStoredValueBalance().equals(new BigDecimal("2000.00")));
    }

    @Test(enabled = false)
    public void addPIAndAuthReverse() throws ExecutionException, InterruptedException {
        PaymentInstrument pi = getPaymentInstrument();
        final UserId userId = new UserId(pi.getUserId());
        final PaymentInstrument result = piClient.postPaymentInstrument(pi).get();
        Assert.assertNotNull(result.getId());
        PaymentTransaction trx = new PaymentTransaction(){
            {
                setTrackingUuid(generateUUID());
                setUserId(userId.getValue());
                setBillingRefId(BILLING_REF_ID);
                setPaymentInstrumentId(result.getId());
                setChargeInfo(new ChargeInfo(){
                    {
                        setCurrency("USD");
                        setAmount(new BigDecimal(14.00));
                    }
                });
            }
        };
        PaymentTransaction paymentResult = paymentClient.postPaymentAuthorization(trx).get();
        Assert.assertEquals(paymentResult.getStatus().toUpperCase(), PaymentStatus.AUTHORIZED.toString());
        PaymentTransaction reverseTrx = new PaymentTransaction(){
            {
                setTrackingUuid(generateUUID());
                setBillingRefId(BILLING_REF_ID);
                setUserId(userId.getValue());
            }
        };
        PaymentTransaction captureResult = paymentClient.reversePayment(new PaymentId(paymentResult.getId()), reverseTrx).get();
        Assert.assertEquals(captureResult.getStatus().toUpperCase(), PaymentStatus.REVERSED.toString());
    }

    @Test(enabled = false)
    public void addPIAndChargeReverse() throws ExecutionException, InterruptedException {
        PaymentInstrument pi = getPaymentInstrument();
        final UserId userId = new UserId(pi.getUserId());
        final PaymentInstrument result = piClient.postPaymentInstrument(pi).get();
        Assert.assertNotNull(result.getId());
        PaymentTransaction trx = new PaymentTransaction(){
            {
                setTrackingUuid(generateUUID());
                setUserId(userId.getValue());
                setPaymentInstrumentId(result.getId());
                setBillingRefId(BILLING_REF_ID);
                setChargeInfo(new ChargeInfo(){
                    {
                        setCurrency("USD");
                        setAmount(new BigDecimal(15.00));
                    }
                });
            }
        };
        PaymentTransaction paymentResult = paymentClient.postPaymentCharge(trx).get();
        Assert.assertEquals(paymentResult.getStatus().toUpperCase(), PaymentStatus.SETTLEMENT_SUBMITTED.toString());
        PaymentTransaction reverseTrx = new PaymentTransaction(){
            {
                setTrackingUuid(generateUUID());
                setBillingRefId(BILLING_REF_ID);
                setUserId(userId.getValue());
            }
        };
        PaymentTransaction captureResult = paymentClient.reversePayment(new PaymentId(paymentResult.getId()), reverseTrx).get();
        Assert.assertEquals(captureResult.getStatus().toUpperCase(), PaymentStatus.REVERSED.toString());
    }

    @Test(enabled = false)
    public void addPIAndAuthFailed() throws ExecutionException, InterruptedException {
        PaymentInstrument pi = getPaymentInstrument();
        final UserId userId = new UserId(pi.getUserId());
        final PaymentInstrument result = piClient.postPaymentInstrument(pi).get();
        Assert.assertNotNull(result.getId());
        PaymentTransaction trx = new PaymentTransaction(){
            {
                setTrackingUuid(generateUUID());
                setUserId(userId.getValue());
                setBillingRefId(BILLING_REF_ID);
                setPaymentInstrumentId(result.getId());
                setChargeInfo(new ChargeInfo(){
                    {
                        setCurrency("abc");
                        setAmount(new BigDecimal(16.00));
                    }
                });
            }
        };
        PaymentTransaction paymentResult = null;
        try{
            paymentResult = paymentClient.postPaymentAuthorization(trx).get();
        }catch(Exception ex){

        }

    }

    @Test(enabled = false)
    public void addPIAndAuthCaptureFailed() throws Exception {
        PaymentInstrument pi = getPaymentInstrument();
        final UserId userId = new UserId(pi.getUserId());
        final PaymentInstrument result = piClient.postPaymentInstrument(pi).get();
        Assert.assertNotNull(result.getId());
        PaymentTransaction trx = new PaymentTransaction(){
            {
                setTrackingUuid(generateUUID());
                setUserId(userId.getValue());
                setPaymentInstrumentId(result.getId());
                setBillingRefId(BILLING_REF_ID);
                setChargeInfo(new ChargeInfo(){
                    {
                        setCurrency("USD");
                        setAmount(new BigDecimal(17.00));
                    }
                });
            }
        };
        PaymentTransaction paymentResult = paymentClient.postPaymentAuthorization(trx).get();
        Assert.assertEquals(paymentResult.getStatus().toUpperCase(), PaymentStatus.AUTHORIZED.toString());
        PaymentTransaction captureTrx = new PaymentTransaction(){
            {
                setTrackingUuid(generateUUID());
                setUserId(userId.getValue());
                setBillingRefId(BILLING_REF_ID);
                setChargeInfo(new ChargeInfo(){
                    {
                        setCurrency("USD");
                        setAmount(new BigDecimal(10.00));
                    }
                });
            }
        };
        PaymentTransaction captureResult = null;
        try{
            captureResult = paymentClient.postPaymentCapture(new PaymentId(paymentResult.getId()), captureTrx).get();
        }catch (Exception ex){
            Assert.assertNull(captureResult);
            PaymentTransaction revertResult = paymentClient.getPayment(new PaymentId(paymentResult.getId())).get();
            Assert.assertEquals(revertResult.getStatus().toUpperCase(), PaymentStatus.SETTLEMENT_SUBMIT_DECLINED.toString());
            for(PaymentEvent event : revertResult.getPaymentEvents()){
                if(event.getType().equalsIgnoreCase(PaymentEventType.SUBMIT_SETTLE.toString())
                        && event.getStatus().equalsIgnoreCase(PaymentStatus.SETTLEMENT_SUBMIT_DECLINED.toString())){
                    return;
                }
            }
            throw ex;
        }
    }

    @Test(enabled = false)
    public void addPIAndAuthReverseFailed() throws Exception {
        PaymentInstrument pi = getPaymentInstrument();
        final UserId userId = new UserId(pi.getUserId());
        final PaymentInstrument result = piClient.postPaymentInstrument(pi).get();
        Assert.assertNotNull(result.getId());
        PaymentTransaction trx = new PaymentTransaction(){
            {
                setTrackingUuid(generateUUID());
                setUserId(userId.getValue());
                setBillingRefId(BILLING_REF_ID);
                setPaymentInstrumentId(result.getId());
                setChargeInfo(new ChargeInfo(){
                    {
                        setCurrency("USD");
                        setAmount(new BigDecimal(18.00));
                    }
                });
            }
        };
        PaymentTransaction paymentResult = paymentClient.postPaymentAuthorization(trx).get();
        Assert.assertEquals(paymentResult.getStatus().toUpperCase(), PaymentStatus.AUTHORIZED.toString());
        PaymentTransaction reverseTrx = new PaymentTransaction(){
            {
                setTrackingUuid(generateUUID());
                setBillingRefId(BILLING_REF_ID);
                setUserId(userId.getValue());
            }
        };
        PaymentTransaction reverseResult = null;
        try{
            reverseResult = paymentClient.reversePayment(new PaymentId(paymentResult.getId()), reverseTrx).get();
        }catch (Exception ex){
            Assert.assertNull(reverseResult);
            PaymentTransaction getResult = paymentClient.getPayment(new PaymentId(paymentResult.getId())).get();
            Assert.assertEquals(getResult.getStatus().toUpperCase(), PaymentStatus.REVERSE_DECLINED.toString());
            for(PaymentEvent event : getResult.getPaymentEvents()){
                if(event.getType().equalsIgnoreCase(PaymentEventType.AUTH_REVERSE.toString())
                        && event.getStatus().equalsIgnoreCase(PaymentStatus.REVERSE_DECLINED.toString())){
                    return;
                }
            }
            throw ex;
        }
        throw new RuntimeException("Expect exception");
    }
}
