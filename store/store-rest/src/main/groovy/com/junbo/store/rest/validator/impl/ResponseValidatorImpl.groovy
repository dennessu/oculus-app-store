package com.junbo.store.rest.validator.impl
import com.junbo.common.json.ObjectMapperProvider
import com.junbo.store.rest.validator.ResponseValidator
import com.junbo.store.spec.model.browse.*
import com.junbo.store.spec.model.browse.document.*
import groovy.transform.CompileStatic
import org.apache.commons.beanutils.PropertyUtils
import org.apache.commons.lang.StringUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.util.Assert
import org.springframework.util.CollectionUtils
/**
 * The ResponseValidatorImpl class.
 */
@CompileStatic
@Component('storeResponseValidator')
class ResponseValidatorImpl implements ResponseValidator {

    private static final int ConstraintViolationLimit = 50

    private static final String FieldSeparator = '.'

    private static final Logger LOGGER = LoggerFactory.getLogger(ResponseValidatorImpl)

    @Value('${store.log.invalid.response}')
    private boolean logInvalidResponse

    enum Constraint {
        NotNull,
        NotEmpty
    }

    enum ApiEndpoint {
        Library,
        Section_Items,
        Details,
        Purchase
    }

    static class ConstraintViolation {
        String fieldPath = ''
        String constraint
    }

    static class ValidationContext {

        private List<ConstraintViolation> violations = [] as List
        List<String> fieldPath = []

        void with (String field, Closure closure) {
            fieldPath << field
            try {
                closure.call()
            } finally {
                fieldPath.pop()
            }
        }

        void appendViolation(ConstraintViolation constraintViolation) {
            if (violations.size() < ConstraintViolationLimit) {
                violations << constraintViolation
            }
        }

        List<ConstraintViolation> getViolations() {
            return Collections.unmodifiableList(violations)
        }
    }

    @Override
    void validateLibraryResponse(LibraryResponse libraryResponse) {
        Assert.notNull(libraryResponse.items)
        ValidationContext validationContext = new ValidationContext()
        validationContext.with('items') {
            validateItems(libraryResponse.items, Images.BuildType.Item_Details, ApiEndpoint.Library,validationContext)
        }
        chechAndlogInvalidResponse(LibraryResponse, validationContext)
    }

    @Override
    void validateTocResponse(TocResponse tocResponse) {
        ValidationContext validationContext = new ValidationContext()
        if (tocResponse.challenge == null) {
            notEmpty(tocResponse, 'sections', validationContext)
            for(int i = 0;i < tocResponse.sections.size(); ++i) {
                validationContext.with("sections[${i}]") {
                    validateSectionInfoNode(tocResponse.sections[i], validationContext)
                }
            }
        }
        chechAndlogInvalidResponse(TocResponse, validationContext)
    }

    @Override
    void validateSectionLayoutResponse(SectionLayoutResponse sectionLayoutResponse) {
        ValidationContext validationContext = new ValidationContext()
        notNull(sectionLayoutResponse, 'breadcrumbs', validationContext)
        notNull(sectionLayoutResponse, 'children', validationContext)
        notNull(sectionLayoutResponse, 'ordered', validationContext)
        notEmpty(sectionLayoutResponse, 'name', validationContext)

        if (!CollectionUtils.isEmpty(sectionLayoutResponse.breadcrumbs)) {
            for (int i = 0;i < sectionLayoutResponse.breadcrumbs.size();++i) {
                notEmpty(sectionLayoutResponse, "breadcrumbs[${i}].name", validationContext)
            }
        }

        if (!CollectionUtils.isEmpty(sectionLayoutResponse.children)) {
            for (int i = 0;i < sectionLayoutResponse.children.size();++i) {
                notEmpty(sectionLayoutResponse, "children[${i}].name", validationContext)
            }
        }
        chechAndlogInvalidResponse(SectionLayoutResponse, validationContext)
    }

    @Override
    void validateListResponse(ListResponse listResponse) {
        Assert.notNull(listResponse.items)
        ValidationContext validationContext = new ValidationContext()
        validationContext.with('items') {
            validateItems(listResponse.items, Images.BuildType.Item_List, ApiEndpoint.Section_Items,validationContext)
        }
        chechAndlogInvalidResponse(ListResponse, validationContext)
    }

    @Override
    void validateItemDetailsResponse(DetailsResponse detailsResponse) {
        ValidationContext validationContext = new ValidationContext()
        notNull(detailsResponse, 'item', validationContext)
        if (detailsResponse?.item != null) {
            validationContext.with('item') {
                validateItem(detailsResponse.item, Images.BuildType.Item_Details, ApiEndpoint.Details, validationContext)
            }
        }
        chechAndlogInvalidResponse(DetailsResponse, validationContext)
    }

    private void validateSectionInfoNode(SectionInfoNode sectionInfoNode, ValidationContext validationContext) {
        notEmpty(sectionInfoNode, 'name', validationContext)
        notNull(sectionInfoNode, 'children', validationContext)
        if (!CollectionUtils.isEmpty(sectionInfoNode.children)) {
            for (int i = 0;i < sectionInfoNode.children.size(); ++i) {
                validationContext.with("children[${i}]") {
                    validateSectionInfoNode(sectionInfoNode.children[i], validationContext)
                }
            }
        }
    }

    private void validateItems(List<Item> items, Images.BuildType buildType, ApiEndpoint apiEndpoint, ValidationContext validationContext) {
        for (int i = 0;i < items.size(); ++i) {
            validationContext.with("[${i}:${items[i]?.self}]") {
                validateItem(items[i], buildType, apiEndpoint,validationContext)
            }
        }
    }

