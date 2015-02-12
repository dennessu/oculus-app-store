package com.junbo.payment.clientproxy.facebook;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by wenzhu on 2/12/15.
 */
//Server error is commented here
public enum FacebookClientErrorCode {
    PAYMENTS_INVALID_CARD_NUMBER(5204),
    PAYMENTS_INVALID_EXP_DATE(5203),
    PAYMENTS_INVALID_CARD_TYPE(5201),
    PAYMENTS_INVALID_ADDRESS(5211),
    PAYMENTS_INVALID_ZIP(5215),
    PAYMENTS_INVALID_CSC(5202),
    PAYMENTS_BLACKLISTED_CARD(5217),
    PAYMENTS_FRAUD_RULE_TRIGGERED(5218),
    PAYMENTS_STOLEN_CARD(5219),
    PAYMENTS_INACTIVE_CARD(5220),
    PAYMENTS_AUTH_GENERAL_DECLINE(5216),
    //PAYMENTS_UNKNOWN(1150),
    PAYMENTS_INVALID_AMOUNT(1179),
    //DATA_DATABASE_ERROR(805),
    PAYMENTS_PAYMENT_DECLINED(5212),
    PAYMENTS_INSUFFICIENT_FUNDS(5221),
    PAYMENTS_REACHED_CREDIT_LIMIT(5222),
    PAYMENTS_REFERRAL_REQUIRED(5223),
    //PAYMENTS_SYSTEM_OR_PROVIDER_ERROR(5224),
    PAYMENTS_INVALID_PARAM(1157),
    PAYMENTS_REFUND_OLDER_THAN_SIX_MONTHS(5214),
    PAYMENTS_NO_OUTSTANDING_NET_PROVIDER_AMOUNT(1196),
    //PAYMENTS_DATABASE(1152),
    PARAM(100),
    //PAYMENTS_NETWORK_ISSUE(1195),
    PAYMENTS_ACCOUNT_GET_FAILURE(1199),
    PAYMENTS_ENTITY_PRIVACY_ERROR(5213);
    //UNKNOWN(1);

    private final Integer id;
    FacebookClientErrorCode(Integer id){
        this.id = id;
    }
    public Integer getId() {
        return id;
    }

    // Lookup table
    private static final Map LOOKUP = new HashMap<Integer, FacebookClientErrorCode>();

    // Populate the lookup table on loading time
    static {
        for (FacebookClientErrorCode s : EnumSet.allOf(FacebookClientErrorCode.class))
            LOOKUP.put(s.getId(), s);
    }

    // This method can be used for reverse lookup purpose
    public static FacebookClientErrorCode get(Integer id) {
        return (FacebookClientErrorCode)LOOKUP.get(id);
    }
}
