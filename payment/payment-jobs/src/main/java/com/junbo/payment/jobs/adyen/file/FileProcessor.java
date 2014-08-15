/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.payment.jobs.adyen.file;

import com.junbo.payment.common.CommonUtil;
import com.junbo.payment.common.exception.AppServerExceptions;
import com.junbo.payment.db.repository.SettlementDetailRepository;
import com.junbo.payment.spec.internal.SettlementDetail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * File Processor.
 */
public class FileProcessor {
    private static final Logger LOGGER = LoggerFactory.getLogger(FileProcessor.class);
    private static final String filePrefix = "settlement_detail_report_batch_";
    private static final String fileFormat = ".csv";
    private static final Long NO_MORE_BATCHES = -1L;
    private int concurrentCount;
    private int batchSize;
    private int timeLimitMinutes;
    private String batchDirectory;

    private PlatformTransactionManager transactionManager;
    private SettlementDetailRepository settlementDetailRepo;

    @Required
    public void setBatchDirectory(String batchDirectory) {
        this.batchDirectory = batchDirectory;
    }
    @Required
    public void setSettlementDetailRepo(SettlementDetailRepository settlementDetailRepo) {
        this.settlementDetailRepo = settlementDetailRepo;
    }

    @Required
    public void setConcurrentCount(int concurrentCount) {
        this.concurrentCount = concurrentCount;
    }

    @Required
    public void setBatchSize(int batchSize) {
        this.batchSize = batchSize;
    }

    @Required
    public void setTimeLimitMinutes(int timeLimitMinutes) {
        this.timeLimitMinutes = timeLimitMinutes;
    }

