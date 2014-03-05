package com.goshop.langur.processor.model


class RestAdapterModel {

    String packageName

    String className

    String adapteeName

    String adapteeType

    List<String> annotations

    List<RestMethodModel> restMethods
}
