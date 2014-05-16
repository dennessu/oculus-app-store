/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.authorization.test

import com.fasterxml.jackson.databind.ObjectMapper
import com.junbo.authorization.Entity
import com.junbo.authorization.EntityService
import com.junbo.authorization.MockHttpHeaders
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests
import org.testng.Assert
import org.testng.annotations.Test

/**
 * UnitTest.
 */
@CompileStatic
@ContextConfiguration(locations = "classpath:spring/context-test.xml")
class UnitTest extends AbstractTestNGSpringContextTests {
    @Autowired
    private EntityService entityService

    @Autowired
    private MockHttpHeaders httpHeaders

    @Test
    void testAspect() {
        ObjectMapper objectMapper = new ObjectMapper()

        /*
        httpHeaders.userId = 456L
        Entity entity = entityService.annotatedGet(123L)
        println objectMapper.writeValueAsString(entity)

        Assert.assertEquals(entity.id, 123L)
        Assert.assertNull(entity.name)
        Assert.assertNull(entity.createdBy)

        entity = entityService.annotatedGet(456L)
        println objectMapper.writeValueAsString(entity)

        Assert.assertEquals(entity.id, 456L)
        Assert.assertEquals(entity.name, 'name')
        Assert.assertNull(entity.createdBy)

        httpHeaders.userId = 123L
        entity = entityService.annotatedGet(456L)
        println objectMapper.writeValueAsString(entity)

        Assert.assertEquals(entity.id, 456L)
        Assert.assertEquals(entity.name, 'name')
        Assert.assertEquals(entity.createdBy, 'system')
        */
    }
}
