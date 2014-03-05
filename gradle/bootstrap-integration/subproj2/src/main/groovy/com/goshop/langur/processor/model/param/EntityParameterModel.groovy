package com.goshop.langur.processor.model.param

import com.goshop.langur.processor.model.ClientParameterModel

class EntityParameterModel extends ClientParameterModel {

    @Override
    String getParameterType() {
        return "entity"
    }
}
