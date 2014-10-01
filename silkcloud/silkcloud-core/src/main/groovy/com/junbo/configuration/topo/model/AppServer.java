/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.configuration.topo.model;

/**
 * Data utilities.
 */
public class AppServer {
    private String ipAddress;
    private int port;
    private String ipPort;
    private int[] shards;
    private int dcId;

    public AppServer(String ipAddress, int port, int[] shards, int dcId) {
        this.ipAddress = ipAddress;
        this.port = port;
        this.ipPort = ipAddress + ":" + port;
        this.shards = shards;
        this.dcId = dcId;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public int getPort() {
        return port;
    }

    public String getIpPort() {
        return ipPort;
    }

    public int[] getShards() {
        return shards;
    }

    public int getDcId() {
        return dcId;
    }
}
