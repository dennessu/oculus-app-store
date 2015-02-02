package com.junbo.order.jobs.subledger.payout

import groovy.transform.CompileStatic

/**
 * The Constants class.
 */
@CompileStatic
class Constants {

    public static final String X_REQUEST_ID = "oculus-request-id";

    public static final String SIGNATURE_EXTENSION = 'signature'

    public static final String CSV_EXTENSION = 'csv'

    public static final String PAYOUT_FILE_NAME = 'PayoutFile'

    public static final String PAYOUT_STATUS_FILE_NAME = 'PayoutStatus'

    public static final String REVENUE_FILE_NAME = 'MonthlyRevenue'

    public static final String TRANSACTION_FILE_NAME = 'OculusTransaction'

    public static final String DISCREPANCY_FILE_NAME = 'OrderDiscrepancy'

    public static final long MS_A_DAY = 1000L * 3600 * 24

    public static final long MS_A_SECOND = 1000L
}
