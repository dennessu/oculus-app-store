/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.sharding.core.ds;

/**
 * Created by haomin on 14-3-10.
 */
public class ShardDataSourceKey {

    private int shardId;
    private String databaseName;

    public ShardDataSourceKey(int shardId, String databaseName) {
        this.shardId = shardId;
        this.databaseName = databaseName;
    }

    public void setShardId(int shardId) {
        this.shardId = shardId;
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    public int getShardId() {

        return shardId;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    @Override
    public String toString() {
        return String.format("Key[shardId=%s, databaseName=%s]", shardId, databaseName);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((databaseName == null) ? 0 : databaseName.hashCode());
        result = prime * result + shardId;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }

        ShardDataSourceKey other = (ShardDataSourceKey) obj;
        if (databaseName == null) {
            if (other.databaseName != null) {
                return false;
            }
        } else if (!databaseName.equals(other.databaseName)) {
            return false;
        }
        if (shardId != other.shardId) {
            return false;
        }

        return true;
    }
}
