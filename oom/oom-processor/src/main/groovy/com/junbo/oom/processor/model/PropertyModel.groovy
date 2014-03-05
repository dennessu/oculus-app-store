/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oom.processor.model

import groovy.transform.CompileStatic
/**
 * Java doc.
 */
@CompileStatic
class PropertyModel {

    TypeModel owner

    String name

    TypeModel type

    String getterString

    String setterString

    Set<TypeModel> getImportedTypes() {
        return (owner.importedTypes + type.importedTypes) as Set
    }
}
