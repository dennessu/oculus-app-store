package com.junbo.order.jobs.subledger.payout

import com.junbo.common.id.PayoutId
import com.junbo.common.util.IdFormatter
import com.junbo.configuration.topo.DataCenters
import com.junbo.configuration.topo.model.DataCenter
import com.junbo.order.clientproxy.FacadeContainer
import com.junbo.order.clientproxy.TransactionHelper
import com.junbo.order.db.repo.facade.SubledgerRepositoryFacade
import com.junbo.order.jobs.utils.csv.ConcurrentCSVWriter
import com.junbo.order.jobs.utils.csv.CSVWriter
import com.junbo.order.jobs.utils.ftp.FTPUtils
import com.junbo.order.jobs.utils.signature.SignatureUtils
import com.junbo.order.spec.model.PageParam
import com.junbo.order.spec.model.Subledger
import com.junbo.order.spec.model.enums.PayoutStatus
import com.junbo.order.spec.model.enums.SubledgerType
import groovy.transform.CompileStatic
import org.apache.commons.io.FileUtils
import org.apache.commons.lang3.StringUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import org.springframework.stereotype.Component
import org.springframework.util.Assert
import org.springframework.util.CollectionUtils

import javax.annotation.Resource
import java.nio.charset.Charset
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.concurrent.Callable
import java.util.concurrent.Future
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock
/**
 * The DailyPayoutExporter class.
 */
@CompileStatic
@Component('order.payoutExportJob')
class PayoutExportJob {

    private static final long MS_A_DAY = 24L * 3600 * 1000

    private Logger LOGGER = LoggerFactory.getLogger(PayoutExportJob)

    private int pageSize = 100

    private String payoutKeyId = '___payout_key'

    private static Lock lock = new ReentrantLock()

    @Resource(name='payoutExportJobAsyncTaskExecutor')
    ThreadPoolTaskExecutor  threadPoolTaskExecutor

    @Resource(name ='subledgerRepositoryFacade')
    SubledgerRepositoryFacade subledgerRepository

    @Resource(name ='order.SubledgerPayoutIdAssignUtils')
    SubledgerPayoutAssignUtils subledgerPayoutIdAssignUtils

    @Resource(name ='orderFacadeContainer')
    FacadeContainer facadeContainer

    @Resource(name ='order.SignatureUtils')
    SignatureUtils signatureUtils

    @Resource(name ='orderTransactionHelper')
    TransactionHelper transactionHelper

    @Value('${order.jobs.subledger.localDir.payout}')
    private String payoutDir

    @Value('${order.jobs.subledger.localDir.payout.history}')
    private String historyDir

    @Value('${order.jobs.subledger.remoteDir.payout}')
    private String remoteDir

    @Resource(name = 'subledgerPayoutFTPUtils')
    private FTPUtils ftpUtils

    @Value('${order.jobs.subledger.maxRetry}')
    private int maxRetry

    private int initialRetryIntervalSecond = 10

    private List<String> extensions = Arrays.asList(Constants.CSV_EXTENSION, Constants.SIGNATURE_EXTENSION)

    private class ExportCallable implements Callable<Void> {

        private final File file
        private final int dcId
        private final int shardId
        private final Date date
        private final PayoutExportJob job

        ExportCallable(File file, int dcId, int shardId, Date date, PayoutExportJob job) {
            this.file = file
            this.dcId = dcId
            this.shardId = shardId
            this.date = date
            this.job = job
        }

        @Override
        Void call() throws Exception {
            job.exportSubledger(dcId, shardId, file, date)
        }
    }

    public void exportAndUploadDaily() {
        exportAndUploadDaily(new Date(System.currentTimeMillis() - MS_A_DAY))
    }

    public void exportAndUploadDaily(String dateString) {
        exportAndUploadDaily(new SimpleDateFormat('yyyy-MM-dd').parse(dateString))
    }

