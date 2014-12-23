/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.csr.apihelper;

import com.junbo.csr.spec.def.SearchType;

import java.util.List;

/**
 * Created by weiyu_000 on 11/26/14.
 */
public interface SearchService {
    List<String> searchUsers(SearchType type,String value, int expectedResponseCode) throws Exception;
    List<String> searchUsers(SearchType type,String value) throws Exception;

}
