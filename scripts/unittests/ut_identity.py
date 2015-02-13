#!/usr/bin/python
import silkcloudut as ut
import ut_oauth
import ut_storeapi
from silkcloudut import *

oauth = ut_oauth.OAuthTests('testRegister')
storeapi = ut_storeapi.StoreTests('testRegister')
class IdentityTests(ut.TestBase):

    def testGroupMembershipDeletion(self):
        user = oauth.testRegister('identity')
        userAdmin = oauth.testRegister('identity')
        userDeveloper1 = oauth.testRegister('identity')
        userDeveloper2 = oauth.testRegister('identity')
        userOther = oauth.testRegister('identity')

        # create the organization
        organization = curlJson('POST', ut.test_uri, '/v1/organizations', headers = {
            "Authorization": "Bearer " + user.access_token
        }, data = {
            "owner": {
                "href": user.href,
                "id": user.id
            },
            "isValidated": False,
            "name": randomstr(10)
        })

        # create the admin role
        roleAdmin = curlJson('POST', ut.test_uri, '/v1/roles', headers = {
            "Authorization": "Bearer " + user.access_token
        }, data = {
            "self": None,
            "rev": None,
            "name": "admin",
            "target": {
                "targetType": "organizations",
                "filterType": "SINGLEINSTANCEFILTER",
                "filterLink": organization['self']
            },
            "futureExpansion": {},
            "createdTime": None,
            "updatedTime": None,
            "adminInfo": None
        })

        # assign the admin user to admin role
        roleAssignmentAdmin = curlJson('POST', ut.test_uri, '/v1/role-assignments', headers = {
            "Authorization": "Bearer " + user.access_token
        }, data = {
            "assignee": userAdmin.json['self'],
            "role": roleAdmin['self'],
            "futureExpansion": {}
        })

        # create the developer role
        roleDeveloper = curlJson('POST', ut.test_uri, '/v1/roles', headers = {
            "Authorization": "Bearer " + user.access_token
        }, data = {
            "self": None,
            "rev": None,
            "name": "developer",
            "target": {
                "targetType": "organizations",
                "filterType": "SINGLEINSTANCEFILTER",
                "filterLink": organization['self']
            },
            "futureExpansion": {},
            "createdTime": None,
            "updatedTime": None,
            "adminInfo": None
        })

        # create the developer group
        group = curlJson('POST', ut.test_uri, '/v1/groups', headers = {
            "Authorization": "Bearer " + user.access_token
        }, data = {
            "name": "developer",
            "organization": {
                "href": organization['self']['href'],
                "id": organization['self']['id']
            }
        })

        # assign the developer role to the developer group
        roleAssignmentDeveloper = curlJson('POST', ut.test_uri, '/v1/role-assignments', headers = {
            "Authorization": "Bearer " + user.access_token
        }, data = {
            "assignee": group['self'],
            "role": roleDeveloper['self'],
            "futureExpansion": {}
        })

        # add developer1 to the developer group
        groupMembership1 = curlJson('POST', ut.test_uri, '/v1/user-group-memberships', headers =  {
            "Authorization": "Bearer " + user.access_token
        }, data = {
            "user": {
                "href": userDeveloper1.href,
                "id": userDeveloper1.id
            },
            "group": {
                "href": group['self']['href'],
                "id": group['self']['id']
            }
        })

        # add developer2 to the developer group
        groupMembership2 = curlJson('POST', ut.test_uri, '/v1/user-group-memberships', headers =  {
            "Authorization": "Bearer " + user.access_token
        }, data = {
            "user": {
                "href": userDeveloper2.href,
                "id": userDeveloper2.id
            },
            "group": {
                "href": group['self']['href'],
                "id": group['self']['id']
            }
        })

        # test the developer2 does not have permission to delete developer1's group membership
        errorResp = curlJson('DELETE', ut.test_uri, 'v1/user-group-memberships/' + groupMembership1['self']['id'], headers = {
            "Authorization": "Bearer " + userDeveloper2.access_token
        }, raiseOnError = False)

        assert errorResp['message'] == 'Forbidden'

        # test the userOther does not have permission to delete developer1's group membership
        errorResp = curlJson('DELETE', ut.test_uri, 'v1/user-group-memberships/' + groupMembership1['self']['id'], headers = {
            "Authorization": "Bearer " + userOther.access_token
        }, raiseOnError = False)

        assert errorResp['message'] == 'Forbidden'

        # test the developer1 can delete the developer1's group membership
        response = curl('DELETE', ut.test_uri, 'v1/user-group-memberships/' + groupMembership1['self']['id'], headers = {
            "Authorization": "Bearer " + userDeveloper1.access_token
        })

        # test the admin can delete developer2's group membership
        response = curl('DELETE', ut.test_uri, 'v1/user-group-memberships/' + groupMembership2['self']['id'], headers = {
            "Authorization": "Bearer " + userAdmin.access_token
        })

    def testRoles(self):
        user = oauth.testRegister('identity')
        userAdmin = oauth.testRegister('identity')
        userDeveloper = oauth.testRegister('identity')

        # create the organization
        organization = curlJson('POST', ut.test_uri, '/v1/organizations', headers = {
            "Authorization": "Bearer " + user.access_token
        }, data = {
            "owner": {
                "href": user.href,
                "id": user.id
            },
            "isValidated": False,
            "name": randomstr(10)
        })

        # create the admin role
        roleAdmin = curlJson('POST', ut.test_uri, '/v1/roles', headers = {
            "Authorization": "Bearer " + user.access_token
        }, data = {
            "self": None,
            "rev": None,
            "name": "admin",
            "target": {
                "targetType": "organizations",
                "filterType": "SINGLEINSTANCEFILTER",
                "filterLink": organization['self']
            },
            "futureExpansion": {},
            "createdTime": None,
            "updatedTime": None,
            "adminInfo": None
        })

        # assign the admin user to admin role
        roleAssignmentAdmin = curlJson('POST', ut.test_uri, '/v1/role-assignments', headers = {
            "Authorization": "Bearer " + user.access_token
        }, data = {
            "assignee": userAdmin.json['self'],
            "role": roleAdmin['self'],
            "futureExpansion": {}
        })

        # create the developer role
        roleDeveloper = curlJson('POST', ut.test_uri, '/v1/roles', headers = {
            "Authorization": "Bearer " + user.access_token
        }, data = {
            "self": None,
            "rev": None,
            "name": "developer",
            "target": {
                "targetType": "organizations",
                "filterType": "SINGLEINSTANCEFILTER",
                "filterLink": organization['self']
            },
            "futureExpansion": {},
            "createdTime": None,
            "updatedTime": None,
            "adminInfo": None
        })

        # assign the developer role to the developer group
        roleAssignmentDeveloper = curlJson('POST', ut.test_uri, '/v1/role-assignments', headers = {
            "Authorization": "Bearer " + user.access_token
        }, data = {
            "assignee": userDeveloper.json['self'],
            "role": roleDeveloper['self'],
            "futureExpansion": {}
        })

        # test the admin user has permission to get the role
        roleDeveloper = curlJson('GET', ut.test_uri, '/v1/roles/' + roleDeveloper['self']['id'], headers = {
            "Authorization": "Bearer " + user.access_token
        })

        # test the developer doesn't have permission to get the role
        errorResp = curlJson('GET', ut.test_uri, '/v1/roles/' + roleDeveloper['self']['id'], headers = {
            "Authorization": "Bearer " + userDeveloper.access_token
        }, raiseOnError = False)
        assert errorResp['message'] == 'Resource Not Found'

        # test the admin user has permission to update the role
        roleDeveloper = curlJson('PUT', ut.test_uri, '/v1/roles/' + roleDeveloper['self']['id'], headers = {
            "Authorization": "Bearer " + user.access_token
        }, data = roleDeveloper)

        # test the developer doesn't have permission to update the role
        errorResp = curlJson('PUT', ut.test_uri, '/v1/roles/' + roleDeveloper['self']['id'], headers = {
            "Authorization": "Bearer " + userDeveloper.access_token
        }, raiseOnError = False, data = roleDeveloper)
        assert errorResp['message'] == 'Forbidden'

        # test the admin user can get the role assignment
        roleAssignmentDeveloper = curlJson('GET', ut.test_uri, '/v1/role-assignments/' + roleAssignmentDeveloper['self']['id'], headers = {
            "Authorization": "Bearer " + user.access_token
        })

        # test the developer can't get the role assignment
        errorResp = curlJson('GET', ut.test_uri, '/v1/role-assignments/' + roleAssignmentDeveloper['self']['id'], headers = {
            "Authorization": "Bearer " + userDeveloper.access_token
        }, raiseOnError = False)
        assert errorResp['message'] == 'Resource Not Found'

        # test the developer can't delete the role assignment
        errorResp = curlJson('DELETE', ut.test_uri, '/v1/role-assignments/' + roleAssignmentDeveloper['self']['id'], headers = {
            "Authorization": "Bearer " + userDeveloper.access_token
        }, raiseOnError = False)
        assert errorResp['message'] == 'Forbidden'

        # test the admin can delete the role assignment
        resp = curl('DELETE', ut.test_uri, '/v1/role-assignments/' + roleAssignmentDeveloper['self']['id'], headers = {
            "Authorization": "Bearer " + user.access_token
        })

    def testUserAttribute(self):
        # use store API tokens and it should pass
        user1 = storeapi.testRegister()
        user2 = storeapi.testRegister()

        # create user attribute
        userAttribute = curlJson('POST', ut.test_uri, '/v1/user-attributes', headers = {
            "Authorization": "Bearer " + user1.access_token
        }, data = {
            "user": {
                "href": user1.href,
                "id": user1.id
            },
            "type": "PUBLIC_PROFILE",
            "value": randomstr(10)
        })

        userAttributeGet = curlJson('GET', ut.test_uri, userAttribute['self']['href'], headers = {
            "Authorization": "Bearer " + user2.access_token
        })

        assert userAttributeGet['value'] == userAttribute['value']
        assert userAttributeGet['type'] == userAttribute['type']
        assert userAttributeGet['organization'] is not None
        assert userAttributeGet['definition'] is not None

        userAttributeDef2 = curlJson('GET', ut.test_uri, '/v1/user-attribute-definitions', headers = {
            "Authorization": "Bearer " + user1.access_token
        }, query = {
            "type": "PRIVATE_PROFILE",
            "organizationId": userAttributeGet['organization']['id']
        })
        assert len(userAttributeDef2['results']) == 1

        userAttribute2 = curlJson('POST', ut.test_uri, '/v1/user-attributes', headers = {
            "Authorization": "Bearer " + user1.access_token
        }, data = {
            "user": {
                "href": user1.href,
                "id": user1.id
            },
            "definition": userAttributeDef2['results'][0]['self'],
            "value": randomstr(10)
        })

        userAttribute2['value'] = "1234567"
        userAttribute2 = curlJson('PUT', ut.test_uri, userAttribute2['self']['href'], headers = {
            "Authorization": "Bearer " + user1.access_token
        }, data = userAttribute2)

        userAttribute2Get = curlJson('GET', ut.test_uri, userAttribute2['self']['href'], headers = {
            "Authorization": "Bearer " + user2.access_token
        })
        assert userAttribute2Get['value'] == '1234567'

        userAttributeList = curlJson('GET', ut.test_uri, "/v1/user-attributes", headers = {
            "Authorization": "Bearer " + user2.access_token
        }, query = {
            "type": "PUBLIC_PROFILE",
            "userId": user1.id
        })
        assert len(userAttributeList['results']) == 1

        userAttributeList = curlJson('GET', ut.test_uri, "/v1/user-attributes", headers = {
            "Authorization": "Bearer " + user2.access_token
        }, query = {
            "definitionId": userAttributeGet['definition']['id'],
            "userId": user1.id
        })
        assert len(userAttributeList['results']) == 1

        userAttributeList = curlJson('GET', ut.test_uri, "/v1/user-attributes", headers = {
            "Authorization": "Bearer " + user2.access_token
        }, query = {
            "definitionId": userAttributeGet['definition']['id'],
            "userId": user1.id,
            "activeOnly": True
        })
        assert len(userAttributeList['results']) == 1

        userAttribute["isSuspended"] = True
        userAttribute = curlJson('PUT', ut.test_uri, userAttribute['self']['href'], headers = {
            "Authorization": "Bearer " + oauth.getServiceAccessToken('identity.service')
        }, data = userAttribute)

        userAttributeList = curlJson('GET', ut.test_uri, "/v1/user-attributes", headers = {
            "Authorization": "Bearer " + user2.access_token
        }, query = {
            "definitionId": userAttributeGet['definition']['id'],
            "userId": user1.id,
            "activeOnly": True
        })
        assert len(userAttributeList['results']) == 0

        userAttributeList = curlJson('GET', ut.test_uri, "/v1/user-attributes", headers = {
            "Authorization": "Bearer " + user2.access_token
        }, query = {
            "definitionId": userAttributeGet['definition']['id'],
            "userId": user1.id
        })
        assert len(userAttributeList['results']) == 1




if __name__ == '__main__':
    silkcloud_utmain()