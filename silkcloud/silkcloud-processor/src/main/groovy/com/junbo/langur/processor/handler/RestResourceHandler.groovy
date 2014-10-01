/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.langur.processor.handler

import javax.lang.model.element.TypeElement
/**
 * Created by kevingu on 11/28/13.
 */
interface RestResourceHandler {

    void handle(TypeElement mapperType, HandlerContext handlerContext)
}