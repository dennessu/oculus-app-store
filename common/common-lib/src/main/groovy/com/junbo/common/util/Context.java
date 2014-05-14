/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.common.util;

import com.junbo.common.routing.model.DataAccessPolicy;
import com.junbo.configuration.topo.Topology;

import javax.ws.rs.container.ContainerRequestContext;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Data utilities.
 */
public class Context {
    private Context() { }

    private static final ThreadLocal<Data> context = new ThreadLocal<>();

    public static final String X_REQUEST_ID = "x-request-id";
    public static final String X_ROUTING_HOPS = "x-routing-hops";

    /**
     * The context of current API call.
     */
    public static class Data {
        private Date currentDate;
        private String currentUser;
        private String currentClient = "System";
        private ContainerRequestContext requestContext;
        private Integer shardId;
        private Integer dataCenterId;
        private Topology topology;
        private DataAccessPolicy dataAccessPolicy;

        public Date getCurrentDate() {
            if (currentDate == null) {
                return new Date();
            }
            return currentDate;
        }

        public void setCurrentDate(Date currentDate) {
            this.currentDate = currentDate;
        }

        public String getCurrentUser() {
            return currentUser;
        }

        public void setCurrentUser(String currentUser) {
            this.currentUser = currentUser;
        }

        public String getCurrentClient() {
            return currentClient;
        }

        public void setCurrentClient(String currentClient) {
            this.currentClient = currentClient;
        }

        public ContainerRequestContext getRequestContext() {
            return requestContext;
        }

        public void setRequestContext(ContainerRequestContext requestContext) {
            this.requestContext = requestContext;
        }

        public Integer getShardId() {
            return shardId;
        }

        public void setShardId(Integer shardId) {
            this.shardId = shardId;
        }

        public Integer getDataCenterId() {
            return dataCenterId;
        }

        public void setDataCenterId(Integer dataCenterId) {
            this.dataCenterId = dataCenterId;
        }

        public Topology getTopology() {
            return topology;
        }

        public void setTopology(Topology topology) {
            this.topology = topology;
        }

        public String getHeader(String key) {
            if (this.requestContext == null) {
                return null;
            }
            return this.requestContext.getHeaders().getFirst(key);
        }

        public List<String> getHeaderValues(String key) {
            if (this.requestContext == null) {
                return new ArrayList<>();
            }
            return this.requestContext.getHeaders().get(key);
        }

        public String getRequestId() {
            return getHeader(X_REQUEST_ID);
        }

        public String getHttpMethod() {
            if (this.requestContext == null) {
                throw new RuntimeException("HttpMethod not available.");
            }
            return this.requestContext.getMethod();
        }

        public DataAccessPolicy getDataAccessPolicy() {
            return dataAccessPolicy;
        }

        public void setDataAccessPolicy(DataAccessPolicy dataAccessPolicy) {
            this.dataAccessPolicy = dataAccessPolicy;
        }
    }

    public static Data get() {
        if (context.get() == null) {
            context.set(new Data());
        }
        return context.get();
    }

    public static void clear() {
        context.set(null);
    }
}
