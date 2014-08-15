package com.junbo.payment.jobs.service;

import com.junbo.payment.jobs.BaseTest;
import com.junbo.payment.jobs.adyen.file.FileProcessingJob;
import com.junbo.payment.jobs.adyen.file.FileProcessor;
import com.junbo.payment.jobs.adyen.reconcile.AdyenReconcileJob;
import com.junbo.payment.jobs.adyen.reconcile.AdyenReconcileProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.Test;

import java.util.concurrent.ExecutionException;

public class PaymentJobTest extends BaseTest {
    @Autowired
    private FileProcessor fileProcessor;
    @Autowired
    private AdyenReconcileProcessor reconcileProcessor;

    @Test(enabled = false)
    public void testFileProcessJob() throws ExecutionException, InterruptedException {
        FileProcessingJob job = new FileProcessingJob();
        fileProcessor.setBatchDirectory("D:\\adyen\\batches");
        job.setFileProcessor(fileProcessor);
        job.execute();
    }

    @Test(enabled = false)
    public void testReconcileJob() throws ExecutionException, InterruptedException {
        AdyenReconcileJob job = new AdyenReconcileJob();
        job.setReconcileProcessor(reconcileProcessor);
        job.execute();
    }
}
