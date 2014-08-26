#!/usr/bin/python
import silkcloudut as ut
import ut_oauth
from silkcloudut import *

oauth = ut_oauth.OAuthTests('testRegister')
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
if __name__ == '__main__':
    silkcloud_utmain()