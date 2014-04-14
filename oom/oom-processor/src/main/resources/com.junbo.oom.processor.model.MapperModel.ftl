[#-- @ftlvariable name="" type="com.junbo.oom.processor.model.MapperModel" --]


package ${packageName};

import com.junbo.oom.core.filter.ItemMappingEvent;
import com.junbo.oom.core.filter.ItemMappingFilter;
import com.junbo.oom.core.filter.PropertyMappingEvent;
import com.junbo.oom.core.filter.PropertyMappingFilter;

import java.util.*;

[#list importedTypes as importedType]
import ${importedType.qualifiedName};
[/#list]

@org.springframework.stereotype.Component("${packageName}.${implementationName}")
public class ${implementationName} implements ${interfaceName} {

[#list mapperRefs as mapperRef]

    [@includeModel model=mapperRef indent=true/]

[/#list]

[#list mappingMethods as mappingMethod]

    [@includeModel model=mappingMethod indent=true/]

[/#list]
}
