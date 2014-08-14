package com.junbo.test.identity;

import com.fasterxml.jackson.databind.JsonNode;
import com.junbo.identity.spec.v1.model.ErrorDetail;
import com.junbo.identity.spec.v1.model.ErrorInfo;
import com.junbo.test.common.HttpclientHelper;
import com.junbo.test.common.JsonHelper;
import com.junbo.test.common.RandomHelper;
import com.junbo.test.common.Validator;
import org.testng.annotations.*;

/**
 * Created by liangfu on 8/12/14.
 */
public class postErrorCodeDetail {

    @BeforeClass
    public void run() throws Exception {
        HttpclientHelper.CreateHttpClient();
        Identity.GetHttpAuthorizationHeader();
        HttpclientHelper.CloseHttpClient();
    }

    @BeforeMethod
    public void setup() throws Exception {
        HttpclientHelper.CreateHttpClient();
    }

    @AfterMethod
    public void dispose() throws Exception {
        HttpclientHelper.CloseHttpClient();
    }

    @Test(groups = "bvt")
    // https://oculus.atlassian.net/browse/SER-306
    // https://oculus.atlassian.net/browse/SER-305
    public void postErrorInfo() throws Exception {
        ErrorInfo errorInfo = IdentityModel.DefaultErrorInfo();
        ErrorInfo posted = Identity.ErrorInfoDefault(errorInfo);

        ErrorDetail errorDetail = new ErrorDetail();
        errorDetail.setErrorInformation(RandomHelper.randomAlphabetic(15));
        errorDetail.setErrorSummary(RandomHelper.randomAlphabetic(15));
        errorDetail.setSupportLink(RandomHelper.randomAlphabetic(15));
        errorDetail.setErrorTitle(RandomHelper.randomAlphabetic(15));

        posted.getLocales().put("zh_CN", JsonHelper.ObjectToJsonNode(errorDetail));
        ErrorInfo updated = Identity.ErrorInfoPut(posted);
        JsonNode jsonNode = updated.getLocales().get("zh_CN");
        ErrorDetail errorDetailGotten = (ErrorDetail)JsonHelper.JsonNodeToObject(jsonNode, ErrorDetail.class);
        Validator.Validate("Validate update errorInformation", errorDetail.getErrorInformation(), errorDetailGotten.getErrorInformation());
        Validator.Validate("Validate update errorSummary", errorDetail.getErrorSummary(), errorDetailGotten.getErrorSummary());
        Validator.Validate("Validate update supportLink", errorDetail.getSupportLink(), errorDetailGotten.getSupportLink());
        Validator.Validate("Validate update errorTitle", errorDetail.getErrorTitle(), errorDetailGotten.getErrorTitle());

        ErrorInfo gotten = Identity.ErrorInfoGet(updated.getErrorIdentifier());
        jsonNode = gotten.getLocales().get("zh_CN");
        errorDetailGotten = (ErrorDetail)JsonHelper.JsonNodeToObject(jsonNode, ErrorDetail.class);
        Validator.Validate("Validate get errorInformation", errorDetail.getErrorInformation(), errorDetailGotten.getErrorInformation());
        Validator.Validate("Validate get errorSummary", errorDetail.getErrorSummary(), errorDetailGotten.getErrorSummary());
        Validator.Validate("Validate get supportLink", errorDetail.getSupportLink(), errorDetailGotten.getSupportLink());
        Validator.Validate("Validate get errorTitle", errorDetail.getErrorTitle(), errorDetailGotten.getErrorTitle());
    }
}
