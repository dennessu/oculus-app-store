/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.entity.user;

import com.junbo.common.util.Identifiable;
import com.junbo.identity.data.entity.common.ResourceMetaEntity;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by liangfu on 4/10/14.
 */
@Entity
@Table(name = "user_pii")
public class UserPiiEntity extends ResourceMetaEntity implements Identifiable<Long> {
    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "user_Id")
    private Long userId;

    @Column(name = "display_name_type")
    private Integer displayNameType;

    @Column(name = "gender")
    private String gender;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "birthday")
    private Date birthday;

    @Override
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

    public Integer getDisplayNameType() {
        return displayNameType;
    }

    public void setDisplayNameType(Integer displayNameType) {
        this.displayNameType = displayNameType;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }
}
