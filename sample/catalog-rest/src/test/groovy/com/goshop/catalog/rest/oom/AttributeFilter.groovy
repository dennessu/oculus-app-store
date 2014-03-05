package com.goshop.catalog.rest.oom


import com.goshop.oom.core.MappingContext
import com.goshop.oom.core.filter.ElementMappingEvent
import com.goshop.oom.core.filter.ElementMappingFilter
/**
 * Created by kevingu on 11/20/13.
 */
class AttributeFilter implements ElementMappingFilter {

    @Override
    boolean skipElementMapping(ElementMappingEvent event, MappingContext context) {
        if (event.getSourceElementType() == ECategory.class) {
          /*  def category = (ECategory) event.getSourceElement()
            if (category.name == "name2") {
                return true;
            }*/
        }

        return false
    }

    @Override
    void beginElementMapping(ElementMappingEvent event, MappingContext context) {

    }

    @Override
    void endElementMapping(ElementMappingEvent event, MappingContext context) {

    }
}
