/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.payment.core.provider.adyen;

import com.adyen.services.payment.PaymentPortType;
import com.adyen.services.recurring.RecurringPortType;
import com.junbo.payment.clientproxy.CountryServiceFacade;
import com.junbo.payment.clientproxy.CurrencyServiceFacade;
import com.junbo.payment.clientproxy.PersonalInfoFacade;
import com.junbo.payment.core.provider.AbstractPaymentProviderService;
import com.junbo.payment.db.repo.facade.PaymentInstrumentRepositoryFacade;
import com.junbo.payment.db.repo.facade.PaymentRepositoryFacade;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Abstract Adyen ProviderService Impl.
 */
public abstract class AbstractAdyenProviderServiceImpl extends AbstractPaymentProviderService {
    protected static final String PROVIDER_NAME = "Adyen";
    protected static final String CONFIRMED_STATUS = "AUTHORISED";
    protected static final String RECURRING = "RECURRING";
    protected static final String CANCEL_STATE = "[cancel-received]";
    protected static final String REFUND_STATE = "[refund-received]";
    protected static final String CAPTURE_STATE = "[capture-received]";
    protected static final String AUTH_USER = "javax.xml.rpc.security.auth.username";
    protected static final String AUTH_PWD = "javax.xml.rpc.security.auth.password";
    protected static final String[] NOTIFY_FIELDS = {"eventDate","reason","originalReference","merchantReference",
            "currency","pspReference","merchantAccountCode","eventCode","value","operations","success","live"};
    protected static final int SHIP_DELAY = 1;
    protected static final int VALID_HOURS = 3;

    protected String redirectURL;
    protected String paymentURL;
    protected String recurringURL;
    protected String merchantAccount;
    protected String skinCode;
    protected String skinSecret;
    protected String authUser;
    protected String authPassword;
    protected PaymentPortType service;
    protected RecurringPortType recurService;
    protected String notifyUser;
    protected String notifyPassword;
    @Autowired
    protected PaymentInstrumentRepositoryFacade paymentInstrumentRepositoryFacade;
    @Autowired
    protected PaymentRepositoryFacade paymentRepositoryFacade;
    @Autowired
    protected CountryServiceFacade countryResource;
    @Autowired
    protected CurrencyServiceFacade currencyResource;
    @Autowired
    protected PersonalInfoFacade personalInfoFacade;

    public String getRedirectURL() {
        return redirectURL;
    }

    public void setRedirectURL(String redirectURL) {
        this.redirectURL = redirectURL;
    }

    public String getMerchantAccount() {
        return merchantAccount;
    }

    public void setMerchantAccount(String merchantAccount) {
        this.merchantAccount = merchantAccount;
    }

    public String getSkinCode() {
        return skinCode;
    }

    public void setSkinCode(String skinCode) {
        this.skinCode = skinCode;
    }

    protected String nullToEmpty(String value){
        return value == null ? "" : value;
    }

    public String getSkinSecret() {
        return skinSecret;
    }

    public void setSkinSecret(String skinSecret) {
        this.skinSecret = skinSecret;
    }

    public String getPaymentURL() {
        return paymentURL;
    }

    public void setPaymentURL(String paymentURL) {
        this.paymentURL = paymentURL;
    }

    public String getRecurringURL() {
        return recurringURL;
    }

    public void setRecurringURL(String recurringURL) {
        this.recurringURL = recurringURL;
    }

    public String getAuthUser() {
        return authUser;
    }

    public void setAuthUser(String authUser) {
        this.authUser = authUser;
    }

    public String getAuthPassword() {
        return authPassword;
    }

    public void setAuthPassword(String authPassword) {
        this.authPassword = authPassword;
    }

    public String getNotifyUser() {
        return notifyUser;
    }

    public void setNotifyUser(String notifyUser) {
        this.notifyUser = notifyUser;
    }

    public String getNotifyPassword() {
        return notifyPassword;
    }

    public void setNotifyPassword(String notifyPassword) {
        this.notifyPassword = notifyPassword;
    }
}
