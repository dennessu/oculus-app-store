/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oom.processor.factory
import com.junbo.oom.processor.model.TypeModel

import javax.lang.model.type.TypeMirror
/**
 * Java doc.
 */
interface TypeFactory {

    TypeModel getType(TypeMirror typeMirror)
}