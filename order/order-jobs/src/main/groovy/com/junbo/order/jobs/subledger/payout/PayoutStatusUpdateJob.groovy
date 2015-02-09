package com.junbo.order.jobs.subledger.payout
import com.Ostermiller.util.CSVParser
import com.junbo.common.error.AppErrorException
import com.junbo.common.id.PayoutId
import com.junbo.common.util.IdFormatter
import com.junbo.order.jobs.Constants
import com.junbo.order.jobs.utils.ftp.FTPUtils
import com.junbo.order.spec.model.FBPayoutStatusChangeRequest
import com.junbo.order.spec.resource.SubledgerResource
import groovy.transform.CompileStatic
import org.apache.commons.io.FileUtils
import org.apache.commons.lang.StringUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import org.springframework.stereotype.Component

import javax.annotation.Resource
import java.text.SimpleDateFormat
import java.util.concurrent.Callable
import java.util.concurrent.Future
import java.util.concurrent.atomic.AtomicInteger
/**
 * Created by fzhang on 2015/1/18.
 */
@CompileStatic
@Component('order.payoutUpdateJob')
class PayoutStatusUpdateJob {

    private Logger LOGGER = LoggerFactory.getLogger(PayoutStatusUpdateJob)

    public static final String X_REQUEST_ID = "oculus-request-id";

    private static final int MAX_FUTURE_NUM = 100000

    private static final String COL_PAYOUT_ID = 'payout_id'
    private static final String COL_FINANCIAL_ID = 'financial_id'
    private static final String COL_START_DATE = 'start_date'
    private static final String COL_END_DATE = 'end_date'
    private static final String COL_PAYOUT_CURRENCY = 'payout_currency'
    private static final String COL_PAYOUT_AMOUNT = 'payout_amount'
    private static final String COL_TAX_AMOUNT = 'tax_amount'
    private static final String COL_EXTERNAL_ID = 'external_id'
    private static final String COL_STATUS = 'status'
    private static final String COL_REASON = 'reason'
    private static final List<String> COL_ALL = [
        COL_PAYOUT_ID, COL_FINANCIAL_ID, COL_START_DATE, COL_END_DATE, COL_PAYOUT_CURRENCY, COL_PAYOUT_AMOUNT,
        COL_TAX_AMOUNT, COL_TAX_AMOUNT, COL_EXTERNAL_ID, COL_EXTERNAL_ID, COL_STATUS, COL_REASON
    ]

    @Value('${order.jobs.subledger.localDir.payoutStatus.history}')
    private String historyDir

    @Value('${order.jobs.subledger.localDir.payoutStatus}')
    private String localDir

    @Value('${order.jobs.subledger.remoteDir.payoutStatus}')
    private String remoteDir

    @Value('${order.jobs.subledger.maxRetry}')
    private int maxRetry

    private int initialRetryIntervalSecond = 10

    @Resource(name = 'payoutUpdateJobAsyncTaskExecutor')
    private ThreadPoolTaskExecutor threadPoolTaskExecutor

    @Resource(name='order.subledgerClient')
    private SubledgerResource subledgerResource

    @Resource(name='subledgerPayoutFTPUtils')
    FTPUtils ftpUtils

    public void execute() {
        innerExecute(new Date(System.currentTimeMillis() ))
    }

    public void execute(String dateString) {
        innerExecute(new SimpleDateFormat('yyyy-MM-dd').parse(dateString))
    }

    private synchronized void innerExecute(Date input) {
        LOGGER.info('name=startPayoutStatusUpdateJob')
        long start = System.currentTimeMillis()
        try
        {
            Date date = new Date(input.year, input.month, input.date)

            // generate payout
            File payoutStatusFile = getPayoutStatusFile(date)
            File payoutHistoryFile = getHistoryFile(payoutStatusFile)
            if (payoutHistoryFile.exists()) {
                LOGGER.info("name=PayoutStatusAlreadyUpdated, file={}", payoutStatusFile.name)
                return
            }

            if (!payoutStatusFile.exists()) { // download if not exists in local
                boolean downloaded = ftpUtils.downloadFile(payoutStatusFile, "${remoteDir}/${payoutStatusFile.name}", maxRetry)
                if (!downloaded) {
                    return
                }
            }

            processPayoutStatusFile(payoutStatusFile)
            FileUtils.moveFile(payoutStatusFile, payoutHistoryFile)
        } finally {
            LOGGER.info('name=endPayoutStatusUpdateJob, time={}s', (System.currentTimeMillis() - start) / 1000)
        }
    }

