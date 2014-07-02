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
import com.junbo.langur.core.context.JunboHttpContext;
import com.junbo.langur.core.routing.Router;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.StringUtils;

import javax.ws.rs.core.MultivaluedMap;

/**
 * The implementation of router.
 */
public class RouterImpl implements Router {
    private static final Logger logger = LoggerFactory.getLogger(RouterImpl.class);

    private static final String X_ROUTING_PATH = "X-Routing-Path";
    private static final String X_ROUTING_DC_SHARD = "X-Routing-DC-Shard";
    private static final String X_DATAACCESS_MODE = "X-DataAccess-Mode";

    private Topology topology;
    private boolean crossDcRoutingEnabled;
    private boolean inDcRoutingEnabled;
    private boolean showRoutingPath;
    private boolean forceRoute;
    private int maxRoutingHops;
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

    public void setShowRoutingPath(boolean showRoutingPath) {
        this.showRoutingPath = showRoutingPath;
    }

    public void setForceRoute(boolean forceRoute) {
        this.forceRoute = forceRoute;
    }

    public void setMaxRoutingHops(int maxRoutingHops) {
        this.maxRoutingHops = maxRoutingHops;
    }

    public void setDefaultPolicy(DataAccessPolicy defaultPolicy) {
        this.defaultPolicy = defaultPolicy;
    }

    @Override
    public String getTargetUrl(Class<?> resourceClass, Object[] routingParams, boolean fallbackToAnyLocal) {
        String result = getTargetUrlInternal(resourceClass, routingParams, fallbackToAnyLocal);
        if (result != null) {
            logger.info("Routing {} to {}", JunboHttpContext.getRequestUri(), result);
        } else {
            // no routing, run in current server
            setOutputRoutingPath();
        }
        return result;
    }

    private String getTargetUrlInternal(Class<?> resourceClass, Object[] routingParams, boolean fallbackToAnyLocal) {
        boolean isFirstRoute = setRoutingPath();

        DataAccessPolicy policy = resolveDataAccessPolicy(resourceClass);

        if (policy == null || policy == DataAccessPolicy.CLOUDANT_ONLY) {
            return getDefault(false);
        }

        if (routingParams == null || routingParams.length == 0) {
            return getDefault(false);
        }

        if (isFirstRoute && forceRoute) {
            // force route to self
            return topology.getSelfUrl();
        }

        for (Object routingParam : routingParams) {
            if (routingParam != null) {
                return resolveRotingAddress(routingParam);
            }
        }

        if (fallbackToAnyLocal) {
            return getDefault(false);
        }

        // TODO: else throw exception? or deprecate the fallbackAnyLocal?
        return getDefault(false);
    }

    private boolean setRoutingPath() {
        boolean isFirstRoute = false;

        MultivaluedMap<String, String> requestHeaders = JunboHttpContext.getRequestHeaders();
        if (requestHeaders != null) {
            String routingPath = requestHeaders.getFirst(X_ROUTING_PATH);
            if (routingPath == null) {
                // first routing
                isFirstRoute = true;
                routingPath = "";
            }
            routingPath += "-> " + topology.getSelfUrl();

            int routingCount = StringUtils.countOccurrencesOf(routingPath, "->");
            if (routingCount > maxRoutingHops) {
                throw new RuntimeException("The API request hit max routing hops. " + JunboHttpContext.getRequestUri());
            }

            // put to requestHeaders so it can be forwarded when routing.
            requestHeaders.putSingle(X_ROUTING_PATH, routingPath);
        }
        return isFirstRoute;
    }

    private void setOutputRoutingPath() {
        MultivaluedMap<String, String> requestHeaders = JunboHttpContext.getRequestHeaders();
        if (requestHeaders != null) {
            String routingPath = requestHeaders.getFirst(X_ROUTING_PATH);
            // put to responseHeaders so the client can see it.
            if (showRoutingPath) {
                JunboHttpContext.getResponseHeaders().putSingle(X_ROUTING_PATH, routingPath);
            }
        }
    }

    private DataAccessPolicy resolveDataAccessPolicy(Class<?> resourceClass) {
        DataAccessPolicy policy = null;

        MultivaluedMap<String, String> requestHeaders = JunboHttpContext.getRequestHeaders();
        if (requestHeaders != null) {
            String dataAccessMode = requestHeaders.getFirst(X_DATAACCESS_MODE);
            if (dataAccessMode != null && dataAccessMode.length() != 0) {
                policy = Enum.valueOf(DataAccessPolicy.class, dataAccessMode);
                logger.debug("Forcing data access policy in call. url: {}, policy: {}", JunboHttpContext.getRequestUri(), policy);
            }
        }

        if (policy == null) {
            policy = DataAccessPolicies.instance().getHttpDataAccessPolicy(JunboHttpContext.getRequestMethod(), resourceClass);
        }
        if (policy == null) {
            policy = defaultPolicy;
        }
        if (logger.isDebugEnabled()) {
            logger.debug("Setting effective dataAccessPolicy in call. url: {}, policy: {}", JunboHttpContext.getRequestUri(), policy);
        }
        Context.get().setDataAccessPolicy(policy);
        return policy;
    }

    private String resolveRotingAddress(Object routingParam) {
        resolveShard(routingParam);
        return doRouting(false);
    }

    private String doRouting(boolean inGetDefault) {
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
        return getDefault(inGetDefault);
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

    private String getDefault(boolean inGetDefault) {
        if (inGetDefault) {
            return null;
        }

        MultivaluedMap<String, String> requestHeaders = JunboHttpContext.getRequestHeaders();
        if (requestHeaders != null) {
            String forceShard = requestHeaders.getFirst(X_ROUTING_DC_SHARD);
            if (forceShard == null || forceShard.length() == 0) {
                return null;
            }

            String[] dcShard = forceShard.split(";");
            if (dcShard.length != 2) {
                throw new RuntimeException("Invalid routing DC shard hint. " + forceShard);
            }
            String dc = dcShard[0];
            int shard = Integer.parseInt(dcShard[1]);

            // set to context and doRouting again!
            Context.get().setDataCenterId(DataCenters.instance().getDataCenter(dc).getId());
            Context.get().setShardId(shard);
            Context.get().setTopology(topology);

            return doRouting(true);
        }

        return null;
    }
}
