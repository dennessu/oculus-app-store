/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.payment.jobs.adyen.file;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

/**
 * FileProcessing Job.
 */
public class FileProcessingJob {
    private FileProcessor fileProcessor;

    private static final Logger LOGGER = LoggerFactory.getLogger(FileProcessingJob.class);

    public void execute() {
        LOGGER.info("Starting Adyen File Processing job");

        fileProcessor.processFiles();

        LOGGER.info("Adyen File Processing finished");
    }

    @Required
    public void setFileProcessor(FileProcessor fileProcessor) {
        this.fileProcessor = fileProcessor;
    }

}
