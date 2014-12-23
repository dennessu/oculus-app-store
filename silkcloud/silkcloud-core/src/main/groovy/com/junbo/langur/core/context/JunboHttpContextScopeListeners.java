// CHECKSTYLE:OFF

/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.langur.core.context;

import java.util.List;

/**
 * The class is created to work around the following issues:
 * 1. When using @Autowried to inject a list of JunboHttpContextScope, the bean defined using util:list cannot be injected.
 * 2. When using @Resource to inject a list of JunboHttpContextScope, the bean with prototype scope won't get the injection.
 * So we use this class to delegate the ArrayList.
 */
public class JunboHttpContextScopeListeners {
    private List<JunboHttpContextScopeListener> list;

    public List<JunboHttpContextScopeListener> getList() {
        return list;
    }

    public void setList(List<JunboHttpContextScopeListener> list) {
        this.list = list;
    }
}
