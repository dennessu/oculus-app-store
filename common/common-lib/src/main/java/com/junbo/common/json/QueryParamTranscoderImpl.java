package com.junbo.common.json;

import com.junbo.common.id.Id;
import com.junbo.common.util.IdFormatter;
import com.junbo.langur.core.client.QueryParamTranscoder;

/**
 * Created by liangfu on 3/11/14.
 */
public class QueryParamTranscoderImpl implements QueryParamTranscoder {
    @Override
    public <T> String encode(T pathParam) {
        if(pathParam.getClass() == Id.class || pathParam.getClass().getSuperclass() == Id.class) {
            return IdFormatter.encodeId((Id) pathParam);
        }
        else {
            return pathParam.toString();
        }
    }
}
