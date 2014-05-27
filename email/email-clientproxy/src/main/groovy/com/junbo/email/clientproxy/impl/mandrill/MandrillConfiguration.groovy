/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.email.clientproxy.impl.mandrill

import groovy.transform.CompileStatic

/**
 * Configuration info of Mandrill.
 */
@CompileStatic
class MandrillConfiguration {
    String key
    String url
    Integer size
}
