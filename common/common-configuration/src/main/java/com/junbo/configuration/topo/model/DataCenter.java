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

    public DataCenter(String url, int id, String name) {
        this.url = url;
        this.id = id;
        this.name = name;
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
}
