package com.junbo.common.cloudant
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.JavaType
import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.util.ISO8601DateFormat
import groovy.transform.CompileStatic
/**
 * Json Marshaller implementation.
 * Used internally for entity persistence and other.
 */
@CompileStatic
class CloudantDefaultMarshaller implements CloudantMarshaller {
    private CloudantDefaultMarshaller() { }
    /**
     * static Fastxml jackson ObjectMapper used for marshall and unmarshall.
     */
    private static ObjectMapper objectMapper

    /**
     * Initialize the objectMapper here.
     */
    static {
        // Intentionally not using ObjectMapperProvider to get the raw serialization result
        // todo:    For those objectMapper, we should define one common configuration, or it will have bugs
        objectMapper = new ObjectMapper()
        objectMapper.disable(MapperFeature.USE_ANNOTATIONS)
        objectMapper.setDateFormat(new ISO8601DateFormat())
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL)
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    }

    /**
     * Method to marshall an object into a json string.
     * @param object The object to be marshaled.
     * @return The json string of the input object.
     */
    @Override
    String marshall(CloudantEntity object) throws JsonProcessingException {
        return objectMapper.writeValueAsString(object)
                .replace('"cloudantId":', '"_id":')
                .replace('"cloudantRev":', '"_rev":')
    }

    /**
     * Method to unmarshall a json string to an object of the given class.
     * @param string The json string.
     * @param clazz The target class of the unmarshalled object.
     * @return The unmashalled Object of the given class.
     */
    @Override
    <T extends CloudantEntity> T unmarshall(String string, Class<T> clazz) throws IOException {
        string = string.replace('"_id":', '"cloudantId":')
                       .replace('"_rev":', '"cloudantRev":')
        return objectMapper.readValue(string, clazz)
    }

    /**
     * Method to unmashall a json string to an object of a generic class, such as List<String>.
     * @param string The json string.
     * @param parametrized The generic class parametrized part.
     * @param parameterClass The generic class generic part.
     * @return The unmashalled Object of the given generic class.
     */
    @Override
    <T extends CloudantEntity> T unmarshall(String string, Class<?> parametrized, Class<?> parameterClass) throws IOException {
        string = string.replace('"_id":', '"cloudantId":')
                       .replace('"_rev":', '"cloudantRev":')
        // Construct the JavaType with the given parametrized class and generic type.
        JavaType javaType = objectMapper.typeFactory.constructParametricType(parametrized, parameterClass)
        return objectMapper.readValue(string, javaType)
    }
}
