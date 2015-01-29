package com.junbo.order.jobs.subledger.payout

import com.junbo.order.spec.model.Subledger
import com.junbo.order.spec.model.SubledgerKeyInfo
import groovy.transform.CompileStatic

/**
 * Created by acer on 2015/1/26.
 */
@CompileStatic
class Utils {

    public static String getFbPayoutOrgId(Subledger subledger) {
        SubledgerKeyInfo subledgerKeyInfo = SubledgerKeyInfo.fromProperties(subledger.getProperties())
        return subledgerKeyInfo?.fbPayoutOrgId
    }

}
