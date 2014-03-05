/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.langur.processor.model

/**
 * Created by kevingu on 11/28/13.
 */
class ClientMethodModel {

    String methodName

    String returnType

    List<ClientParameterModel> parameters

    String httpMethodName

    String path

    String contentType

    List<String> accepts
}
