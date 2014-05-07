/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.clientproxy.facebook

import com.fasterxml.jackson.annotation.JsonProperty
import groovy.transform.CompileStatic

/**
 * FacebookAccount.
 */
@CompileStatic
class FacebookAccount {
    String id;

    @JsonProperty("first_name")

    String firstName;

    @JsonProperty("last_name")
    String lastName;

    String name;

    String gender;
}
