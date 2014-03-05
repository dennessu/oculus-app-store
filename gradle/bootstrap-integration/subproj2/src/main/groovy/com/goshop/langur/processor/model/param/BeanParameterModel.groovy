package com.goshop.langur.processor.model.param

import com.goshop.langur.processor.model.ClientParameterModel

class BeanParameterModel extends ClientParameterModel {

    List<ClientParameterModel> parameters

    @Override
    String getParameterType() {
        return "bean"
    }
}
