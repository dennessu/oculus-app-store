/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.email.clientproxy.impl.mandrill;

/**
 * Status of Mandrill.
 */
public class SendStatus {
    private SendStatus() {}

    public static final String SENT = "sent";

    public static final String QUEUED = "queued";

    public static final String INVALID = "invalid";

    public static final String REJECTED = "rejected";

    public static final String SCHEDULED = "scheduled";
}