    private void validateItem(Item item, Images.BuildType buildType, ApiEndpoint apiEndpoint, ValidationContext validationContext) {
        notNull(item, 'self', validationContext)
        notEmpty(item, 'itemType', validationContext)
        notEmpty(item, 'title', validationContext)
        notEmpty(item, 'aggregatedRatings', validationContext)
        notEmpty(item, 'creator', validationContext)

        notNull(item, 'appDetails', validationContext)
        validationContext.with('appDetails') {
            validateAppDetails(item.appDetails, apiEndpoint, validationContext)
        }

        notNull(item, 'images', validationContext)
        validationContext.with('images') {
            validateImages(item.images, buildType, validationContext)
        }

        if (apiEndpoint == ApiEndpoint.Details) {
            notNull(item, 'reviews', validationContext)
        }

        if (apiEndpoint == ApiEndpoint.Details || apiEndpoint == ApiEndpoint.Section_Items) {
            notNull(item, 'offer', validationContext)
            validationContext.with('offer') {
                validateOffer(item.offer, validationContext)
            }
        }

        if (apiEndpoint == ApiEndpoint.Details || apiEndpoint == ApiEndpoint.Library) {
            notNull(item, 'ownedByCurrentUser', validationContext)
        }
        notNull(item, 'currentRevision', validationContext)
    }

    private void validateOffer(Offer offer, ValidationContext validationContext) {
        notNull(offer, 'self', validationContext)
        notNull(offer, 'price', validationContext)
        notNull(offer, 'isFree', validationContext)
        notNull(offer, 'currentRevision', validationContext)
    }

    private void validateAppDetails(AppDetails appDetails, ApiEndpoint apiEndpoint, ValidationContext validationContext) {
        boolean offerAvailable = apiEndpoint != ApiEndpoint.Library && apiEndpoint != ApiEndpoint.Purchase
        notEmpty(appDetails, 'categories', validationContext)
        if (appDetails.categories != null) {
            for (int i = 0;i < appDetails.categories.size();++i) {
                notNull(appDetails, "categories[${i}]", validationContext)
                notEmpty(appDetails, "categories[${i}].id", validationContext)
                notEmpty(appDetails, "categories[${i}].name", validationContext)
            }
        }

        if (offerAvailable) {
            notNull(appDetails, 'releaseDate', validationContext)
            notEmpty(appDetails, 'publisherName', validationContext)
        }

        notEmpty(appDetails, 'developerName', validationContext)
        notEmpty(appDetails, 'packageName', validationContext)
        notEmpty(appDetails, 'versionString', validationContext)
        notNull(appDetails, 'versionCode', validationContext)
        notNull(appDetails, 'permissions', validationContext)
        notNull(appDetails, 'installationSize', validationContext)
        notEmpty(appDetails, 'revisionNotes', validationContext)

        if (appDetails.revisionNotes != null) {
            for (int i = 0;i < appDetails.revisionNotes.size();++i) {
                notNull(appDetails, "revisionNotes[${i}]", validationContext)
                validationContext.with("revisionNotes[${i}]") {
                    validateRevisionNote(appDetails.revisionNotes[i], offerAvailable, validationContext)
                }
            }
        }
    }

    private void validateRevisionNote(RevisionNote revisionNote, boolean offerAvailable, ValidationContext validationContext) {
        notNull(revisionNote, 'versionCode', validationContext)
        notEmpty(revisionNote, 'versionString', validationContext)
        if (offerAvailable) {
            notNull(revisionNote, 'releaseDate', validationContext)
        }
    }

    private void validateImages(Images images, Images.BuildType buildType, ValidationContext validationContext) {
        notEmpty(images, 'main', validationContext)
        if (buildType == Images.BuildType.Item_Details) {
            notEmpty(images, 'gallery',  validationContext)
            if (!CollectionUtils.isEmpty(images.gallery)) {
                for (int i = 0;i < images.gallery.size(); ++i) {
                    notEmpty(images, "gallery.[${i}]", validationContext)
                }
            }
        }
    }

    private void notNull(Object bean, String fieldName, ValidationContext validationContext) {
        Object val = PropertyUtils.getProperty(bean, fieldName)
        if (val == null) {
            validationContext.appendViolation( new ConstraintViolation(
                    fieldPath: path(fieldName, validationContext),
                    constraint: Constraint.NotNull.name()
            ))
        }
    }

    private void notEmpty(Object bean, String fieldName, ValidationContext validationContext) {
        Object val = PropertyUtils.getProperty(bean, fieldName)
        if (val == null || (val instanceof  String && StringUtils.isBlank(val)) ||
            (val instanceof Collection && ((Collection) val).isEmpty()) || (val instanceof Map && ((Map)val).isEmpty())) {
            validationContext.appendViolation(new ConstraintViolation(
                    fieldPath: path(fieldName, validationContext),
                    constraint: Constraint.NotEmpty.name()
            ))
        }
    }

    private String path(String fieldName, ValidationContext validationContext) {
        String val = StringUtils.join(validationContext.fieldPath, FieldSeparator)
        if (StringUtils.isBlank(val)) {
            return fieldName
        }
        return "${val}${FieldSeparator}${fieldName}"
    }

    private void chechAndlogInvalidResponse(Class type, ValidationContext validationContext) {
        if (validationContext.violations.isEmpty() || !logInvalidResponse) {
            return
        }
        LOGGER.error('name=Invalid_Response, type={}, details={}', type.canonicalName, ObjectMapperProvider.instance().writeValueAsString(validationContext.violations))
    }
}
