/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.csr.apihelper;

import com.junbo.csr.spec.model.CsrGroup;

import java.util.List;

/**
 * Created by weiyu_000 on 11/27/14.
 */
public interface GroupService {

    List<CsrGroup> getCsrGroups() throws Exception;

    List<CsrGroup> getCsrGroups(int expectedResponseCode) throws Exception;

    List<CsrGroup> getCsrGroups(String groupName, String uid) throws Exception;

    List<CsrGroup> getCsrGroups(String groupName, String uid, int expectedResponseCode) throws Exception;

}
