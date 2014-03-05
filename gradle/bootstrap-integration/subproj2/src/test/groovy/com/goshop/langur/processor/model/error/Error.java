package com.goshop.langur.processor.model.error;

import java.util.List;

public class Error {

    private String code;

    private String message;

    private String field;

    private List<Error> causes;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
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
