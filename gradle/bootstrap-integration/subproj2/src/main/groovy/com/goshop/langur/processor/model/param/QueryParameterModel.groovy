package com.goshop.langur.processor.model.param

import com.goshop.langur.processor.model.ClientParameterModel

class QueryParameterModel extends ClientParameterModel {

    String queryName

    @Override
    String getParameterType() {
        return "query"
    }
}
