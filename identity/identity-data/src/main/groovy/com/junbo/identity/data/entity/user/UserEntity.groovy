/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.entity.user

import com.junbo.identity.data.entity.common.ResourceMetaEntity

import javax.persistence.*
/**
 * UserEntity model for user_device_profile table
 */
@Entity
@Table(name = 'user_account')
class UserEntity extends ResourceMetaEntity {

    @Id
    @Column(name = 'id')
    private Long id

    @Column(name = 'user_name')
    private String userName

    @Column(name = 'nick_name')
    private String nickName

    @Column(name = 'preferred_language')
    private String preferredLanguage

    @Column(name = 'locale')
    private String locale

    @Column(name = 'time_zone')
    private String timezone

    @Column(name = 'is_active')
    private Boolean active

    @Column(name = 'birthday')
    private String birthday

    @Column(name = 'gender')
    private String gender

    @Column(name = 'type')
    private String type

    Long getId() {
        return id
    }

    void setId(Long id) {
        this.id = id
    }

    String getUserName() {
        return userName
    }

    void setUserName(String userName) {
        this.userName = userName
    }

    String getNickName() {
        return nickName
    }

    void setNickName(String nickName) {
        this.nickName = nickName
    }

    String getPreferredLanguage() {
        return preferredLanguage
    }

    void setPreferredLanguage(String preferredLanguage) {
        this.preferredLanguage = preferredLanguage
    }

    String getLocale() {
        return locale
    }

    void setLocale(String locale) {
        this.locale = locale
    }

    String getTimezone() {
        return timezone
    }

    void setTimezone(String timezone) {
        this.timezone = timezone
    }

    Boolean getActive() {
        return active
    }

    void setActive(Boolean active) {
        this.active = active
    }

    String getBirthday() {
        return birthday
    }

    void setBirthday(String birthday) {
        this.birthday = birthday
    }

    String getGender() {
        return gender
    }

    void setGender(String gender) {
        this.gender = gender
    }

    String getType() {
        return type
    }

    void setType(String type) {
        this.type = type
    }
}
