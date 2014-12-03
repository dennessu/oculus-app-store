package com.junbo.payment.clientproxy.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.junbo.langur.core.promise.Promise;
import com.junbo.payment.clientproxy.FacebookGatewayService;
import com.junbo.payment.clientproxy.facebook.*;
import com.junbo.payment.common.CommonUtil;
import com.junbo.payment.common.exception.AppServerExceptions;

import java.io.IOException;

/**
 * Created by wenzhu on 12/2/14.
 */
public class FacebookGatewayServiceImpl implements FacebookGatewayService {
    private FacebookOauthApi facebookOauthApi;
    private FacebookPaymentApi facebookPaymentApi;
    private FacebookCreditCardTokenApi facebookCCTokenApi;
    private FacebookBatchApi facebookBatchApi;
    public Promise<String> getAccessToken(FacebookTokenRequest fbTokenRequest) {
        return facebookOauthApi.getAccessToken(fbTokenRequest).then(new Promise.Func<String, Promise<String>>() {
            @Override
            public Promise<String> apply(String s) {
                if(!CommonUtil.isNullOrEmpty(s)){
                    return Promise.pure(s.replace("access_token=", ""));
                }
                return Promise.pure(s);
            }
        });
    }

    public Promise<String> getCCToken(FacebookCreditCardTokenRequest request) {
        return facebookCCTokenApi.getCCToken(request).then(new Promise.Func<String, Promise<String>>() {
            @Override
            public Promise<String> apply(String s) {
                if(!CommonUtil.isNullOrEmpty(s)){
                    return Promise.pure(s.split(":")[1].replace("\"", "").replace("}", "").trim());
                }
                return Promise.pure(s);
            }
        });
    }
    
    public Promise<FacebookPaymentAccount> createAccount(String accessToken, String oculusAppId, FacebookPaymentAccount fbPaymentAccount) {
        return facebookPaymentApi.createAccount(accessToken, oculusAppId, fbPaymentAccount);
    }

    public Promise<FacebookCreditCard> addCreditCard(String accessToken, String paymentAccountId, FacebookCreditCard fbCreditCard) {
        return facebookPaymentApi.addCreditCard(accessToken, paymentAccountId, fbCreditCard);
    }

    public Promise<FacebookCreditCard> getCreditCard(String accessToken, String creditcardId) {
        return facebookPaymentApi.getCreditCard(accessToken, creditcardId);
    }

    public Promise<FacebookPayment> addPayment(String accessToken, String paymentAccountId, FacebookPayment fbPayment) {
        return facebookPaymentApi.addPayment(accessToken, paymentAccountId, fbPayment);
    }

    public Promise<FacebookPayment> modifyPayment(String accessToken, String paymentId, FacebookPayment fbPayment) {
        return facebookPaymentApi.modifyPayment(accessToken, paymentId, fbPayment);
    }

    @Override
    public Promise<FacebookCreditCard> batchAddAndGetCreditCard(String accessToken, String paymentAccountId, final FacebookCreditCard fbCreditCard) {
        FacebookBatch addCard = new FacebookBatch();
        addCard.setName("addCC");
        addCard.setMethod("POST");
        addCard.setRelativeUrl(paymentAccountId + "/credit_cards");
        addCard.setBody(fbCreditCard.toBatchString());
        FacebookBatch getCard = new FacebookBatch();
        getCard.setMethod("GET");
        getCard.setRelativeUrl("{result=addCC:$.id}");
        getCard.setOmitResponse(false);
        String addCardJson = addCard.toString();
        String getCardJson = getCard.toString();
        return facebookBatchApi.batchInvoke(accessToken, "[" + addCardJson + "," + getCardJson + "]").then(new Promise.Func<String, Promise<FacebookCreditCard>>() {
            @Override
            public Promise<FacebookCreditCard> apply(String s) {
                ObjectMapper mapper = new ObjectMapper();
                FacebookCCBatchResponse[] results = null;
                try {
                    results = mapper.readValue(s, FacebookCCBatchResponse[].class);
                } catch (IOException e) {
                    throw AppServerExceptions.INSTANCE.providerProcessError("Facebook", "deserialize:" + s).exception();
                }
                FacebookCCBatchResponse getResult = null;
                if(results != null && results.length > 1 ){
                    getResult = (results[0] == null ? results[1] : results[0]);
                }
                if(getResult == null){
                    throw AppServerExceptions.INSTANCE.providerProcessError("Facebook", s).exception();
                }
                FacebookCreditCard creditCard = null;
                try {
                    creditCard = mapper.readValue(getResult.getBody(), FacebookCreditCard.class);
                } catch (IOException e) {
                    throw AppServerExceptions.INSTANCE.providerProcessError("Facebook", "deserialize:" + getResult.getBody()).exception();
                }
                return Promise.pure(creditCard);
            }
        });
    }

    private String getFieldValue(String nameValuePair, String fieldName){
        if(nameValuePair == null && fieldName == null){
            return null;
        }
        if(nameValuePair.contains(fieldName)){
            String[] results = nameValuePair.split(":");
            if(results.length > 1){
                return results[results.length - 1].replace("\"", "").replace("\\","");
            }
        }
        return null;
    }

    public static void main(String[] args){

    }

    public void setFacebookOauthApi(FacebookOauthApi facebookOauthApi) {
        this.facebookOauthApi = facebookOauthApi;
    }

    public void setFacebookPaymentApi(FacebookPaymentApi facebookPaymentApi) {
        this.facebookPaymentApi = facebookPaymentApi;
    }

    public void setFacebookCCTokenApi(FacebookCreditCardTokenApi facebookCCTokenApi) {
        this.facebookCCTokenApi = facebookCCTokenApi;
    }

    public void setFacebookBatchApi(FacebookBatchApi facebookBatchApi) {
        this.facebookBatchApi = facebookBatchApi;
    }
}