    public void exportAndUploadDaily(Date input) {
        LOGGER.info('name=startPayoutExportJob')
        long start = System.currentTimeMillis()
        try
        {
            Date date = new Date(input.year, input.month, input.date)

            // generate payout
            File payoutFile = getPayoutFile(date)
            File payoutHistoryFile = getHistoryFile(payoutFile)
            if (payoutFile.exists() || payoutHistoryFile.exists()) {
                LOGGER.info("name=PayoutAlreadyGenerated, file={}", payoutFile.name)
            } else {
                generatePayoutFile(date, payoutFile);
            }

            // generate signature
            File signatureFile = getSignatureFile(date)
            File signatureHistoryFile = getHistoryFile(signatureFile)
            if (signatureFile.exists() || signatureHistoryFile.exists()) {
                LOGGER.info("name=PayoutSignatureAlreadyGenerated, file={}", signatureFile.name)
            } else {
                LOGGER.info("name=GeneratePayoutSignature, file={}", payoutFile.path)
                generateSignatureFile(payoutHistoryFile.exists() ? payoutHistoryFile : payoutFile, signatureFile);
            }

            upload() // upload to ftp
        } finally {
            LOGGER.info('name=endPayoutExportJob, time={}s', (System.currentTimeMillis() - start) / 1000)
        }
    }

    private File getPayoutFile(Date date) {
        String fileName = "${Constants.PAYOUT_FILE_NAME}_${new SimpleDateFormat('yyyy_MM_dd').format(date)}.${Constants.CSV_EXTENSION}"
        return new File(new File(payoutDir), fileName)
    }

    private File getSignatureFile(Date date)  {
        String fileName = "${Constants.PAYOUT_FILE_NAME}_${new SimpleDateFormat('yyyy_MM_dd').format(date)}.${Constants.SIGNATURE_EXTENSION}"
        return new File(new File(payoutDir), fileName)
    }

    private void generatePayoutFile(Date date, File file) {
        long start = System.currentTimeMillis()
        LOGGER.info("name=StartGeneratePayoutFile, file={}", file.path)

        try {
            // create work file
            File work = new File(file.getPath() + ".tmp")
            FileUtils.openOutputStream(work, false).close()

            // write headers
            CSVWriter csvWriter = new ConcurrentCSVWriter(work, lock)
            csvWriter.writeRecords(Arrays.asList(Arrays.asList('ds','financial_id','payout_amount','external_id')))
            csvWriter.close()

            List<Future> futures = []
            for (Integer dcId : DataCenters.instance().getDataCenterIds()) {
                DataCenter dataCenter = DataCenters.instance().getDataCenter(dcId)
                for (int shardId = 0; shardId < dataCenter.numberOfShard; ++shardId) {
                    futures << threadPoolTaskExecutor.submit(new ExportCallable(work, dcId, shardId, date, this))
                }
            }
            futures.each { Future future ->
                future.get()
            }

            FileUtils.moveFile(work, file)
        } finally {
            LOGGER.info("name=FinishGeneratePayoutFile, file={}, time={}s", file.path, (System.currentTimeMillis() - start) / 1000)
        }

    }

    private void generateSignatureFile(File payoutFile, File signatureFile) {
        String signature = signatureUtils.generateRSASignatureBase64Encoded(payoutFile, payoutKeyId)
        FileUtils.write(signatureFile, signature, Charset.forName('UTF-8'))
    }

    private void exportSubledger(int dcId, int shardId, File file, Date date) {
        LOGGER.info("name=SubledgerExportStart, file={}, dcId={}, shardId={}", file.path, dcId, shardId)
        int pageStart = 0
        Date endDate = new Date(date.time + MS_A_DAY)
        Date startDate = new Date(date.time)
        DateFormat dateFormat = new SimpleDateFormat('yyyy-MM-dd')

        try {
            CSVWriter csvWriter = new ConcurrentCSVWriter(file, lock)

            subledgerPayoutIdAssignUtils.execute(dcId, shardId, startDate, endDate)

            List<Subledger> subledgersWithSameSeller = [] as ArrayList<Subledger>
            while(true) {
                List<Subledger> subledgers = transactionHelper.executeInNewTransaction {
                    subledgerRepository.getSubledgersOrderBySeller(dcId, shardId, PayoutStatus.PENDING.name(),
                            startDate, endDate, new PageParam(start: pageStart, count: pageSize))
                }
                pageStart += subledgers.size()

                subledgers.each { Subledger subledger ->
                    if (subledgersWithSameSeller.isEmpty()) {
                        subledgersWithSameSeller << subledger
                    } else if (subledgersWithSameSeller[0].seller == subledger.seller) {
                        subledgersWithSameSeller << subledger
                    } else {
                        aggregateAndWrite(subledgersWithSameSeller, csvWriter, date, dateFormat)
                        subledgersWithSameSeller.clear()
                        subledgersWithSameSeller << subledger
                    }
                }

                if (subledgers.size() < pageSize) {
                    aggregateAndWrite(subledgersWithSameSeller, csvWriter, date, dateFormat)
                    csvWriter.close()
                    break
                }
            }
        } finally {
            LOGGER.info("name=SubledgerExportEnd, file={}, dcId={}, shardId={}, numRead={}", file.path, dcId, shardId, pageStart)
        }
    }

