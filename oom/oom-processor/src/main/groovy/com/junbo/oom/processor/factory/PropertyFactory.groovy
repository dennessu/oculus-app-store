/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oom.processor.factory

import com.junbo.oom.processor.model.PropertyModel
import com.junbo.oom.processor.model.TypeModel
/**
 * Java doc.
 */
interface PropertyFactory {

    PropertyModel getProperty(TypeModel owner, String propertyName)
}
