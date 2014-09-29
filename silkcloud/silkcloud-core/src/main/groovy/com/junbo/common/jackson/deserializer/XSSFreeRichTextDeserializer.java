/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.common.jackson.deserializer;

import org.jsoup.safety.Whitelist;

/**
 * XSSFreeRichTextDeserializer.
 */
public class XSSFreeRichTextDeserializer extends XSSFreeStringDeserializer {
    @Override
    protected Whitelist getWhitelist() {
        return Whitelist.basic();
    }
}
