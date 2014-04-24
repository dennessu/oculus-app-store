package com.junbo.common.cloudant
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.JavaType
import com.fasterxml.jackson.databind.ObjectMapper
import com.junbo.common.cloudant.model.CloudantModel
import groovy.transform.CompileStatic

/**
 * Json Marshaller implementation.
 * Used internally for marshalling com.junbo.common.cloudant.model.*
 */
@CompileStatic
class CloudantModelMarshaller {
    private CloudantModelMarshaller() { }

    private static ObjectMapper objectMapper

    /**
     * Initialize the objectMapper here.
     */
    static {
        // Intentionally not using ObjectMapperProvider to get the raw serialization result
        objectMapper = new ObjectMapper()
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL)
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    }

    /**
     * Method to marshall an object into a json string.
     * @param object The object to be marshaled.
     * @return The json string of the input object.
     */
    static String marshall(CloudantModel object) throws JsonProcessingException {
        return objectMapper.writeValueAsString(object)
    }

    /**
     * Method to unmarshall a json string to an object of the given class.
     * @param string The json string.
     * @param clazz The target class of the unmarshalled object.
     * @return The unmashalled Object of the given class.
     */
    static <T extends CloudantModel> T unmarshall(String string, Class<T> clazz) throws IOException {
        return objectMapper.readValue(string, clazz)
    }

    /**
     * Method to unmashall a json string to an object of a generic class, such as List<String>.
     * @param string The json string.
     * @param parametrized The generic class parametrized part.
     * @param parameterClass The generic class generic part.
     * @return The unmashalled Object of the given generic class.
     */
    static <T extends CloudantModel> T unmarshall(String string, Class<?> parametrized, Class<?> parameterClass) throws IOException {
        // Construct the JavaType with the given parametrized class and generic type.
        JavaType javaType = objectMapper.typeFactory.constructParametricType(parametrized, parameterClass)
        return objectMapper.readValue(string, javaType)
    }
}
