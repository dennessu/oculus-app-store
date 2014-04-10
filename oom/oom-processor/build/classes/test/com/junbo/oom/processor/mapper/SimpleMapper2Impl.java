

package com.junbo.oom.processor.mapper;

import com.junbo.oom.core.filter.ElementMappingEvent;
import com.junbo.oom.core.filter.ElementMappingFilter;
import com.junbo.oom.core.filter.PropertyMappingEvent;
import com.junbo.oom.core.filter.PropertyMappingFilter;

import com.junbo.oom.core.builtin.BuiltinMapper;
import java.util.Map;
import java.util.HashMap;
import java.lang.String;
import com.junbo.oom.processor.model.Item2;
import com.junbo.oom.core.MappingContext;
import java.util.Map;
import java.util.HashMap;
import com.junbo.oom.processor.model.Item1;
import java.lang.Long;
import java.util.List;
import java.util.ArrayList;
import java.util.List;
import java.util.ArrayList;

@org.springframework.stereotype.Component("com.junbo.oom.processor.mapper.SimpleMapper2Impl")
public class SimpleMapper2Impl implements SimpleMapper2 {


    @org.springframework.beans.factory.annotation.Autowired
    public BuiltinMapper __builtinMapper;


    
    public Map<String, Item1> fromItem2stoItem1s(Map<String, Item2> source, MappingContext mappingContext) {
    
        if (source == null) {
            return null;
        }
    
        if (mappingContext.getSkipMapping() == Boolean.TRUE) {
            return null;
        }
    
        Map<String, Item1> __result = new HashMap<String, Item1>();
    
        for (Map.Entry<String, Item2> __entry : source.entrySet()) {
    
            String __key =
                __builtinMapper.fromStringToString(__entry.getKey());
    
            PropertyMappingFilter __filter = mappingContext.getPropertyMappingFilter();
            if (__filter == null) {
    
                Item1 __value;
                __value = __fromItem2ToItem1(__entry.getValue(), mappingContext);
                __result.put(__key, __value);
    
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
    
                __event.setSourceType(HashMap.class);
                __event.setSourcePropertyType(Item2.class);
                __event.setSourcePropertyName(__entry.getKey().toString());
                __event.setSourceProperty(__entry.getValue());
    
                __event.setTargetType(HashMap.class);
                __event.setTargetPropertyType(Item1.class);
                __event.setTargetPropertyName(__key.toString());
    
                boolean __skipped = __filter.skipPropertyMapping(__event, mappingContext);
    
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, mappingContext);
    
                    Item1 __value;
                    __value = __fromItem2ToItem1(__entry.getValue(), mappingContext);
                    __result.put(__key, __value);
    
                    __filter.endPropertyMapping(__event, mappingContext);
                }
            }
    
        }
    
        return __result;
    }
    
    

    
    public Item1 __fromItem2ToItem1(Item2 source, MappingContext mappingContext) {
    
        if (source == null) {
            return null;
        }
    
        if (mappingContext.getSkipMapping() == Boolean.TRUE) {
            return null;
        }
    
        Item1 __result = new Item1();
    
        {
        
            Long __sourceProperty = source.getField1();
        
            PropertyMappingFilter __filter = mappingContext.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setField1(__builtinMapper.fromLongToLong(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(Item2.class);
                __event.setSourcePropertyType(Long.class);
                __event.setSourcePropertyName("field1");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(Item1.class);
                __event.setTargetPropertyType(Long.class);
                __event.setTargetPropertyName("field1");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, mappingContext);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, mappingContext);
        
                    __result.setField1(__builtinMapper.fromLongToLong(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, mappingContext);
                }
            }
        
        }
            {
        
            String __sourceProperty = source.getProp1();
        
            PropertyMappingFilter __filter = mappingContext.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setProp1(__builtinMapper.fromStringToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(Item2.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("prop1");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(Item1.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("prop1");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, mappingContext);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, mappingContext);
        
                    __result.setProp1(__builtinMapper.fromStringToString(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, mappingContext);
                }
            }
        
        }
            {
        
            String __sourceProperty = source.getProp2();
        
            PropertyMappingFilter __filter = mappingContext.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setProp2(__builtinMapper.fromStringToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(Item2.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("prop2");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(Item1.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("prop2");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, mappingContext);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, mappingContext);
        
                    __result.setProp2(__builtinMapper.fromStringToString(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, mappingContext);
                }
            }
        
        }
        
        return __result;
    }

    
    public List<Item1> fromItem2toItem1(List<Item2> source, MappingContext mappingContext) {
    
        if (source == null) {
            return null;
        }
    
        if (mappingContext.getSkipMapping() == Boolean.TRUE) {
            return null;
        }
    
    
        List<Item1> __result = new ArrayList<Item1>();
    
        for (Item2 __sourceItem : source) {
    
    
            ElementMappingFilter __filter = mappingContext.getElementMappingFilter();
            if (__filter == null) {
    
                Item1 __targetItem =
                    __fromItem2ToItem1(__sourceItem, mappingContext);
    
                __result.add(__targetItem);
    
            } else {
                ElementMappingEvent __event = new ElementMappingEvent();
    
                __event.setSourceElementType(Item2.class);
                __event.setSourceElement(__sourceItem);
                __event.setTargetElementType(Item1.class);
    
                boolean __skipped = __filter.skipElementMapping(__event, mappingContext);
    
                if (!__skipped) {
                    __filter.beginElementMapping(__event, mappingContext);
    
                    Item1 __targetItem =
                        __fromItem2ToItem1(__sourceItem, mappingContext);
    
                    __result.add(__targetItem);
    
                    __filter.endElementMapping(__event, mappingContext);
                }
            }
    
        }
    
        return __result;
    }
    
}