    private void processPayoutStatusFile(File payoutStatusFile) {
        LOGGER.info('name=StartProcessPayoutStatusFile, file={}', payoutStatusFile.path)
        long start = System.currentTimeMillis()

        FileInputStream is = new FileInputStream(payoutStatusFile)
        try {
            CSVParser csvParser = new CSVParser(is)
            final String[] headersLowerCase = csvParser.getLine()
            for (int i = 0; i < headersLowerCase.length; ++i) {
                headersLowerCase[i] = headersLowerCase[i].toLowerCase()
            }
            List<Future<Void>> taskList = [] as List
            try {
                COL_ALL.each { String colName ->
                    validateHeader(headersLowerCase, colName)
                }
            } catch (IllegalArgumentException ex) {
                LOGGER.error('name=PayoutStatusUpdateError, file={}', payoutStatusFile.path, ex)
                return
            }

            int index = 0
            AtomicInteger totalProcessed = new AtomicInteger()
            AtomicInteger numOfError = new AtomicInteger()
            while (true) {
                final String[] values = csvParser.getLine()
                if (values == null) {
                    break
                }

                def self = this
                Future<Void> future = threadPoolTaskExecutor.submit(new Callable<Void>() {
                    @Override
                    Void call() throws Exception {
                        MDC.put(X_REQUEST_ID, UUID.randomUUID().toString());
                        try {
                            totalProcessed.incrementAndGet()
                            self.processPayoutStatus(headersLowerCase, values)
                        } catch (Exception ex) {
                            numOfError.incrementAndGet()
                            LOGGER.error('name=PayoutStatusUpdateError, file={}, index={}, values={}', payoutStatusFile.path, index, StringUtils.join(values, ','), ex)
                        }
                    }
                })
                taskList.add(future)

                if (taskList.size() > MAX_FUTURE_NUM) {
                    taskList.each { Future f -> f.get()}
                    taskList.clear()
                }
                index++
            }

            taskList.each { Future f -> f.get()}
            LOGGER.info('name=EndProcessPayoutStatusFile, file={}, totalProcessed={}, numOfError={}, latencyInMs={}', payoutStatusFile.path,
                    totalProcessed.toString(), numOfError.toString(), System.currentTimeMillis() - start)
        } finally {
            is.close()
        }

    }

    private void processPayoutStatus(String[] headersLowerCase, String[] values) {
        Map<String, String> valuesMap = [:] as Map<String, String>
        if (values.length != headersLowerCase.length) {
            throw new RuntimeException('Length of header and data not match, headerLength=' + headersLowerCase.length + ", valuesLength=" + values.length)
        }
        for (int i = 0;i < headersLowerCase.length; ++i) {
            valuesMap[headersLowerCase[i].toLowerCase()] = values[i].trim()
        }

        List<PayoutId> payoutIds = [] as List
        for (String payoutIdString: valuesMap[COL_EXTERNAL_ID].split(';')) {
            if (!payoutIdString.trim().isEmpty()) {
                payoutIds << new PayoutId(IdFormatter.decodeId(PayoutId, payoutIdString.trim()))
            }
        }
        if (payoutIds.isEmpty()) {
            throw new RuntimeException('Invalid externalId value.')
        }

        boolean hasError = false
        payoutIds.each { PayoutId payoutId ->
            FBPayoutStatusChangeRequest request = new FBPayoutStatusChangeRequest(
                    payoutId: payoutId,
                    fbPayoutId: valuesMap[COL_PAYOUT_ID],
                    financialId: valuesMap[COL_FINANCIAL_ID],
                    startDate: valuesMap[COL_START_DATE],
                    endDate: valuesMap[COL_END_DATE],
                    payoutCurrency: valuesMap[COL_PAYOUT_CURRENCY],
                    payoutAmount: new BigDecimal(valuesMap[COL_PAYOUT_AMOUNT]),
                    status: valuesMap[COL_STATUS],
                    reason: valuesMap[COL_REASON]
            )

            int retryCount = 0;
            while (true) {
                try {
                    subledgerResource.updateStatusOnFacebookPayoutStatusChange(request).get()
                    break
                } catch (Exception ex) {
                    LOGGER.error('name=PayoutStatusUpdateApiError,payoutId={},fbPayoutId={},retryCount={}', IdFormatter.encodeId(payoutId), request.fbPayoutId, retryCount, ex)
                    boolean retryable = (ex instanceof AppErrorException && (((AppErrorException) ex).error.httpStatusCode / 100 == 5))
                    if (retryable && retryCount < maxRetry) {
                        Thread.sleep(1000L * initialRetryIntervalSecond * (1 << retryCount))
                        retryCount++
                    } else {
                        LOGGER.error('name=PayoutStatusUpdateApiErrorNotRetryAble,payoutId={},fbPayoutId={}', IdFormatter.encodeId(payoutId), request.fbPayoutId, ex)
                        hasError = true
                        break
                    }
                }
            }
        }

        if (hasError) {
            throw new RuntimeException("Error_In_PayoutStatus_Update,fbPayoutId=" + valuesMap[COL_PAYOUT_ID])
        }
    }

    private File getPayoutStatusFile(Date date) {
        String fileName = "${Constants.PAYOUT_STATUS_FILE_NAME}-${new SimpleDateFormat('yyyy-MM-dd').format(date)}.${Constants.CSV_EXTENSION}"
        return new File(new File(localDir), fileName)
    }

    private File getHistoryFile(File file) {
        return new File(new File(historyDir), file.getName())
    }

    private void validateHeader(String[] headersLowerCase, String name) {
        if (Arrays.asList(headersLowerCase).indexOf(name.toLowerCase()) < 0) {
            throw new IllegalArgumentException("column ${name} is missing.")
        }
    }

}
