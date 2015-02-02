/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.order.jobs.subledger.revenue

import com.junbo.catalog.spec.model.item.Item
import com.junbo.catalog.spec.model.item.ItemRevisionGetOptions
import com.junbo.catalog.spec.resource.ItemResource
import com.junbo.catalog.spec.resource.ItemRevisionResource
import com.junbo.common.enumid.CountryId
import com.junbo.common.json.ObjectMapperProvider
import com.junbo.configuration.topo.DataCenters
import com.junbo.identity.spec.v1.model.Organization
import com.junbo.identity.spec.v1.option.model.OrganizationGetOptions
import com.junbo.identity.spec.v1.resource.OrganizationResource
import com.junbo.identity.spec.v1.resource.UserResource
import com.junbo.order.clientproxy.TransactionHelper
import com.junbo.order.db.repo.facade.SubledgerRepositoryFacade
import com.junbo.order.jobs.subledger.payout.Constants
import com.junbo.order.jobs.utils.csv.CSVWriter
import com.junbo.order.jobs.utils.csv.ConcurrentCSVWriter
import com.junbo.order.jobs.utils.ftp.FTPUtils
import com.junbo.order.spec.model.PageParam
import com.junbo.order.spec.model.Subledger
import com.junbo.order.spec.model.SubledgerKeyInfo
import com.junbo.order.spec.model.enums.SubledgerType
import com.junbo.order.spec.model.fb.TransactionType
import groovy.transform.CompileStatic
import org.apache.commons.io.FileUtils
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
 * Created by acer on 2015/1/25.
 */
@CompileStatic
@Component('order.revenueReportJob')
class RevenueReportJob {

    private final static Logger LOGGER = LoggerFactory.getLogger(RevenueReportJob)

    private static final int PAGE_SZE = 100

    @Resource(name ='subledgerRepositoryFacade')
    SubledgerRepositoryFacade subledgerRepository

    @Resource(name = 'order.identityOrganizationClient')
    private OrganizationResource organizationResource

    @Resource(name = 'order.offerItemClient')
    private ItemResource itemResource

    @Resource(name = 'order.offerItemRevisionClient')
    private ItemRevisionResource itemRevisionResource

    @Resource(name = 'order.identityUserClient')
    private UserResource userResource

    @Resource(name='payoutExportJobAsyncTaskExecutor')
    private ThreadPoolTaskExecutor threadPoolTaskExecutor

    @Resource(name = 'subledgerPayoutFTPUtils')
    private FTPUtils ftpUtils

    @Value('${order.jobs.subledger.localDir.revenue.bankId.physical}')
    private String physicalBankId

    @Value('${order.jobs.subledger.localDir.revenue.bankId.digital}')
    private String digitalBankId

    private Set<CountryId> countriesUS

    private List<String> columns

    @Resource(name ='orderTransactionHelper')
    TransactionHelper transactionHelper

    @Value('${order.jobs.subledger.localDir.revenue}')
    private String localDir

    @Value('${order.jobs.subledger.localDir.revenue.history}')
    private String historyDir

    @Value('${order.jobs.subledger.remoteDir.revenue}')
    private String remoteDir

    @Value('${order.jobs.subledger.maxRetry}')
    private int maxRetry

    @Value('${order.jobs.subledger.revenue.usCountries}')
    void setCountriesUS(String countriesUS) {
        this.countriesUS = [] as Set
        for(String country : countriesUS.split(',')) {
            if (country.trim().isEmpty()) {
                continue
            }
            this.countriesUS << new CountryId(country.trim())
        }
    }

    @Value('${order.jobs.subledger.revenue.columns}')
    void setColumns(List<String> columns) {
        this.columns = columns
    }

    class RevenueReportRecord {
        BigDecimal totalAmount = 0
        BigDecimal fbAmount = 0
        BigDecimal devAmount = 0
        BigDecimal txAmount = 0
        Long revShareId
        String userEntity
        String appName

        public void aggregate(RevenueReportRecord record) {
            this.totalAmount += record.totalAmount
            this.fbAmount += record.fbAmount
            this.devAmount += record.devAmount
            this.txAmount += record.txAmount
        }
    }

    public void execute() {
        innerExecute(new Date())
    }

    public void execute(String dateString) {
        innerExecute(new SimpleDateFormat('yyyy-MM-dd').parse(dateString))
    }

