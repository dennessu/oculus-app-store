/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.store.spec.model.browse;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.junbo.common.id.TosId;
import com.junbo.common.id.UserTosAgreementId;
import com.junbo.common.userlog.EntityLoggable;

import java.util.Date;

/**
 * The AcceptTosResponse class.
 */
public class AcceptTosResponse implements EntityLoggable {

    private TosId tos;

    private Date acceptedTime;

    @JsonIgnore
    private UserTosAgreementId userTosAgreementId;

    public TosId getTos() {
        return tos;
    }

    public void setTos(TosId tos) {
        this.tos = tos;
    }

    public Date getAcceptedTime() {
        return acceptedTime;
    }

    public void setAcceptedTime(Date acceptedTime) {
        this.acceptedTime = acceptedTime;
    }

    public UserTosAgreementId getUserTosAgreementId() {
        return userTosAgreementId;
    }

    public void setUserTosAgreementId(UserTosAgreementId userTosAgreementId) {
        this.userTosAgreementId = userTosAgreementId;
    }

    @JsonIgnore
    @Override
    public String getEntityLogId() {
        return userTosAgreementId == null ? null : userTosAgreementId.toString();
    }
}
