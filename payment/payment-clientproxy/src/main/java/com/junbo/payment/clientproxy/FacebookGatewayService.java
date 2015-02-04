package com.junbo.payment.clientproxy;

import com.junbo.langur.core.promise.Promise;
import com.junbo.payment.clientproxy.facebook.*;

/**
 * Created by wenzhu on 12/2/14.
 */
public interface FacebookGatewayService{
    Promise<String> getAccessToken(FacebookTokenRequest fbTokenRequest);

    Promise<String> getCCToken(FacebookCreditCardTokenRequest request);

    Promise<FacebookPaymentAccount> createAccount(String accessToken, String oculusAppId, FacebookPaymentAccount fbPaymentAccount);

    Promise<FacebookCreditCard> addCreditCard(String accessToken, String paymentAccountId, FacebookCreditCard fbCreditCard);

    Promise<FacebookCreditCard> getCreditCard(String accessToken, String creditcardId);

    Promise<FacebookPayment> addPayment(String accessToken, String paymentAccountId, FacebookPayment fbPayment);

    Promise<FacebookPayment> getPayment(String accessToken, String paymentId);

    Promise<FacebookPayment> modifyPayment(String accessToken, String paymentId, FacebookPayment fbPayment);

    Promise<FacebookCreditCard> batchAddAndGetCreditCard(String accessToken, String paymentAccountId, FacebookCreditCard fbCreditCard);

    Promise<FacebookRiskPayment> getPaymentRisk(String accessToken, String paymentId);

    Promise<FacebookPayment> batchAddAndGetPayment(String accessToken, String paymentAccountId, FacebookPayment fbPayment);
}
