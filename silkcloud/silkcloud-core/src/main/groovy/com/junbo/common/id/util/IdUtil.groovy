package com.junbo.common.id.util
import com.junbo.common.enumid.EnumId
import com.junbo.common.id.CloudantId
import com.junbo.common.id.Id
import com.junbo.common.id.IdResourcePath
import com.junbo.common.id.UniversalId
import com.junbo.common.model.Link
import com.junbo.common.util.IdFormatter
import com.junbo.common.util.Utils
import com.junbo.configuration.ConfigService
import com.junbo.configuration.ConfigServiceManager
import groovy.transform.CompileStatic
import org.springframework.beans.factory.config.BeanDefinition
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider
import org.springframework.core.annotation.AnnotationUtils
import org.springframework.core.type.filter.AssignableTypeFilter

import java.util.regex.Matcher
import java.util.regex.Pattern
/**
 * IdUtil.
 */
@CompileStatic
class IdUtil {

    public static final List<Class> ID_CLASSES = []

    public static final List<Class> CLOUDANT_ID_CLASSES = []

    public static final List<Class> ENUM_ID_CLASSES = []

    private static final Map<Class, Pattern> RESOURCE_PATH_PATTERNS = [:]
    private static String resourcePathPrefix = 'v1'
    private static final Map<Class, String> RESOURCE_PATHS = [:]
    private static final Map<Class, String> RESOURCE_TYPES = [:]

    static {
        ClassPathScanningCandidateComponentProvider provider = new ClassPathScanningCandidateComponentProvider(true);

        provider.addIncludeFilter(new AssignableTypeFilter(Id));
        provider.addIncludeFilter(new AssignableTypeFilter(CloudantId));
        // scan in com.junbo.common.id package for SubClass of Id or CloudantId
        Set<BeanDefinition> idDefinitions = provider.findCandidateComponents('com/junbo/common/id');
        for (BeanDefinition definition : idDefinitions) {
            try {
                Class cls = IdUtil.classLoader.loadClass(definition.beanClassName);
                if (Id.isAssignableFrom(cls)) {
                    ID_CLASSES.add(cls)
                } else if (CloudantId.isAssignableFrom(cls)) {
                    CLOUDANT_ID_CLASSES.add(cls)
                } else {
                    continue;
                }
                IdResourcePath pathAnno = AnnotationUtils.findAnnotation(cls, IdResourcePath);
                if (pathAnno != null) {
                    String regex = pathAnno.regex()

                    if (!regex.startsWith('^')) {
                        regex = '^' + regex
                    }

                    if (!regex.endsWith('$')) {
                        regex += '$'
                    }
                    RESOURCE_PATH_PATTERNS[cls] = Pattern.compile(regex)

                    RESOURCE_PATHS[cls] = pathAnno.value()

                    RESOURCE_TYPES[cls] = pathAnno.resourceType()
                }
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }

        provider.addIncludeFilter(new AssignableTypeFilter(EnumId));
        // scan in com.junbo.common.enumid package for Subclass of EnumId
        Set<BeanDefinition> enumIdDefinitions = provider.findCandidateComponents('com/junbo/common/enumid');
        for (BeanDefinition definition : enumIdDefinitions) {
            try {
                Class cls = IdUtil.classLoader.loadClass(definition.beanClassName);
                if (EnumId.isAssignableFrom(cls)) {
                    ENUM_ID_CLASSES.add(cls)
                }
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }

        ConfigService configService = ConfigServiceManager.instance();
        if (configService != null) {
            String prefixFromConfig = configService.getConfigValue('common.conf.resourceUrlPrefix');
            if (prefixFromConfig != null) {
                resourcePathPrefix = prefixFromConfig;
            }
        }
    }

    static UniversalId fromLink(Link link) {
        String href = link.href.replace(resourcePathPrefix, '')
        Class matchingClass = null
        String id = null

        for (Class key : RESOURCE_PATH_PATTERNS.keySet()) {
            Matcher matcher = RESOURCE_PATH_PATTERNS[key].matcher(href)
            if (matcher.find()) {
                matchingClass = key
                id = matcher.group('id')
                break
            }
        }

        if (matchingClass != null && id != null && (id == link.id || link.id == null)) {
            // todo:hack for cloudant id
            if (Id.class.isAssignableFrom(matchingClass)) {
                return matchingClass.newInstance(IdFormatter.decodeId(matchingClass, id)) as Id
            } else {
                return matchingClass.newInstance(id) as CloudantId
            }
        }
        return null
    }

    static String toHref(UniversalId value) {
        if (value instanceof Id) {
            return toHref((Id)value)
        } else if (value instanceof CloudantId) {
            return toHref((CloudantId)value)
        } else {
            return null
        }
    }

    static String toHref(Id value) {
        String path = RESOURCE_PATHS[value.class]
        if (path == null) {
            path = '/resources/{0}'
        }

        String href = formatIndexPlaceHolder(Utils.combineUrl(resourcePathPrefix, path), IdFormatter.encodeId(value));
        href = formatPropertyPlaceHolder(href, value.resourcePathPlaceHolder);
        return href;
    }

    static String toHref(CloudantId value) {
        String path = RESOURCE_PATHS[value.class]
        if (path == null) {
            path = '/resources/{0}'
        }

        String href = formatIndexPlaceHolder(Utils.combineUrl(resourcePathPrefix, path), IdFormatter.encodeId(value));
        href = formatPropertyPlaceHolder(href, value.resourcePathPlaceHolder);
        return href;
    }

    static String getResourceType(Class idClass) {
        String resourceType = RESOURCE_TYPES[idClass]

        if (resourceType == null || resourceType.empty) {
            throw new IllegalArgumentException("resourceType undefined for $idClass")
        }

        return resourceType;
    }

    static String getResourcePathPrefix() {
        return resourcePathPrefix;
    }

    private static String formatIndexPlaceHolder(String pattern, Object[] args) {
        if (pattern == null) {
            return null;
        }

        int index = 0;
        for (Object arg : args) {
            pattern = pattern.replace("{$index}", arg.toString());
            index++;
        }
        return pattern;
    }

    private static String formatPropertyPlaceHolder(String pattern, Properties properties) {
        if (pattern == null) {
            return null;
        }

        if (properties == null || properties.size() == 0) {
            return pattern;
        }

        for (Map.Entry<Object, Object> entry : properties.entrySet()) {
            String key = (String) entry.key;
            Object value = entry.value;
            String encodedValue;

            if (value instanceof Id) {
                encodedValue = IdFormatter.encodeId((Id) value);
            } else {
                encodedValue = value.toString();
            }

            pattern = pattern.replace("{$key}", encodedValue);
        }

        return pattern;
    }
}
