package com.junbo.identity.data.repository

import com.junbo.common.id.ErrorIdentifier
import com.junbo.identity.spec.v1.model.ErrorInfo
import com.junbo.sharding.repo.BaseRepository
import groovy.transform.CompileStatic

/**
 * Created by liangfu on 7/23/14.
 */
@CompileStatic
public interface ErrorInfoRepository extends BaseRepository<ErrorInfo, ErrorIdentifier>{

}