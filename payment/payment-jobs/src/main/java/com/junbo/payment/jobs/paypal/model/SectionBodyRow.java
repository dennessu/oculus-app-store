/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.payment.jobs.paypal.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * raw data row.
 */
public class SectionBodyRow {
    private static final Logger LOGGER = LoggerFactory.getLogger(SectionBodyRow.class);
    private String transactionID;
    private String invoiceID;
    private String payPalReferenceID;
    private ReferenceIDType refIdType;
    private String transactionEventCode;
    private Date transactionInitiationDate;
    private Date transactionCompleteDate;
    private TransactionType trxType;
    private BigDecimal grossTrxAmount;
    private String grossTrxCurrency;
    private TransactionType feeType;
    private BigDecimal feeAmount;
    private String feeCurrency;
    private String customField;
    private String consumerID;
    private String paymentTrackingID;
    private String storeID;

    private SectionBodyRow(String[] values){
        transactionID = values[1];
        invoiceID = values[2];
        payPalReferenceID = values[3];
        refIdType = ReferenceIDType.getReferenceIDType(values[4]);
        transactionEventCode = values[5];
        transactionInitiationDate = parseDate(values[6]);
        transactionCompleteDate = parseDate(values[7]);
        trxType = TransactionType.getTransactionType(values[8]);
        grossTrxCurrency = values[10];
        grossTrxAmount = parseBigDecimal(values[9]);
        feeType = TransactionType.getTransactionType(values[11]);
        feeCurrency = values[13];
        feeAmount = parseBigDecimal(values[12]);
        customField = values[14];
        if(values.length == 15){
            return;
        }
        consumerID = values[15];
        paymentTrackingID = values[16];
        if(values.length == 17){
            return;
        }
        storeID = values[17];
    }

    public static SectionBodyRow getRawData(String rowType, String[] values){
        if(rowType.equalsIgnoreCase(RowType.SB.name())){
            return new SectionBodyRow(values);
        }
        return null;
    }

    private Date parseDate(String dateString) {
        DateFormat dfRead = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss Z");
        Date date = null;
        try {
            date = dfRead.parse(dateString);
        } catch (ParseException e) {
            LOGGER.error("error parse date", e);
        }
        return date;
    }

    private BigDecimal parseBigDecimal(String amount){
        BigDecimal result = new BigDecimal((amount.length() == 0) ? "0" : amount);
        result = result.divide(new BigDecimal(100));
        return result;

    }

    public String getInvoiceID(){
        return invoiceID;
    }

    public void setInvoiceID(String invoiceID){
        this.invoiceID = invoiceID;
    }

    public String getTransactionID() {
        return transactionID;
    }

    public String getPayPalReferenceID() {
        return payPalReferenceID;
    }

    public ReferenceIDType getRefIdType() {
        return refIdType;
    }

    public String getTransactionEventCode() {
        return transactionEventCode;
    }

    public Date getTransactionInitiationDate() {
        return transactionInitiationDate;
    }

    public Date getTransactionCompleteDate() {
        return transactionCompleteDate;
    }

    public TransactionType getTrxType() {
        return trxType;
    }

    public BigDecimal getGrossTrxAmount() {
        return grossTrxAmount;
    }

    public String getGrossTrxCurrency() {
        return grossTrxCurrency;
    }

    public TransactionType getFeeType() {
        return feeType;
    }

    public BigDecimal getFeeAmount() {
        return feeAmount;
    }

    public String getFeeCurrency() {
        return feeCurrency;
    }

    public String getCustomField() {
        return customField;
    }

    public String getConsumerID() {
        return consumerID;
    }

    public String getPaymentTrackingID() {
        return paymentTrackingID;
    }

    public String getStoreID() {
        return storeID;
    }
}
