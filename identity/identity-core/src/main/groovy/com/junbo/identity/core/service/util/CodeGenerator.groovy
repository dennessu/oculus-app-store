/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.core.service.util

/**
 * Created by liangfu on 5/6/14.
 */
interface CodeGenerator {
    String generateTeleCode()

    String generateTeleBackupCode()
}