    @Required
    public void setTransactionManager(PlatformTransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    public void processFiles() {
        ExecutorService consumers = Executors.newFixedThreadPool(concurrentCount);

        final LinkedBlockingQueue<Long> batchIds = new LinkedBlockingQueue<Long>();
        File batchDir = new File(batchDirectory);
        if(!batchDir.exists() || !batchDir.isDirectory()){
            LOGGER.error("batch directory not exists:" + batchDirectory);
            throw AppServerExceptions.INSTANCE.invalidBatchFile(batchDirectory).exception();
        }
        for(File file : batchDir.listFiles()){
            batchIds.add(getBatchIndex(file.getPath()));
        }
        for (int i = 0; i < concurrentCount; i++) {
            consumers.execute(new SingleBatchProcessor(batchIds));
        }

        consumers.shutdown();

        try {
            if (!consumers.awaitTermination(timeLimitMinutes, TimeUnit.MINUTES)) {
                consumers.shutdownNow();
                LOGGER.error("Settlement Job ran too long. Killed it...");
            }

        } catch (InterruptedException ex) {
            consumers.shutdownNow();
            LOGGER.error("Settlement Job was interrupted. Shutting down...", ex);
        }

    }

    private void processBalance(Long batchId) {
        LOGGER.info("Sending check balance request for batch id: " + batchId);
        try {
            loadFiles(new File(batchDirectory, filePrefix + batchId + fileFormat).getAbsolutePath());
        } catch (IOException e) {
            LOGGER.error("Sending check balance request for batch id: " + batchId);
            throw AppServerExceptions.INSTANCE.errorParseBatchFile(batchId.toString()).exception();
        }

    }

    private class SingleBatchProcessor implements Runnable {

        private final LinkedBlockingQueue<Long> batchIds;

        public SingleBatchProcessor(LinkedBlockingQueue<Long> ids) {
            this.batchIds = ids;
        }

        @Override
        public void run() {
            LOGGER.info("Start thread to check balance");
            Long batchId;
            while (true) {
                try {
                    batchId = batchIds.take();

                    if (batchId.equals(NO_MORE_BATCHES)) {
                        break;
                    } else if (batchId != null) {
                        final Long id = batchId;
                        processBalance(id);
                    }
                } catch (InterruptedException ex) {
                    LOGGER.warn("Balance Checking thread is interrupted.");
                }
            }

            batchIds.add(NO_MORE_BATCHES);
            LOGGER.info("Thread to check balance finished");
        }
    }

    public void loadFiles(String filePath) throws IOException {
        try{
            if(CommonUtil.isNullOrEmpty(filePath)){
                LOGGER.error("error parse file name:" + filePath);
                throw AppServerExceptions.INSTANCE.invalidBatchFile(filePath).exception();
            }
            Long fileIndex = getBatchIndex(filePath);
            clearIfExists(fileIndex);
            CsvReader reader = new CsvReader(filePath);
            reader.readHeaders();
            int lineNumber = 0;
            List<SettlementDetail> details = new ArrayList<SettlementDetail>();
            while (!reader.readLine()){
                lineNumber++;
                SettlementDetail settlementDetail = new SettlementDetail();
                settlementDetail.setBatchIndex(fileIndex);
                settlementDetail.setCompanyAccount(reader.getFieldValue("Company Account"));
                settlementDetail.setMerchantAccount(reader.getFieldValue("Merchant Account"));
                settlementDetail.setPspReference(reader.getFieldValue("Psp Reference"));
                settlementDetail.setMerchantReference(reader.getFieldValue("Merchant Reference"));
                settlementDetail.setPaymentMethod(reader.getFieldValue("Payment Method"));
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                try {
                    settlementDetail.setCreationDate(df.parse(reader.getFieldValue("Creation Date")));
                } catch (ParseException e) {
                    LOGGER.error("error parse create date file:" + filePath);
                    throw AppServerExceptions.INSTANCE.invalidBatchFile(filePath).exception();
                }
                settlementDetail.setTimeZone(reader.getFieldValue("TimeZone"));
                settlementDetail.setType(reader.getFieldValue("Type"));
                settlementDetail.setModificationReference(reader.getFieldValue("Modification Reference"));
                settlementDetail.setGrossCurrency(reader.getFieldValue("Gross Currency"));
                settlementDetail.setGrossDebit(getDecimal(reader.getFieldValue("Gross Debit (GC)")));
                settlementDetail.setGrossCredit(getDecimal(reader.getFieldValue("Gross Credit (GC)")));
                settlementDetail.setExchangeRate(getDecimal(reader.getFieldValue("Exchange Rate")));
                settlementDetail.setNetCurrency(reader.getFieldValue("Net Currency"));
                settlementDetail.setNetDebit(getDecimal(reader.getFieldValue("Net Debit (NC)")));
                settlementDetail.setNetCredit(getDecimal(reader.getFieldValue("Net Credit (NC)")));
                settlementDetail.setCommission(reader.getFieldValue("Commission (NC)"));
                settlementDetail.setMarkup(reader.getFieldValue("Markup (NC)"));
                settlementDetail.setSchemeFees(getDecimal(reader.getFieldValue("Scheme Fees (NC)")));
                settlementDetail.setInterchange(reader.getFieldValue("Interchange (NC)"));
                settlementDetail.setPaymentMethodVariant(reader.getFieldValue("Payment Method Variant"));
                settlementDetail.setAcquirer(reader.getFieldValue("Acquirer"));
                settlementDetail.setModificationMerchantReference(reader.getFieldValue("Modification Merchant Reference"));
                settlementDetail.setSplitSettlement(reader.getFieldValue("Split Settlement"));
                details.add(settlementDetail);
                // perform program logic here
                if(lineNumber >= batchSize){
                    //load to Database:
                    saveToDB(details);
                    details.clear();
                    lineNumber = 0;
                }
            }
            if(lineNumber > 0){
                //load to Database for the rest:
                saveToDB(details);
            }
            reader.close();
            new File(filePath).delete();
        } catch (FileNotFoundException e) {
            LOGGER.error("file not found:" + filePath);
            throw AppServerExceptions.INSTANCE.invalidBatchFile(filePath).exception();
        } catch (IOException e) {
            LOGGER.error("error read file:" + filePath);
            throw AppServerExceptions.INSTANCE.invalidBatchFile(filePath).exception();
        }
    }

    private Long getBatchIndex(String filePath) {
        String[] fileNames = filePath.split("_");
        Long fileIndex = null;
        try{
            fileIndex = Long.parseLong(fileNames[fileNames.length - 1].replace(fileFormat, ""));
        }catch (Exception ex){
            LOGGER.error("error parse file name:" + filePath);
            throw AppServerExceptions.INSTANCE.invalidBatchFile(filePath).exception();
        }
        return fileIndex;
    }

    private void saveToDB(final List<SettlementDetail> details) {
        TransactionTemplate template = new TransactionTemplate(transactionManager);
        template.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                settlementDetailRepo.saveDetails(details);
            }
        });
    }

    private void clearIfExists(final Long fileIndex) {
        TransactionTemplate template = new TransactionTemplate(transactionManager);
        template.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                settlementDetailRepo.clearIfExists(fileIndex);
            }
        });

    }

    private BigDecimal getDecimal(String value){
        if(CommonUtil.isNullOrEmpty(value)){
            return null;
        }else{
            return new BigDecimal(value);
        }
    }
}
