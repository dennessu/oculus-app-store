/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.payment.jobs.paypal;

import com.junbo.payment.common.CommonUtil;
import com.junbo.payment.core.PaymentTransactionService;
import com.junbo.payment.jobs.paypal.model.ReportSection;
import com.junbo.payment.jobs.paypal.model.SectionBodyRow;
import com.junbo.payment.jobs.paypal.model.SettlementReport;
import com.junbo.payment.spec.enums.PaymentEventType;
import com.junbo.payment.spec.enums.PaymentStatus;
import com.junbo.payment.spec.model.PaymentEvent;
import com.junbo.payment.spec.model.PaymentTransaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Reconcile Processor.
 */
public class PaypalReconcileProcessor {
    private static final Logger LOGGER = LoggerFactory.getLogger(PaypalReconcileProcessor.class);
    protected static final String SUCCESS_EVENT_RESPONSE = "{\"result\": \"OK\"}";
    private SFTPDownloader sftpDownloader;
    private String remotePath;
    private String localPath;
    private PaymentTransactionService paymentTransactionService;

    /*
    public static void main(String[] args) throws Exception {
        String[] files = {"D:\\paypal\\STL-20140814.01.2.csv"};
        PaypalReconcileProcessor processor = new PaypalReconcileProcessor();
        try{
            processor.process(files);
        }catch (Exception ex){
        }
    }
    */

    public void execute(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        //download and process the files generated yesterday
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        Date date = cal.getTime();
        cal.add(Calendar.DATE, -1);
        String strDate = sdf.format(date);
        try{
            process(strDate);
        }catch (Exception ex){
            LOGGER.error("paypal reconcile execute error on date:" + strDate + " " + ex.toString());
        }
    }

    private String process(String date){
        LOGGER.info("Start PayPal Settlement Job for date: " + date);
        String[] fileToProcess = getFileToProcess(date);
        String files = "";
        for(String file : fileToProcess){
            files += (file + ";");
        }
        LOGGER.info("Files to process: " + files);
        if (fileToProcess == null) return null;
        String result = null;
        try{
            result = process(fileToProcess);
        }catch (Exception e) {
            LOGGER.error("PayPal Settlement Job Stopped with error when processing file for date: " + date + ". " + e.toString());
            return null;
        }
        if(!CommonUtil.isNullOrEmpty(result)){
            LOGGER.info("Finish PayPal Settlement Job for date: " + date);
        }
        return result;
    }


    private String[] getFileToProcess(String strDate){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        try{
            sdf.parse(strDate);
        }catch (java.text.ParseException e){
            LOGGER.error("error parse date: " + strDate + e.toString());
            return null;
        }
        String[] allFiles = null;
        List<String> fileToProcess = new ArrayList<String>();
        try{
            allFiles = sftpDownloader.getFileLists(remotePath);
            if(allFiles.length == 0){
                LOGGER.error("no settlement file available.");
            }
            for(String file : allFiles){
                if(file.startsWith("STL") && file.contains(strDate)){
                    sftpDownloader.download(localPath + file, remotePath + file);
                    fileToProcess.add(localPath + file);
                }
            }
        }catch (Exception e) {
            LOGGER.error("failed downloading PayPal file for date: " + strDate + " " + e.toString());
            return null;
        }

        if (fileToProcess.isEmpty()){
            LOGGER.info("No files to process.");
            return null;
        }
        return fileToProcess.toArray(new String[0]);
    }

    public String process(String[] unprocessedFile) throws Exception{
        SettlementReport report = new SettlementReport();
        report.loadReport(unprocessedFile);

        if(report.getSections().isEmpty()){
            LOGGER.error("error loading report files");
            return "";
        }
        List<SectionBodyRow> rawDataRows = new ArrayList<SectionBodyRow>();
        for(ReportSection section : report.getSections()){
            for(SectionBodyRow row : section.getRows()){
                rawDataRows.add(row);
            }
        }
        for(SectionBodyRow row : rawDataRows){
            processPayment(row);
        }
        return null;
    }

    private void processPayment(SectionBodyRow row){
        LOGGER.info("Sending check payment request for payment id: " + row.getInvoiceID());
        PaymentTransaction payment = new PaymentTransaction();
        payment.setId(CommonUtil.decode(row.getInvoiceID()));
        PaymentEvent event = new PaymentEvent();
        event.setPaymentId(payment.getId());
        event.setType(PaymentEventType.REPORT_EVENT.toString());
        event.setStatus(PaymentStatus.SETTLED.toString());
        event.setRequest(CommonUtil.toJson(row, null));
        event.setResponse(SUCCESS_EVENT_RESPONSE);
        try{
            paymentTransactionService.reportPaymentEvent(event, null, null).get();
        }catch (Exception ex){
            LOGGER.error("Error in reconcile batch:" + row.getPaymentTrackingID() + " due to: " + ex.toString());
        }
    }

    @Required
    public void setPaymentTransactionService(PaymentTransactionService paymentTransactionService) {
        this.paymentTransactionService = paymentTransactionService;
    }

    @Required
    public void setRemotePath(String remotePath){
        this.remotePath = remotePath;
    }

    @Required
    public void setLocalPath(String localPath){
        this.localPath = localPath;
    }

    @Required
    public void setSftpDownloader(SFTPDownloader sftpDownloader) {
        this.sftpDownloader = sftpDownloader;
    }
}
