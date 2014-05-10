/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.common.util;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Data utilities.
 */
public class Context {
    private Context() { }

    private static final ThreadLocal<Data> context = new ThreadLocal<>();

    /**
     * The context of current API call.
     */
    public static class Data {
        private Date currentDate;
        private String currentUser;
        private String currentClient = "System";
        private String requestUri;
        private Map<String, String> headers = new HashMap<>();
        private Integer shardId;
        private Integer dataCenterId;

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

        public String getRequestUri() {
            return requestUri;
        }

        public void setRequestUri(String requestUri) {
            this.requestUri = requestUri;
        }

        public Map<String, String> getHeaders() {
            return headers;
        }

        public void setHeaders(Map<String, String> headers) {
            this.headers = headers;
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

        public String putHeader(String key, String value) {
            return this.headers.put(key, value);
        }

        public String getHeader(String key) {
            return this.headers.get(key);
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
