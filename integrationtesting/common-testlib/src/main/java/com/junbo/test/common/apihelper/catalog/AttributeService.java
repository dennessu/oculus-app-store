/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.common.apihelper.catalog;

import com.junbo.catalog.spec.model.attribute.Attribute;
import java.util.HashMap;
import java.util.List;

/**
 @author Jason
  * Time: 3/21/2014
  * The interface for Attribute related APIs
 */
public interface AttributeService {

    String getAttribute(String attributeId) throws Exception;
    String getAttribute(String attributeId, int expectedResponseCode) throws Exception;

    List<String> getAttribute(HashMap<String, String> httpPara) throws Exception;
    List<String> getAttribute(HashMap<String, String> httpPara, int expectedResponseCode) throws Exception;

    String postAttribute(Attribute attribute) throws Exception;
    String postAttribute(Attribute attribute, int expectedResponseCode) throws Exception;
}
