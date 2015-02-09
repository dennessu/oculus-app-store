package com.junbo.order.jobs.transaction

import com.Ostermiller.util.CSVParser
import com.junbo.common.id.OrderId
import com.junbo.common.util.IdFormatter
import com.junbo.configuration.topo.DataCenters
import com.junbo.order.jobs.transaction.model.DiscrepancyRecord
import com.junbo.order.jobs.transaction.model.FacebookTransaction
import com.junbo.order.jobs.Constants
import com.junbo.order.jobs.transaction.model.TransactionProcessResult
import com.junbo.order.jobs.utils.csv.CSVWriter
import com.junbo.order.jobs.utils.csv.ConcurrentCSVWriter
import com.junbo.order.jobs.utils.ftp.FTPUtils
import com.junbo.order.spec.model.fb.TransactionType
import com.junbo.sharding.ShardAlgorithm
import groovy.transform.CompileStatic
import org.apache.commons.beanutils.BeanUtils
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

/**
 * Created by fzhang on 2015/1/29.
 */
@CompileStatic
@Component('order.transactionReceiveJob')
class TransactionReceiveJob {

    private static final int MAX_FUTURE_SIZE = 10000

    private static final Logger LOGGER = LoggerFactory.getLogger(TransactionReceiveJob)

    @Value('${order.jobs.discrepancy.localDir}')
    private String discrepancyDir

    @Value('${order.jobs.discrepancy.remoteDir}')
    private String discrepancyRemoteDir

    @Value('${order.jobs.transaction.localDir}')
    private String transactionLocalDir

    @Value('${order.jobs.transaction.remoteDir}')
    private String transactionRemoteDir

    @Resource(name = 'subledgerPayoutFTPUtils')
    private FTPUtils ftpUtils

    @Resource(name = 'orderJobAsyncTaskExecutor')
    private ThreadPoolTaskExecutor threadPoolTaskExecutor

    @Resource(name = 'userShardAlgorithm')
    private ShardAlgorithm shardAlgorithm;

    @Resource(name = 'orderDiscrepancyProcessor')
    private DiscrepancyProcessor discrepancyProcessor

    private List<String> discrepancyReportColumns;

    @Value('${order.jobs.discrepancy.columns}')
    public void setDiscrepancyReportColumns(String text) {
        discrepancyReportColumns = []
        for (String columnName : text.split(',')) {
            discrepancyReportColumns << columnName.trim()
        }
    }

    public void execute() {
        innerExecute(new Date(System.currentTimeMillis() - Constants.MS_A_DAY))
    }

    public void execute(String dateString) {
        innerExecute(new SimpleDateFormat('yyyy-MM-dd').parse(dateString))
    }

    private class JobResult {
        int totalProcessed = 0
        int totalError = 0
        List<DiscrepancyRecord> discrepancyRecords = []
    }

    private synchronized void innerExecute(Date date) {
        long startTime = System.currentTimeMillis()
        MDC.put(Constants.X_REQUEST_ID, UUID.randomUUID().toString())
        LOGGER.info('name=Start_Process_TransactionReceive')

        File transactionFile = getTransactionFile(date)
        if (transactionFile.exists()) {
            LOGGER.info('name=TransactionReceive_Already_Done')
            return
        }
        String remote = "${transactionRemoteDir}/${transactionFile.name}"
        if (!ftpUtils.downloadFile(transactionFile, remote)) {
            LOGGER.info('name=Transaction_Remote_File_Not_Available, remotePath={}', remote)
            return
        }

        FileInputStream is = new FileInputStream(transactionFile)
        JobResult jobResult = new JobResult()
        int index = 0
        try {
            CSVParser csvParser = new CSVParser(is)
            final String[] headersLowerCase = csvParser.getLine()
            for (int i = 0; i < headersLowerCase.length; ++i) {
                headersLowerCase[i] = headersLowerCase[i].toLowerCase()
            }
            List<Future<TransactionProcessResult>> futureList = []

            while (true) {
                final String[] values = csvParser.getLine()
                if (values == null) {
                    break
                }

                def self = this
                final currentIndex = index++
                Future<TransactionProcessResult> future = threadPoolTaskExecutor.submit(new Callable<TransactionProcessResult>() {
                    @Override
                    TransactionProcessResult call() throws Exception {
                        MDC.put(Constants.X_REQUEST_ID, UUID.randomUUID().toString());
                        try {
                            return self.processTransaction(headersLowerCase, values)
                        } catch (Exception ex) {
                            LOGGER.error('name=ErrorProcessDiscrepancy, file={}, index={}, values={}', transactionFile.path, currentIndex, StringUtils.join(values, ','), ex)
                            return new TransactionProcessResult(error: ex)
                        }
                    }
                })

                futureList.add(future)
                if (futureList.size() > MAX_FUTURE_SIZE) {
                    updateJobResult(futureList, jobResult)
                    futureList.clear()
                }
            }
            updateJobResult(futureList, jobResult)
            futureList.clear()

            if (jobResult.discrepancyRecords.size() > 0) {
                File discrepancyFile = getFile(date)
                File workFile = new File(discrepancyFile.path + '.tmp')
                writeDiscrepancyFile(workFile, jobResult.discrepancyRecords)
                FileUtils.moveFile(workFile, discrepancyFile)
                ftpUtils.uploadFile(discrepancyFile, "${discrepancyRemoteDir}/${discrepancyFile.name}")
            }
        } finally {
            is.close()
            LOGGER.info('name=End_Process_Discrepancy,totalProcessed={},totalInFile={},numOfError={},numOfDiscrepancy={},latencyInMs={}',jobResult.totalProcessed, index,
                    jobResult.discrepancyRecords.size(), jobResult.totalError, System.currentTimeMillis() - startTime)
        }
    }

