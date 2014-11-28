/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.csr.apihelper;

import com.junbo.csr.spec.model.CsrUpdate;

import java.util.List;

/**
 * Created by weiyu_000 on 11/28/14.
 */
public interface UpdateService {

    CsrUpdate createUpdate(String uid,String content) throws Exception;

    CsrUpdate createUpdate(String uid, String content, int expectedResponseCode) throws Exception;

    CsrUpdate putUpdate(String updateId, CsrUpdate csrUpdate) throws Exception;

    CsrUpdate putUpdate(String updateId, CsrUpdate csrUpdate, int expectedResponseCode) throws Exception;

    CsrUpdate patchUpdate(String updateId, String uid, String content) throws Exception;

    CsrUpdate patchUpdate(String updatedId,String uid, String content, int expectedResponseCode) throws Exception;

    CsrUpdate getUpdate(String updateId) throws Exception;

    CsrUpdate getUpdate(String updateId, int expectedResponseCode) throws Exception;

    List<CsrUpdate> getUpdateList(boolean isActive) throws Exception;

    List<CsrUpdate> getUpdateList(boolean isActive, int expectedResponseCode) throws Exception;

    boolean deleteUpdate(String updateId) throws Exception;

    boolean deleteUpdate(String updateId, int expectedResponseCode) throws Exception;

}
