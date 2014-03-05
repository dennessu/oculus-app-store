/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oom.processor.handler

import javax.lang.model.element.TypeElement
/**
 * Java doc.
 */
interface MapperHandler {

    void handle(TypeElement mapperType, HandlerContext handlerContext)

    int getSequenceNumber()

}