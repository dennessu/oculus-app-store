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
    private String shardIdRange;
    private String databaseName;
    private String loginRole;

    public ShardDataSourceKey(String shardIdRange, String databaseName, String loginRole) {
        this.shardIdRange = shardIdRange;
        this.databaseName = databaseName;
        this.loginRole = loginRole;
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public String getLoginRole() {
        return loginRole;
    }

    public void setLoginRole(String loginRole) {
        this.loginRole = loginRole;
    }

    public String getShardIdRange() {
        return shardIdRange;
    }

    public void setShardIdRange(String shardIdRange) {
        this.shardIdRange = shardIdRange;
    }

    @Override
    public String toString() {
        return String.format("Key[shardIdRange=%s, databaseName=%s, loginRole=%s]",
                shardIdRange, databaseName, loginRole);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 0;
        result = prime * result + ((databaseName == null) ? 0 : databaseName.hashCode());
        result = prime * result + ((loginRole == null) ? 0 : loginRole.hashCode());
        result = prime * result + ((shardIdRange == null) ? 0 : shardIdRange.hashCode());
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
        else if (loginRole == null) {
            if (other.loginRole != null) {
                return false;
            }
        }
        else if (!loginRole.equals(other.loginRole)) {
            return false;
        }
        else if (shardIdRange == null) {
            if (other.loginRole != null) {
                return false;
            }
        }
        else if (!shardIdRange.equals(other.shardIdRange)) {
            return false;
        }

        return true;
    }
}
