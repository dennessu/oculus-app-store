package com.goshop.langur.processor.model

abstract class ClientParameterModel {

    String paramType

    String paramName

    Object defaultValue

    abstract String getParameterType();
}
