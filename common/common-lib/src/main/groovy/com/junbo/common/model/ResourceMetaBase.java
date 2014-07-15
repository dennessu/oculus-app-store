/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.common.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.JsonNode;
import com.junbo.common.cloudant.CloudantEntity;
import com.junbo.common.cloudant.json.annotations.CloudantIgnore;
import com.junbo.common.cloudant.json.annotations.CloudantProperty;
import com.junbo.common.enumid.EnumId;
import com.junbo.common.id.CloudantId;
import com.junbo.common.id.Id;
import com.junbo.common.jackson.annotation.UserId;
import com.wordnik.swagger.annotations.ApiModelProperty;

import java.lang.reflect.ParameterizedType;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * The base class for all resource with system properties.
 */
public abstract class ResourceMetaBase<K> implements CloudantEntity<K> {

    // dummy field used to map getCloudantId/setCloudantId to _id
    @JsonIgnore
    @CloudantProperty("_id")
    private String cloudantId;

    @ApiModelProperty(position = 2001, required = true,
            value = "[Client Immutable] The created datetime of the resource.")
    private Date createdTime;

    @ApiModelProperty(position = 2002, required = true,
            value = "[Client Immutable] The updated datetime of the resource.")
    private Date updatedTime;

    @ApiModelProperty(position = 2003, required = false,
            value = "[Client Immutable] The created datetime of the resource.")
    @CloudantIgnore
    private AdminInfo adminInfo;

    @ApiModelProperty(position = 2004, required = false, value = "Feature expansion of the resource.")
    private Map<String, JsonNode> futureExpansion = new HashMap<>();

    // todo:    Liangfu:    Temporary allow this field to return
    // Will fix it later.
    //@JsonIgnore
    @UserId
    private Long createdBy;

    //@JsonIgnore
    @UserId
    private Long updatedBy;

    //@JsonIgnore
    private String createdByClient;

    //@JsonIgnore
    private String updatedByClient;

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

    public Date getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(Date updatedTime) {
        this.updatedTime = updatedTime;
    }

    public AdminInfo getAdminInfo() {
        return adminInfo;
    }

    public void setAdminInfo(AdminInfo adminInfo) {
        this.adminInfo = adminInfo;
    }

    public Long getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    public Long getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(Long updatedBy) {
        this.updatedBy = updatedBy;
    }

    public String getCreatedByClient() {
        return createdByClient;
    }

    public void setCreatedByClient(String createdByClient) {
        this.createdByClient = createdByClient;
    }

    public String getUpdatedByClient() {
        return updatedByClient;
    }

    public void setUpdatedByClient(String updatedByClient) {
        this.updatedByClient = updatedByClient;
    }

    public Map<String, JsonNode> getFutureExpansion() {
        return futureExpansion;
    }

    public void setFutureExpansion(Map<String, JsonNode> futureExpansion) {
        this.futureExpansion = futureExpansion;
    }


    private static enum KeyType {
        STRING,
        LONG,
        UUID,
        STRONG_TYPE_ID,
        STRONG_TYPE_ENUM_ID,
        STRONG_TYPE_CLOUDANT_ID
    }

    @JsonIgnore
    @CloudantIgnore
    private Class<K> keyClass;

    @JsonIgnore
    @CloudantIgnore
    private KeyType keyType;

    protected ResourceMetaBase() {
        Class clz = getClass();

        while (clz.getGenericSuperclass() instanceof Class) {
            clz = clz.getSuperclass();
        }

        keyClass = (Class<K>) ((ParameterizedType) clz.getGenericSuperclass()).getActualTypeArguments()[0];

        if (String.class.equals(keyClass)) {
            keyType = KeyType.STRING;
        } else if (Long.class.equals(keyClass)) {
            keyType = KeyType.LONG;
        } else if (UUID.class.equals(keyClass)) {
            keyType = KeyType.UUID;
        } else if (Id.class.isAssignableFrom(keyClass)) {
            keyType = KeyType.STRONG_TYPE_ID;
        } else if (EnumId.class.isAssignableFrom(keyClass)) {
            keyType = KeyType.STRONG_TYPE_ENUM_ID;
        } else if (CloudantId.class.isAssignableFrom(keyClass)) {
            keyType = KeyType.STRONG_TYPE_CLOUDANT_ID;
        } else {
            throw new RuntimeException("Unsupported id type: " + keyClass.getName());
        }
    }

    public abstract K getId();

    public abstract void setId(K id);

    @JsonIgnore
    @Override
    public String getCloudantId() {
        return this.cloudantId;
    }

    @JsonIgnore
    @Override
    @SuppressWarnings("unchecked")
    public void setCloudantId(String id) {
        this.cloudantId = id;
        if (id == null) {
            this.setId(null);
            return;
        }

        switch (keyType) {
            case STRING:
                this.setId((K)id);
                break;
            case LONG:
                this.setId((K)(Long)Long.parseLong(id));
                break;
            case UUID:
                this.setId((K)UUID.fromString(id));
                break;
            case STRONG_TYPE_ID:
                try {
                    Id strongTypeId = (Id)keyClass.newInstance();
                    strongTypeId.setCloudantId(id);
                    this.setId((K)strongTypeId);
                } catch (IllegalAccessException | InstantiationException ex) {
                    throw new RuntimeException("Cannot call ctor for type: " + keyClass.getName());
                }
                break;
            case STRONG_TYPE_ENUM_ID:
                try {
                    EnumId strongTypeId = (EnumId)keyClass.newInstance();
                    strongTypeId.setValue(id);
                    this.setId((K)strongTypeId);
                } catch (IllegalAccessException | InstantiationException ex) {
                    throw new RuntimeException("Cannot call ctor for type: " + keyClass.getName());
                }
                break;
            case STRONG_TYPE_CLOUDANT_ID:
                try {
                    CloudantId strongTypeId = (CloudantId)keyClass.newInstance();
                    strongTypeId.setValue(id);
                    this.setId((K)strongTypeId);
                } catch (IllegalAccessException | InstantiationException ex) {
                    throw new RuntimeException("Cannot call ctor for type: " + keyClass.getName());
                }
                break;
            default:
                throw new RuntimeException("Unknown key type: " + keyType);
        }
    }

}
