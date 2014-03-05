/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.entitlement.common.lib;

import java.util.Date;

/**
 * A wrapper of Promise Context.
 */
public class EntitlementContext {
    private static ThreadLocal<Date> now = new ThreadLocal<Date>();

    public static Date now() {
//        if(Promise.Context("now") == null){
//            setNow(new Date());
//        }
//        return Promise.currentContext().get("now");
        if (now.get() == null) {
            setNow(new Date());
        }
        return now.get();
    }

    public static void setNow(Date nowTime) {
//        Promise.currentContext().set("now", now);
        now.set(nowTime);
    }
//
//    public static Long requestorId(){
//        return Promise.currentContext().get("requestorId");
//    }
//
//    public static void setRequestorId(Long requestorId){
//        Promise.currentContext().set("requestorId", requestorId);
//    }
//
//    public static UUID trackingUuid(){
//        return Promise.currentContext().get("trackingUuid");
//    }
//
//    public static void setTrackingUuid(UUID trackingUuid){
//        Promise.currentContext().set("trackingUuid", trackingUuid);
//    }
}