    private void innerExecute(Date time) {
        time = new Date(time.year, time.month, time.date)
        Calendar calendar = Calendar.getInstance()
        calendar.setTime(time)
        calendar.add(Calendar.MONTH, -1)
        Date start = calendar.getTime()
        innerExecute(start, time)
    }

    private void innerExecute(Date startDate, Date endDate) {
        MDC.put(Constants.X_REQUEST_ID, UUID.randomUUID().toString());
        long startTime = System.currentTimeMillis()
        LOGGER.info('name=RevenueReportJob_Start')
        File file = getFile(startDate)
        File historyFile = getHistoryFile(file)

        try {
            if (historyFile.exists()) {
                LOGGER.info("name=RevenueFileAlreadyUploaded, file={}", file.name)
                return
            } else if (!file.exists()) {
                // create work file
                File work = new File(file.getPath() + ".tmp")
                FileUtils.openOutputStream(work, false).close()
                CSVWriter csvWriter = new ConcurrentCSVWriter(work, null)
                csvWriter.writeRecords(Arrays.asList(columns))

                List<Map<String, Object>> records = aggregate(startDate, endDate)

                records.each { Map<String, Object> record ->
                    List<String> row = []
                    columns.each { String columnName ->
                        row << String.valueOf(record[columnName])
                    }
                    csvWriter.writeRecords(Arrays.asList(row))
                }
                csvWriter.close()
                FileUtils.moveFile(work, file)
            }

            if (ftpUtils.uploadFile(file, "${remoteDir}/${file.name}", maxRetry)) {
                FileUtils.moveFile(file, historyFile)
            }

        } finally {
            LOGGER.info('name=RevenueReportJob_End, latencyInMs={}', System.currentTimeMillis() - startTime)
        }
    }

    private List<Map<String, Object>> aggregate(Date startDate, Date endDate) {
        List<Future<List>> tasks = []
        long startTime = System.currentTimeMillis()
        MDC.put(Constants.X_REQUEST_ID, UUID.randomUUID().toString());
        LOGGER.info('name=Aggregate_Subledger_Revenue_Start')

        def self = this
        for (int dcId : DataCenters.instance().getDataCenterIds()) {
            for (int shardId = 0; shardId < DataCenters.instance().getDataCenter(dcId).numberOfShard; ++shardId) {
                final int _dcId = dcId
                final int _shardId = shardId
                Future<List> future = (Future<List>)threadPoolTaskExecutor.submit(new Callable<List>() {
                    @Override
                    List call() throws Exception {
                        MDC.put(Constants.X_REQUEST_ID, UUID.randomUUID().toString());
                        return self.aggregateShard(_dcId, _shardId, startDate, endDate)
                    }
                })
                tasks << future
            }
        }

        List<Map<String, Object>> results = []
        tasks.each { Future<List> task ->
            results.addAll(task.get())
        }

        LOGGER.info('name=Aggregate_Subledger_Revenue_End, latencyInMS={}', System.currentTimeMillis() - startTime)
        return results
    }

    private List<Map<String, Object>> aggregateShard(int dcId, int shardId, Date startDate, Date endDate) {
        LOGGER.info('name=Revenue_Aggregate_Start, dcId={}, shardId={}, startDate={}, endDate={}', dcId, shardId,
            ObjectMapperProvider.instance().writeValueAsString(startDate), ObjectMapperProvider.instance().writeValueAsString(endDate))
        long startTime = System.currentTimeMillis()
        int start = 0, subledgerNum = 0
        Map<RevenueReportKey, RevenueReportRecord> records = [:]
        while (true) {
            List<Subledger> subledgers
            transactionHelper.executeInTransaction {
                subledgers = subledgerRepository.getSubledgersByTime(dcId, shardId, startDate, endDate,
                        new PageParam(start: start, count: PAGE_SZE))
            }

            subledgers.each { Subledger subledger ->
                subledgerNum++
                RevenueReportKey key = getRevenueReportKey(subledger)
                RevenueReportRecord record = getRevenueReportRecord(subledger)
                RevenueReportRecord existing = records[key]
                if (existing != null) {
                    existing.aggregate(record)
                } else {
                    records[key] = record
                }
            }

            if (subledgers.size() < PAGE_SZE) {
                break;
            }
            start += PAGE_SZE
        }

        def results =  records.entrySet().collect { Map.Entry<RevenueReportKey, RevenueReportRecord> entry ->
            return toColumnsMap(entry.key, entry.value)
        }.asList()
        LOGGER.info('name=Revenue_Aggregate_End, dcId={}, shardId={}, latencyMs={}, subledgerNum={}', dcId,
                shardId, System.currentTimeMillis() - startTime, subledgerNum)
        return results
    }

