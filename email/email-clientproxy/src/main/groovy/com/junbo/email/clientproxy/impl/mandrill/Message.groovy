/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.email.clientproxy.impl.mandrill

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Message object of Mandrill request.
 */
class Message {
    String subject

    @JsonProperty('from_email')
    String fromEmail

    @JsonProperty('from_name')
    String fromName

    @JsonProperty('to')
    To[] toList

    @JsonProperty('global_merge_vars')
    Map<String,String>[] properties
}