    private File getHistoryFile(File file) {
        return new File(new File(historyDir), file.getName())
    }

    private List<PayoutRecord> aggregateToPayoutRecord(List<Subledger> subledgers, Date date){
        Map<PayoutId, PayoutRecord> recordMap = [:] as Map

        subledgers.each { Subledger subledger ->
            String fbPayoutId = Utils.getFbPayoutOrgId(subledger)
            if (StringUtils.isEmpty(fbPayoutId)) {
                return
            }
            if (SubledgerType.valueOf(subledger.subledgerType).payoutActionType != SubledgerType.PayoutActionType.NONE) {
                Assert.notNull(subledger.payoutId)
            }

            PayoutRecord record = recordMap[subledger.payoutId] as PayoutRecord
            if (record == null) {
                record = new PayoutRecord(
                        payoutId: subledger.payoutId,
                        payoutAmount: BigDecimal.ZERO,
                        date: date,
                        fbPayoutId: fbPayoutId
                )
                recordMap[subledger.payoutId] = record
            }

            SubledgerType.PayoutActionType payoutActionType = SubledgerType.valueOf(subledger.subledgerType).payoutActionType
            if (payoutActionType == SubledgerType.PayoutActionType.ADD) {
                record.payoutAmount += subledger.totalPayoutAmount
            } else if (payoutActionType == SubledgerType.PayoutActionType.DEDUCT) {
                record.payoutAmount -= subledger.totalPayoutAmount
            }
        }

        return recordMap.values().asList()
    }

    private void aggregateAndWrite(List<Subledger> subledgersWithSameSeller, CSVWriter csvWriter, Date date, DateFormat dateFormat) {
        if (subledgersWithSameSeller.isEmpty()) {
            return
        }

        List<List<String>> records = aggregateToPayoutRecord(subledgersWithSameSeller,  date).collect { PayoutRecord payoutRecord ->
            List<String> result = [] as List
            result << dateFormat.format(payoutRecord.date)
            result << payoutRecord.fbPayoutId
            result << payoutRecord.payoutAmount.toString()
            result << IdFormatter.encodeId(payoutRecord.payoutId)
            return result
        }
        csvWriter.writeRecords(records)
    }

    private void upload() {
        File dir = new File(payoutDir)
        List<File> filesToUpload = [] as List
        File[] files = dir.listFiles()
        if (files == null) {
            return
        }

        files.each { File file ->
            if (file.isDirectory()) {
                return
            }
            boolean accepted = CollectionUtils.isEmpty(extensions) ||
                    extensions.any {String extension -> file.name.toUpperCase().endsWith(extension.toUpperCase())}
            if (accepted) {
                filesToUpload << file
            }
        }

        if (filesToUpload.isEmpty()) {
            return
        }

        int numOfSuccess = 0
        LOGGER.info('name=Start_Multiple_Files_Upload')
        filesToUpload.each { File file ->
            if (ftpUtils.uploadFile(file, "${remoteDir}/${file.name}", maxRetry)) {
                FileUtils.moveFile(file, new File(historyDir + File.separator + file.name))
                numOfSuccess++
            } else {
                LOGGER.error('name=File_Upload_Fail_AfterRetry, path={}', file.path)
            }
        }
        LOGGER.info('name=Finish_Multiple_Files_Upload, total={}, numOfSuccess={}', filesToUpload.size(), numOfSuccess)
    }

}
