/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.v1.model;

/**
 * Created by liangfu on 4/26/14.
 */
public class UserSMS {

    private String textMessage;

    public String getTextMessage() {
        return textMessage;
    }

    public void setTextMessage(String textMessage) {
        this.textMessage = textMessage;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserSMS userSMS = (UserSMS) o;

        if (textMessage != null ? !textMessage.equals(userSMS.textMessage) : userSMS.textMessage != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return textMessage != null ? textMessage.hashCode() : 0;
    }
}
