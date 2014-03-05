package com.goshop.langur.processor.model.param

import com.goshop.langur.processor.model.ClientParameterModel

class HeaderParameterModel extends ClientParameterModel {

    String headerName

    @Override
    String getParameterType() {
        return "header"
    }
}
