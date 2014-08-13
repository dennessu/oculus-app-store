/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 SilkCloud and/or its affiliates. All rights reserved.
 */
package com.junbo.common.job.cache;

import com.junbo.common.cloudant.client.CloudantUri;
import org.springframework.beans.factory.InitializingBean;

import java.util.List;

/**
 * CacheSnifferJob.
 */
public class CacheSnifferJob implements InitializingBean {
    @Override
    public void afterPropertiesSet() throws Exception {

    }

    private void listen() {
        List<CloudantUri> cloudantInstances = CloudantSniffer.instance().getCloudantInstances();

        for (CloudantUri cloudantUri : cloudantInstances) {
            List<String> databases = CloudantSniffer.instance().getAllDatabases(cloudantUri);

            for (String database : databases) {
                String lastChange = CloudantSniffer.instance().getLastChange(cloudantUri, database);
            }
        }
    }
}
