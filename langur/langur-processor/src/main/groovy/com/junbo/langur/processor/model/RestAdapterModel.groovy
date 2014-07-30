/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.langur.processor.model

import groovy.transform.CompileStatic

/**
 * Created by kevingu on 11/28/13.
 */
@CompileStatic
class RestAdapterModel {

    String packageName

    String className

    String adapteeName

    String adapteeType

    List<String> annotations

    List<RestMethodModel> restMethods

    boolean authorizationNotRequired
}
