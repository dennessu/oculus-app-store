package com.junbo.identity.data.entity.user;

import com.junbo.identity.data.entity.common.ResourceMetaEntity;
import com.junbo.sharding.annotations.SeedId;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * UserEntity model for user_device_profile table
 */
@Entity
@Table(name = "user_tos")
public class UserTosEntity extends ResourceMetaEntity {

    @Id
    @Column(name = "id")
    private Long id;

    @SeedId
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "tos_uri")
    private String tosUri;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getTosUri() {
        return tosUri;
    }

    public void setTosUri(String tosUri) {
        this.tosUri = tosUri;
    }

}
