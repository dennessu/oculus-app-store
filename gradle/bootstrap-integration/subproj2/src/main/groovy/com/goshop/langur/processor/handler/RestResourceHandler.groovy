package com.goshop.langur.processor.handler

import javax.lang.model.element.TypeElement

public interface RestResourceHandler {

    void handle(TypeElement mapperType, HandlerContext handlerContext)
}