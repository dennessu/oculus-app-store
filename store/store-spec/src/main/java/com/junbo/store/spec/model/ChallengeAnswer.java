/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.store.spec.model;

import com.junbo.common.id.TosId;

/**
 * The ChallengeAnswer class.
 */
public class ChallengeAnswer {

    private String type;

    private String pin;

    private String password;

    private TosId acceptedTos;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public TosId getAcceptedTos() {
        return acceptedTos;
    }

    public void setAcceptedTos(TosId acceptedTos) {
        this.acceptedTos = acceptedTos;
    }
}
