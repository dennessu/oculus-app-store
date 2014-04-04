/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.sharding.id.impl

import groovy.transform.CompileStatic

/**
 * Java doc for OculusIdGeneratorSlot.
 */
@CompileStatic
interface IdGeneratorSlot {

    long nextId()
}
