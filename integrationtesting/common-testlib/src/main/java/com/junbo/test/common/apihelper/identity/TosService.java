/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.common.apihelper.identity;

import com.junbo.identity.spec.v1.model.Tos;
import com.junbo.common.model.Results;
import com.junbo.common.id.TosId;

import java.util.HashMap;
import java.util.List;

/**
 * @author Jason
 * time 5/09/2014
 * Interface for TOS related API, including get/post/put/delete a TOS.
 */
public interface TosService {

    //Create a default TOS
    Tos postTos() throws Exception;
    //Create a TOS
    Tos postTos(Tos tos) throws Exception;
    Tos postTos(Tos tos, int expectedResponseCode) throws Exception;

    //Get a TOS info
    Tos getTos(TosId tosId) throws Exception;
    Tos getTos(TosId tosId, int expectedResponseCode) throws Exception;

    //Get TOS info list
    Results<Tos> getTosList(HashMap<String, List<String>> getOptions) throws Exception;
    Results<Tos> getTosList(HashMap<String, List<String>> getOptions, int expectedResponseCode) throws Exception;

    //Update a TOS
    Tos putTos(TosId tosId, Tos tos) throws Exception;
    Tos putTos(TosId tosId, Tos tos, int expectedResponseCode) throws Exception;

    //Delete a tos
    void deleteTos(TosId tosId) throws Exception;
    void deleteTos(TosId tosId, int expectedResponseCode) throws Exception;
}
