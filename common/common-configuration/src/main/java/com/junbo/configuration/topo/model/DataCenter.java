/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.configuration.topo.model;

/**
 * Data utilities.
 */
public class DataCenter {
    private String url;
    private int id;
    private String name;
    private int numberOfShard;

    public DataCenter(String url, int id, String name, int numberOfShard) {
        this.url = url;
        this.id = id;
        this.name = name;
        this.numberOfShard = numberOfShard;
    }

    public String getUrl() {
        return url;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getNumberOfShard() {
        return numberOfShard;
    }
}
