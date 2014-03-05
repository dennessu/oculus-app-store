package com.goshop.catalog.db.entity;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

/**
 * Created by kevingu on 11/21/13.
 */
public abstract class Entity<K extends EntityId> {
    @Id
    private ObjectId uniqueId;  // This unique Id is to mark the corresponding id

    @Field("_entityId")
    private K entityId;

    @Field("_version")
    private Long version;

    @Field("_status")
    private String status;

    @Field("_createdDate")
    private Date createdDate;

    @Field("_createdBy")
    private String createdBy;

    @Field("_modifiedDate")
    private Date modifiedDate;

    @Field("_modifiedBy")
    private String modifiedBy;

    public ObjectId getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(ObjectId uniqueId) {
        this.uniqueId = uniqueId;
    }

    public K getEntityId() {
        return entityId;
    }

    public void setEntityId(K id) {
        this.entityId = id;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Date getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(Date modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    public String getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    public abstract String getEntityType();
}
