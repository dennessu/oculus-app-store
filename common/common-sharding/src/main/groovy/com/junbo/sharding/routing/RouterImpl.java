/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.sharding.routing;

import com.junbo.common.id.Id;
import com.junbo.common.id.OrderId;
import com.junbo.common.util.Context;
import com.junbo.configuration.topo.DataCenters;
import com.junbo.configuration.topo.Topology;
import com.junbo.langur.core.routing.Router;
import com.junbo.sharding.id.oculus.OculusIdSchema;
import com.junbo.sharding.id.oculus.OculusObjectId;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Required;

/**
 * The implementation of router.
 */
public class RouterImpl implements Router, InitializingBean {

    private OculusIdSchema oculus40IdSchema;
    private OculusIdSchema oculus48IdSchema;
    private Topology topology;
    private boolean crossDcRoutingEnabled;
    private boolean inDcRoutingEnabled;

    @Required
    public void setOculus40IdSchema(OculusIdSchema oculus40IdSchema) {
        this.oculus40IdSchema = oculus40IdSchema;
    }

    @Required
    public void setOculus48IdSchema(OculusIdSchema oculus48IdSchema) {
        this.oculus48IdSchema = oculus48IdSchema;
    }

    @Required
    public void setTopology(Topology topology) {
        this.topology = topology;
    }

    public void setCrossDcRoutingEnabled(boolean crossDcRoutingEnabled) {
        this.crossDcRoutingEnabled = crossDcRoutingEnabled;
    }

    public void setInDcRoutingEnabled(boolean inDcRoutingEnabled) {
        this.inDcRoutingEnabled = inDcRoutingEnabled;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (oculus40IdSchema.getNumberOfShards() < topology.getNumberOfShards()) {
            throw new RuntimeException(String.format(
                    "Topology is configured to have more shards than Oculus40 ID Generator can provide. id shards: %d, topo: %d",
                    oculus40IdSchema.getNumberOfShards(), topology.getNumberOfShards()));
        }
        if (oculus48IdSchema.getNumberOfShards() < topology.getNumberOfShards()) {
            throw new RuntimeException(String.format(
                    "Topology is configured to have more shards than Oculus48 ID Generator can provide. id shards: %d, topo: %d",
                    oculus48IdSchema.getNumberOfShards(), topology.getNumberOfShards()));
        }
    }

    @Override
    public String getTargetUrl(Class<?> resourceClass, Object[] routingParams, boolean fallbackToAnyLocal) {
        if (routingParams == null || routingParams.length == 0) {
            return null;
        }

        for (Object routingParam : routingParams) {
            if (routingParam != null) {
                return resolveRotingAddress(resourceClass, routingParam);
            }
        }

        if (fallbackToAnyLocal) {
            return null;
        }
        return null;
    }

    private String resolveRotingAddress(Class<?> resourceClass, Object routingParam) {
        resolveShard(routingParam);

        if (crossDcRoutingEnabled) {
            // route across data center
            DataCenters dcs = DataCenters.instance();
            int dcId = Context.get().getDataCenterId();
            if (!dcs.isLocalDataCenter(dcId)) {
                return dcs.getDataCenterUrl(dcId);
            }
        }

        if (inDcRoutingEnabled) {
            // route within data center
            int shardId = Context.get().getShardId();
            if (!topology.isHandledBySelf(shardId)) {
                return topology.getAppServerUrl(shardId);
            }
        }

        // can be handled by current server
        return null;
    }

    private void resolveShard(Object routingParam) {
        Long oculus48Id = null;
        Long oculus40Id = null;
        if (routingParam instanceof Long) {
            oculus48Id = (Long)routingParam;
        } else if (routingParam instanceof OrderId) {
            oculus40Id = ((Id)routingParam).getValue();
        } else if (routingParam instanceof Id) {
            oculus48Id = ((Id)routingParam).getValue();
        } else {
            throw new RuntimeException("Unknown routing parameter type: " + routingParam.getClass());
        }

        OculusObjectId id = null;
        if (oculus40Id != null) {
            id = oculus40IdSchema.parseObjectId(oculus40Id.longValue());
        } else if (oculus48Id != null) {
            id = oculus48IdSchema.parseObjectId(oculus48Id.longValue());
        }
        assert id != null;

        Context.get().setShardId(id.getShardId());
        Context.get().setDataCenterId(id.getDataCenterId());
        Context.get().setTopology(topology);
    }
}
