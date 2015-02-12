package com.junbo.payment.clientproxy.facebook;

import com.junbo.common.error.AppCommonErrors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by wenzhu on 2/12/15.
 */
public class FacebookErrorMapUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(FacebookErrorMapUtil.class);
    public static boolean isClientError(FacebookCCErrorDetail errorDetail){
        if(errorDetail != null && errorDetail.getCode() != null){
            try{
                int errorCode = Integer.parseInt(errorDetail.getCode());
                if(FacebookClientErrorCode.get(errorCode) != null){
                    return true;
                }
            }catch (Exception ex){
                LOGGER.error("invalid error code:" + errorDetail.getCode());
                throw AppCommonErrors.INSTANCE.fieldInvalid("error_code").exception();
            }
        }
        return false;
    }
}
