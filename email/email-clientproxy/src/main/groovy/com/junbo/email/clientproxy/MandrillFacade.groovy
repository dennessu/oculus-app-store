/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.email.clientproxy

import com.junbo.email.clientproxy.impl.mandrill.MandrillResponse
import com.junbo.email.spec.model.Email
import com.junbo.langur.core.promise.Promise

/**
 * Interface of Mandrill Facade
 */
interface MandrillFacade {
    Promise<MandrillResponse> send(Email email)
}