package com.goshop.langur.processor.model.param

import com.goshop.langur.processor.model.ClientParameterModel

class PathParameterModel extends ClientParameterModel {

    String pathName

    @Override
    String getParameterType() {
        return "path"
    }
}
