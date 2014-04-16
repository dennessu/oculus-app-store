package com.junbo.common.error;

import java.util.List;

/**
 * Created by Shenhua on 4/14/2014.
 */
public class Error {
    public Error() {
    }

    public Error(String code, String description, String field, List<Error> causes) {
        this.code = code;
        this.description = description;
        this.field = field;
        this.causes = causes;
    }

    private String code;

    private String description;

    private String field;

    private List<Error> causes;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public List<Error> getCauses() {
        return causes;
    }

    public void setCauses(List<Error> causes) {
        this.causes = causes;
    }
}

