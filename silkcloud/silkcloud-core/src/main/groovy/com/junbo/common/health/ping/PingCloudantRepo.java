/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.common.health.ping;

import com.junbo.common.cloudant.CloudantClient;
import com.junbo.common.cloudant.client.CloudantDbUri;
import com.junbo.configuration.topo.DataCenters;
import com.junbo.langur.core.promise.Promise;
import org.springframework.beans.factory.InitializingBean;

import java.util.List;

/**
 * The ping repo implemented by Cloudant.
 */
public class PingCloudantRepo extends CloudantClient<Ping> implements InitializingBean {

    private int remoteDcId;
    private CloudantDbUri remoteDcUri;

    public void afterPropertiesSet() throws Exception {
        super.afterPropertiesSet();

        // get the URI of remote DC with current DC ID +1
        DataCenters dataCenters = DataCenters.instance();
        int currentDcId = dataCenters.currentDataCenterId();
        List<Integer> allDcs = dataCenters.getDataCenterIds();

        int remoteDcIndex = allDcs.indexOf(currentDcId) + 1;
        if (remoteDcIndex >= allDcs.size()) {
            remoteDcIndex = 0;
        }
        remoteDcId = allDcs.get(remoteDcIndex);

        remoteDcUri = new CloudantDbUri();
        remoteDcUri.setCloudantUri(cloudantGlobalUri.getUri(remoteDcId));
        remoteDcUri.setDbName(dbName);
        remoteDcUri.setFullDbName(dbNamePrefix + dbName);
    }

    public Promise<Ping> get(String id) {
        if (id == null) {
            throw new IllegalArgumentException("id is null");
        }
        return cloudantGet(id);
    }

    public Promise<Ping> create(Ping model) {
        if (model == null) {
            throw new IllegalArgumentException("model is null");
        }
        return cloudantPost(model);
    }

    public Promise<Ping> update(Ping model) {
        if (model == null) {
            throw new IllegalArgumentException("model is null");
        }
        return cloudantPut(model, model);
    }

    public Promise<Void> delete(String id) {
        return cloudantDelete(id);
    }


    public Promise<Ping> getRemote(String id) {
        if (id == null) {
            throw new IllegalArgumentException("id is null");
        }
        return getEffective().cloudantGet(getRemoteDbUri(), entityClass, id);
    }

    public CloudantDbUri getRemoteDbUri() {
        return remoteDcUri;
    }

    public int getRemoteDcId() {
        return remoteDcId;
    }
}
