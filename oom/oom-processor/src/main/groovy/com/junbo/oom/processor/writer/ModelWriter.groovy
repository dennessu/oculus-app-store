/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oom.processor.writer
/**
 * Java doc.
 */
interface ModelWriter {

    void write(Object model, Writer out)

    void write(Object model, Map<String, Object> params, Writer out)
}