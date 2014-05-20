/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.common.routing;

import com.junbo.common.routing.model.DataAccessPolicy;
import com.junbo.common.util.Context;
import com.junbo.common.util.Utils;
import com.junbo.configuration.topo.DataCenters;
import com.junbo.configuration.topo.Topology;
import com.junbo.langur.core.routing.Router;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

/**
 * The implementation of router.
 */
public class RouterImpl implements Router {
    private static final Logger logger = LoggerFactory.getLogger(RouterImpl.class);

    private Topology topology;
    private boolean crossDcRoutingEnabled;
    private boolean inDcRoutingEnabled;
    private DataAccessPolicy defaultPolicy;

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

    public void setDefaultPolicy(DataAccessPolicy defaultPolicy) {
        this.defaultPolicy = defaultPolicy;
    }

    @Override
    public String getTargetUrl(Class<?> resourceClass, Object[] routingParams, boolean fallbackToAnyLocal) {

        DataAccessPolicy policy = DataAccessPolicies.instance().getHttpDataAccessPolicy(Context.get().getHttpMethod(), resourceClass);
        if (policy == null) {
            policy = defaultPolicy;
        }
        if (logger.isDebugEnabled()) {
            logger.debug("Setting effective dataAccessPolicy in call. url: {}, policy: {}", Context.get().getRequestContext().getUriInfo().getRequestUri(), policy);
        }
        Context.get().setDataAccessPolicy(policy);

        if (policy == null || policy == DataAccessPolicy.CLOUDANT_FIRST || policy == DataAccessPolicy.CLOUDANT_ONLY) {
            return null;
        }

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
        long oculusId = Utils.keyToLong(routingParam);

        // both Oculus40Id and Oculus48Id share the same bit layout for dc and shard
        int dc = (int)((oculusId >> 2) & 0xF);
        int shard = (int)((oculusId >> 6) & 0xFF);

        Context.get().setDataCenterId(dc);
        Context.get().setShardId(shard);
        Context.get().setTopology(topology);
    }
}
