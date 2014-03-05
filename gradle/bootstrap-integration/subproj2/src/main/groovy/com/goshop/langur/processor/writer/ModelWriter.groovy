package com.goshop.langur.processor.writer

interface ModelWriter {

    void write(Object model, Writer out)

    void write(Object model, Map<String, Object> params, Writer out)
}