    private Map<String, Object> toColumnsMap(RevenueReportKey key, RevenueReportRecord record) {
        Map<String, Object> result = [:]
        result['account_id'] = key.itemId
        result['product_type'] = 'One_time_payment'
        result['bank_id'] = key.bankId
        result['account_type'] = 'App'
        result['account_subtype'] = ''
        result['fbobj_id'] = key.itemId
        result['name'] = record.appName
        result['financial_id'] = key.financialId
        result['action_type'] = key.actionType
        result['revshare_id'] = record.revShareId
        result['user_entity'] = record.userEntity
        result['dev_entity'] = ''
        result['payout_currency'] = 'USD'
        result['total_amount'] = record.totalAmount
        result['fb_amount'] = record.fbAmount
        result['dev_amount'] = record.devAmount
        result['txn_type'] = key.txnType
        result['mixed_txn'] = key.getMixedTxn()
        result['tax_amount'] = record.txAmount
        return result
    }

    private RevenueReportRecord getRevenueReportRecord(Subledger subledger) {
        RevenueReportRecord record = new RevenueReportRecord()
        record.devAmount = subledger.totalPayoutAmount
        record.fbAmount = subledger.totalAmount - subledger.totalPayoutAmount
        record.totalAmount = subledger.totalAmount
        record.txAmount = subledger.taxAmount

        Organization organization = organizationResource.get(subledger.seller, new OrganizationGetOptions()).get()
        record.revShareId = (organization.publisherRevenueRatio * 100).longValue()
        record.userEntity = 'US' // always US for v1

        Item item = itemResource.getItem(subledger.item?.value).get()
        def itemRevision = itemRevisionResource.getItemRevision(item.currentRevisionId, new ItemRevisionGetOptions(locale: 'en_US')).get()
        record.appName = itemRevision.locales?.get('en_US')?.name
        return record
    }

    private RevenueReportKey getRevenueReportKey(Subledger subledger) {
        RevenueReportKey key = new RevenueReportKey()
        SubledgerKeyInfo subledgerKeyInfo = SubledgerKeyInfo.fromProperties(subledger.properties)

        switch (SubledgerType.valueOf(subledger.subledgerType)) {
            case SubledgerType.PAYOUT:
                key.actionType = TransactionType.S.name()
                break
            case SubledgerType.REFUND:
                key.actionType = TransactionType.R.name()
                break
            case SubledgerType.DECLINE:
                key.actionType = TransactionType.N.name()
                break
            case SubledgerType.CHARGE_BACK:
            case SubledgerType.CHARGE_BACK_REFUNDED:
                key.actionType = TransactionType.C.name()
                break
            case SubledgerType.CHARGE_BACK_OTW:
                key.actionType = TransactionType.D.name()
                break
            case SubledgerType.CHARGE_BACK_REVERSAL:
                key.actionType = TransactionType.K.name()
                break
            case SubledgerType.CHARGE_BACK_REVERSAL_OTW:
                key.actionType = TransactionType.J.name()
                break
            default:
                throw new RuntimeException('should not be here')
        }

        key.bankId = subledgerKeyInfo.isPhysical() ? physicalBankId : digitalBankId
        key.itemId = subledger.item
        key.seller = subledger.seller
        key.financialId = subledgerKeyInfo.fbPayoutOrgId
        key.txnType = subledgerKeyInfo.isPhysical() ? 'PHYSICAL' : 'DIGITAL'
        key.mixedTxn = subledgerKeyInfo.mixTxn

        return key
    }

    private File getFile(Date date) {
        String fileName = "${Constants.REVENUE_FILE_NAME}_${new SimpleDateFormat('yyyy_MM_dd').format(date)}.${Constants.CSV_EXTENSION}"
        return new File(new File(localDir), fileName)
    }

    private File getHistoryFile(File file) {
        return new File(new File(historyDir), file.getName())
    }

}
