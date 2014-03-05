package com.junbo.subscription.db.entity;

import com.junbo.subscription.db.entity.Entity;
import com.junbo.subscription.spec.model.Subscription;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.Date;

@javax.persistence.Entity
@Table(name = "SUBSCRIPTION")
public class SubscriptionEntity extends Entity {
    private Long subscriptionId;
    private Long userId;
    private String type;
    private String status;

    @Id
    @Column(name = "subscription_Id")
    public Long getSubscriptionId() {
        return subscriptionId;
    }

    public void setSubscriptionId(Long subscriptionId) {
        this.subscriptionId = subscriptionId;
    }

    @Column(name = "user_id")
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    @Column(name = "type")
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Column(name = "status")
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Transient
    @Override
    public Long getId() {
        return this.subscriptionId;
    }

    @Override
    public void setId(Long id) {
        this.subscriptionId = id;
    }
}
