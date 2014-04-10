

package com.junbo.oom.processor.mapper;

import com.junbo.oom.core.filter.ElementMappingEvent;
import com.junbo.oom.core.filter.ElementMappingFilter;
import com.junbo.oom.core.filter.PropertyMappingEvent;
import com.junbo.oom.core.filter.PropertyMappingFilter;

import com.junbo.oom.core.builtin.BuiltinMapper;
import com.junbo.oom.processor.mapper.CommonMapper;
import java.util.List;
import java.util.ArrayList;
import com.junbo.oom.processor.model.Item1;
import com.junbo.oom.core.MappingContext;
import java.util.List;
import java.util.ArrayList;
import com.junbo.oom.processor.model.Item2;
import java.lang.String;
import java.lang.Long;

@org.springframework.stereotype.Component("com.junbo.oom.processor.mapper.SimpleMapperImpl")
public class SimpleMapperImpl implements SimpleMapper {


    @org.springframework.beans.factory.annotation.Autowired
    public BuiltinMapper __builtinMapper;

    @org.springframework.beans.factory.annotation.Autowired
    public CommonMapper __commonMapper;


    
    public List<Item2> fromItem1toItem2(List<Item1> source, MappingContext mappingContext) {
    
        if (source == null) {
            return null;
        }
    
        if (mappingContext.getSkipMapping() == Boolean.TRUE) {
            return null;
        }
    
    
        List<Item2> __result = new ArrayList<Item2>();
    
        for (Item1 __sourceItem : source) {
    
    
            ElementMappingFilter __filter = mappingContext.getElementMappingFilter();
            if (__filter == null) {
    
                Item2 __targetItem =
                    fromItem1toItem2_single(__sourceItem, mappingContext);
    
                __result.add(__targetItem);
    
            } else {
                ElementMappingEvent __event = new ElementMappingEvent();
    
                __event.setSourceElementType(Item1.class);
                __event.setSourceElement(__sourceItem);
                __event.setTargetElementType(Item2.class);
    
                boolean __skipped = __filter.skipElementMapping(__event, mappingContext);
    
                if (!__skipped) {
                    __filter.beginElementMapping(__event, mappingContext);
    
                    Item2 __targetItem =
                        fromItem1toItem2_single(__sourceItem, mappingContext);
    
                    __result.add(__targetItem);
    
                    __filter.endElementMapping(__event, mappingContext);
                }
            }
    
        }
    
        return __result;
    }
    

    
    public Item1 fromItem2toItem1_single(Item2 source, MappingContext mappingContext) {
    
        if (source == null) {
            return null;
        }
    
        if (mappingContext.getSkipMapping() == Boolean.TRUE) {
            return null;
        }
    
        Item1 __result = new Item1();
    
        {
        
            String __sourceProperty = source.getTarget1();
        
            PropertyMappingFilter __filter = mappingContext.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setSource1(__commonMapper.explicitMethod_toString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(Item2.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("target1");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(Item1.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("source1");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, mappingContext);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, mappingContext);
        
                    __result.setSource1(__commonMapper.explicitMethod_toString(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, mappingContext);
                }
            }
        
        }
            {
        
            String __sourceProperty = source.getProp1();
        
            PropertyMappingFilter __filter = mappingContext.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setProp1(__commonMapper.explicitMethod_toString2(__sourceProperty));
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
        
                    __result.setProp1(__commonMapper.explicitMethod_toString2(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, mappingContext);
                }
            }
        
        }
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
        
            String __sourceProperty = source.getProp2();
        
            PropertyMappingFilter __filter = mappingContext.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setProp2(__commonMapper.explicitMethod_toString2(__sourceProperty));
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
        
                    __result.setProp2(__commonMapper.explicitMethod_toString2(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, mappingContext);
                }
            }
        
        }
        
        return __result;
    }

    
    public Item2 fromItem1toItem2_single(Item1 source, MappingContext mappingContext) {
    
        if (source == null) {
            return null;
        }
    
        if (mappingContext.getSkipMapping() == Boolean.TRUE) {
            return null;
        }
    
        Item2 __result = new Item2();
    
        {
        
            String __sourceProperty = source.getSource1();
        
            PropertyMappingFilter __filter = mappingContext.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setTarget1(__commonMapper.explicitMethod_toString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(Item1.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("source1");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(Item2.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("target1");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, mappingContext);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, mappingContext);
        
                    __result.setTarget1(__commonMapper.explicitMethod_toString(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, mappingContext);
                }
            }
        
        }
            {
        
            String __sourceProperty = source.getProp1();
        
            PropertyMappingFilter __filter = mappingContext.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setProp1(__commonMapper.explicitMethod_toString2(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(Item1.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("prop1");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(Item2.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("prop1");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, mappingContext);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, mappingContext);
        
                    __result.setProp1(__commonMapper.explicitMethod_toString2(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, mappingContext);
                }
            }
        
        }
            {
        
            Long __sourceProperty = source.getField1();
        
            PropertyMappingFilter __filter = mappingContext.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setField1(__builtinMapper.fromLongToLong(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(Item1.class);
                __event.setSourcePropertyType(Long.class);
                __event.setSourcePropertyName("field1");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(Item2.class);
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
        
            String __sourceProperty = source.getProp2();
        
            PropertyMappingFilter __filter = mappingContext.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setProp2(__commonMapper.explicitMethod_toString2(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(Item1.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("prop2");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(Item2.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("prop2");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, mappingContext);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, mappingContext);
        
                    __result.setProp2(__commonMapper.explicitMethod_toString2(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, mappingContext);
                }
            }
        
        }
        
        return __result;
    }
}
