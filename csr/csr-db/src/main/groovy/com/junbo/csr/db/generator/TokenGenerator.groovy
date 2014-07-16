/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.csr.db.generator

import groovy.transform.CompileStatic

/**
 * Javadoc.
 */
@CompileStatic
interface TokenGenerator {
    String generateCsrInvitationCode()
    boolean isCsrInvitationCode(String codeValue)
}
