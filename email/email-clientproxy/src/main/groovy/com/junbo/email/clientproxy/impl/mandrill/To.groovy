/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.email.clientproxy.impl.mandrill

import groovy.transform.CompileStatic

/**
 * Recipient info of Mandrill request.
 */
@CompileStatic
class To {
    String email
    String name
    String type
}
