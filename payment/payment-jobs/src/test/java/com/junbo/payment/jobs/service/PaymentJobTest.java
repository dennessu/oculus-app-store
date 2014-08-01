package com.junbo.payment.jobs.service;

import com.junbo.payment.jobs.BaseTest;
import com.junbo.payment.jobs.file.FileProcessingJob;
import com.junbo.payment.jobs.file.FileProcessor;
import com.junbo.payment.jobs.reconcile.ReconcileJob;
import com.junbo.payment.jobs.reconcile.ReconcileProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.Test;

import java.util.concurrent.ExecutionException;

public class PaymentJobTest extends BaseTest {
    @Autowired
    private FileProcessor fileProcessor;
    @Autowired
    private ReconcileProcessor reconcileProcessor;

    @Test(enabled = false)
    public void testFileProcessJob() throws ExecutionException, InterruptedException {
        FileProcessingJob job = new FileProcessingJob();
        fileProcessor.setBatchDirectory("D:\\adyen\\batches");
        job.setFileProcessor(fileProcessor);
        job.execute();
    }

    @Test(enabled = false)
    public void testReconcileJob() throws ExecutionException, InterruptedException {
        ReconcileJob job = new ReconcileJob();
        job.setReconcileProcessor(reconcileProcessor);
        job.execute();
    }
}
