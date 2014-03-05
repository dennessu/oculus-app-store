/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oom.processor.source

import groovy.transform.CompileStatic
/**
 * Java doc.
 */
@CompileStatic
class MappingPropertyInfo {

    String source

    String target

    boolean excluded

    boolean bidirectional

    String explicitMethodName
}