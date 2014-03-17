/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.common.jackson;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.junbo.common.jackson.annotation.UserId;

import java.util.List;

/**
 * Flight.
 */
public class Flight {
    @UserId
    private Long hijackerId;

    @UserId
    private List<Long> passengers;

    public Long getHijackerId() {
        return hijackerId;
    }

    // the @JsonIgnore will not take effect
    // you need to mark @JsonIgnore on both field and setter method to take effect
    // please refer to <code>Order</code> class in test package for reference
    @JsonIgnore
    public void setHijackerId(Long hijackerId) {
        this.hijackerId = hijackerId;
    }

    public List<Long> getPassengers() {
        return passengers;
    }

    // same as <code>setHijackerId</code>
    @JsonIgnore
    public void setPassengers(List<Long> passengers) {
        this.passengers = passengers;
    }
}
