package com.junbo.payment.clientproxy.facebook;

/**
 * Created by wenzhu on 2/4/15.
 */
public enum FacebookFraudStatus {
    succeeded,
    blocked,
    pending,
    released_after_pend,
    blocked_after_pend
}
