/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.common.error;

import java.util.List;

/**
 * Interface for Error.
 */
public class Error {

    public Error(String code, String description, String field, List<Error> causes) {
        this.code = code;
        this.description = description;
        this.field = field;
        this.causes = causes;
    }

    private final String code;

    private final String description;

    private final String field;

    private final List<Error> causes;

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public String getField() {
        return field;
    }

    public List<Error> getCauses() {
        return causes;
    }
}
