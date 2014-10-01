/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.langur.core.action;

import com.junbo.langur.core.promise.Promise;

/**
 * Java doc.
 */
public interface Action {

    Promise<ActionResult> execute(ActionContext context);
}
