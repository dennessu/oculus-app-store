/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.langur.processor.model

/**
 * Created by kevingu on 11/28/13.
 */
class RestAdapterModel {

    String packageName

    String className

    String adapteeName

    String adapteeType

    List<String> annotations

    List<RestMethodModel> restMethods
}
