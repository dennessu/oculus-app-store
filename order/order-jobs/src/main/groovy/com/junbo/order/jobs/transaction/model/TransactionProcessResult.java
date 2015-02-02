/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.order.jobs.transaction.model;

/**
 * Created by acer on 2015/1/30.
 */
public class TransactionProcessResult {

    private DiscrepancyRecord discrepancyRecord;

    private boolean processed;

    private Exception error;

    public boolean isProcessed() {
        return processed;
    }

    public void setProcessed(boolean processed) {
        this.processed = processed;
    }

    public DiscrepancyRecord getDiscrepancyRecord() {
        return discrepancyRecord;
    }

    public void setDiscrepancyRecord(DiscrepancyRecord discrepancyRecord) {
        this.discrepancyRecord = discrepancyRecord;
    }

    public Exception getError() {
        return error;
    }

    public void setError(Exception error) {
        this.error = error;
    }
}
