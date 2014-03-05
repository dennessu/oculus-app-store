package com.goshop.langur.processor.model


class ClientMethodModel {

    String methodName

    String returnType

    List<ClientParameterModel> parameters

    String httpMethodName

    String path

    String contentType

    List<String> accepts
}