    private File getFile(Date date) {
        String fileName = "${Constants.DISCREPANCY_FILE_NAME}-${new SimpleDateFormat('yyyy-MM-dd').format(date)}-dc${DataCenters.instance().currentDataCenterId()}.${Constants.CSV_EXTENSION}"
        return new File(new File(discrepancyDir), fileName)
    }

    private File getTransactionFile(Date date) {
        String fileName = "${Constants.TRANSACTION_FILE_NAME}-${new SimpleDateFormat('yyyy-MM-dd').format(date)}.${Constants.CSV_EXTENSION}"
        return new File(new File(transactionLocalDir), fileName)
    }

    private void writeDiscrepancyFile(File file, List<DiscrepancyRecord> discrepancyRecords) {
        FileUtils.openOutputStream(file).close() // create an empty file

        CSVWriter csvWriter = new ConcurrentCSVWriter(file, null)
        csvWriter.writeRecords(Arrays.asList(discrepancyReportColumns))
        discrepancyRecords.each { DiscrepancyRecord discrepancyRecord ->
            List<String> values = toCsvColumns(discrepancyReportColumns, discrepancyRecord)
            csvWriter.writeRecords(Arrays.asList(values))
        }
        csvWriter.close()
    }

    private TransactionProcessResult processTransaction(String[] headersLowerCase, String[] row) {
        Map<String, String> fields = [:]
        for (int i = 0; i < headersLowerCase.size();++i) {
            fields[headersLowerCase[i]] = row[i]
        }

        FacebookTransaction facebookTransaction = new FacebookTransaction(
                txnType: TransactionType.valueOf(fields['txn_type']),
                providerTxnId: fields['provider_txn_id'],
                paymentId: fields['payment_id'],
                senderAmount: new BigDecimal(fields['sender_amount']),
                usdAmount: new BigDecimal(fields['usd_amount']),
                currency: fields['currency']
        )

        OrderId orderId = getOrderId(facebookTransaction)
        if (DataCenters.instance().currentDataCenterId() == shardAlgorithm.dataCenterId(orderId.value)) { // only handle transactions on current dc
            TransactionProcessResult transactionProcessResult = new TransactionProcessResult()
            transactionProcessResult.discrepancyRecord = discrepancyProcessor.process(orderId, facebookTransaction)
            transactionProcessResult.processed = true
            return transactionProcessResult
        }

        return new TransactionProcessResult(processed: false)
    }

    private void updateJobResult(List<Future<TransactionProcessResult>> futureList, JobResult jobResult) {
        futureList.each { Future<TransactionProcessResult> future ->
            TransactionProcessResult result = future.get()
            if (result != null && result.processed) {
                jobResult.totalProcessed++
                if (result.discrepancyRecord != null) {
                    jobResult.discrepancyRecords << result.discrepancyRecord
                }
            }
            if (result.error != null) {
                jobResult.totalError++
            }
        }
    }

    private OrderId getOrderId(FacebookTransaction transaction) {
        if (org.apache.commons.lang3.StringUtils.isEmpty(transaction.providerTxnId)) {
            throw new IllegalArgumentException('providerTxnId is empty')
        }
        int index = transaction.providerTxnId.indexOf('-')
        if (index < 0) {
            throw new IllegalArgumentException('Invalid providerTxnId')
        }

        return new OrderId(IdFormatter.decodeId(OrderId, transaction.providerTxnId.substring(index + 1).trim()))
    }

    private List<String> toCsvColumns(List<String> fields, DiscrepancyRecord record) {
        List<String> values = []
        fields.each { String fieldPath ->
            values << BeanUtils.getProperty(record, fieldPath)
        }
        return values
    }

}
