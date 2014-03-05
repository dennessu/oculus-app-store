package com.goshop.catalog.common.error;

import java.util.Date;

public interface AppErrors {
    public static final AppErrors INSTANCE = ErrorProxy.newProxyInstance(AppErrors.class);

    @ErrorDef(httpStatusCode = 400, code = "10000", message ="invalid null input parameter")
    AppError invalidNullInputParam();

    @ErrorDef(httpStatusCode = 400, code = "10001", message = "invalid Json: {0}", field = "request.body")
    AppError invalidJson(String detail);

    @ErrorDef(httpStatusCode = 400, code = "10003", message = "ObjectId missing.", field = "{0}")
    AppError objectIdMissing(String field);

    @ErrorDef(httpStatusCode = 400, code = "10002", message = "ObjectId [{0}] invalid.", field = "{1}")
    AppError objectIdInvalid(String objectId, String field);

    @ErrorDef(httpStatusCode = 400, code = "10005", message = "Draft [{0}] invalid.", field = "{1}")
    AppError draftInvalid(String objectId, String field);

    @ErrorDef(httpStatusCode = 400, code = "10001", message = "Validation Failed")
    AppError validationFailed(String entityName, AppError... causes);

    @ErrorDef(httpStatusCode = 404, code = "10004", message = "ObjectId [{0}] not found.", field = "{1}")
    AppError objectIdNotFound(String objectId, String field);

    @ErrorDef(httpStatusCode = 404, code = "10006", message = "Draft [{0}] not found.", field = "{1}")
    AppError draftNotFound(String objectId, String field);

    @ErrorDef(httpStatusCode = 404, code = "10007", message = "Query parameter [{0}] is invalid. {1}", field = "{0}")
    AppError queryParamInvalid(String queryParam, String detail);

    @ErrorDef(httpStatusCode = 404, code = "10008", message = "EntityType [{0}] is not expected. Excepted type is [{1}].")
    AppError entityTypeInvalid(String entityType, String expectedEntityType);

    @ErrorDef(httpStatusCode = 404, code = "10009", message = "Method [{0}] not supported in class [{1}].")
    AppError methodNotSupport(String method, String className);

    @ErrorDef(httpStatusCode = 404, code = "10010", message = "ObjectId [{1}] has cycle during method call [{0}].")
    AppError cycleDetectError(String method, String categoryId);

    @ErrorDef(httpStatusCode = 404, code = "10011", message = "ObjectId [{1}] doesn't match the updated objectId [{0}].")
    AppError objectIdMissMatch(String objectId, String objectIdToUpdate);

    @ErrorDef(httpStatusCode = 404, code = "10012", message = "ObjectId [{0}] is using by other objectId.")
    AppError objectIdInUsing(String objectId, String[] targetObjectId);

    @ErrorDef(httpStatusCode = 404, code = "10013", message = "ObjectId [{0}] already exists.")
    AppError objectIdDuplicate(String objectId);

    @ErrorDef(httpStatusCode = 404, code = "10014", message = "Sku properties are equal [{0}].")
    AppError skuPropertyDuplicate(String skuProperty);

    @ErrorDef(httpStatusCode = 404, code = "10015", message = "Price effective date has time overlap in the same currency [{0} to {1}] vs [{2} to {3}].")
    AppError priceEffectiveDateOverlap(Date startDate1, Date endDate1, Date startDate2, Date endDate2);

    @ErrorDef(httpStatusCode = 404, code = "10016", message = "Price start date [{0}] should be earlier than price end date [{1}].")
    AppError priceDateInvalid(Date startDate, Date endDate);

    @ErrorDef(httpStatusCode = 404, code = "10017", message = "Object [{0}] with revision [{1}] not found.")
    AppError objectIdRevisionNotFound(String objectId, Long version);

    @ErrorDef(httpStatusCode = 201, code = "10001", message = "something is wrong with {0}")
    AppError somethingIsWrong(String helloWorld);

    @ErrorDef(httpStatusCode = 202, code = "10002", message = "something is wrong with {0} again")
    AppError somethingIsWrongAgain(String helloWorld);
}
