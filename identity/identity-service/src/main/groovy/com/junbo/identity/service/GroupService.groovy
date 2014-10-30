package com.junbo.identity.service

import com.junbo.common.id.GroupId
import com.junbo.common.id.OrganizationId
import com.junbo.identity.spec.v1.model.Group
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic

/**
 * Created by liangfu on 10/21/14.
 */
@CompileStatic
public interface GroupService {
    Promise<Group> get(GroupId id)

    Promise<Group> getActiveGroup(GroupId id)

    Promise<Group> create(Group model)

    Promise<Group> update(Group model, Group oldModel)

    Promise<Void> delete(GroupId id)

    Promise<Group> searchByOrganizationIdAndName(OrganizationId id, String name, Integer limit, Integer offset)

    Promise<List<Group>> searchByOrganizationId(OrganizationId id, Integer limit, Integer offset)
}