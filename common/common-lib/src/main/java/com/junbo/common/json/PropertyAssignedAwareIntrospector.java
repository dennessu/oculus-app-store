package com.junbo.common.json;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.introspect.AnnotatedClass;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;

/**
 * Created by kg on 3/19/2014.
 */
public class PropertyAssignedAwareIntrospector extends JacksonAnnotationIntrospector {

    @Override
    public Object findFilterId(AnnotatedClass ac) {
        if (PropertyAssignedAware.class.isAssignableFrom(ac.getRawType())) {
            return PropertyAssignedAwareFilter.class.getName();
        }

        return null;
    }
}
