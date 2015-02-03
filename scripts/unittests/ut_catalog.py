#!/usr/bin/python
import silkcloudut as ut
import ut_oauth
from silkcloudut import *

oauth = ut_oauth.OAuthTests('testRegister')
class CatalogTests(ut.TestBase):

    def createDevOrgnization(self):
        user = oauth.testRegister('identity catalog catalog.developer')
        # create the organization
        organization = curlJson('POST', ut.test_uri, '/v1/organizations', headers = {
            "Authorization": "Bearer " + user.access_token
        }, data = {
            "owner": {
                "href": user.href,
                "id": user.id
            },
            "isValidated": False,
            "name": randomstr(10),
            "fbPayoutOrgId": randomstr(10)
        })

        role = curlJson('POST', ut.test_uri, '/v1/roles', headers = {
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

        roleAssignment = curlJson('POST', ut.test_uri, '/v1/role-assignments', headers = {
            "Authorization": "Bearer " + user.access_token
        }, data = {
            "assignee": user.json['self'],
            "role": role['self'],
            "futureExpansion": {}
        })

        return (user, organization)

    def testOrganization(self):
        # create the user
        user = oauth.testRegister('identity catalog catalog.developer')

        # create the organization
        organization = curlJson('POST', ut.test_uri, '/v1/organizations', headers = {
            "Authorization": "Bearer " + user.access_token
        }, data = {
            "owner": {
                "href": user.href,
                "id": user.id
            },
            "isValidated": False,
            "name": randomstr(10),
            "fbPayoutOrgId": randomstr(10)
        })

        # create the role
        role = curlJson('POST', ut.test_uri, '/v1/roles', headers = {
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

        # create the group
        group = curlJson('POST', ut.test_uri, '/v1/groups', headers = {
            "Authorization": "Bearer " + user.access_token
        }, data = {
            "name": "developer",
            "organization": {
                "href": organization['self']['href'],
                "id": organization['self']['id']
            }
        })

        # create the group membership
        groupMembership = curlJson('POST', ut.test_uri, '/v1/user-group-memberships', headers =  {
            "Authorization": "Bearer " + user.access_token
        }, data = {
            "user": {
                "href": user.href,
                "id": user.id
            },
            "group": {
                "href": group['self']['href'],
                "id": group['self']['id']
            }
        })

        roleAssignment = curlJson('POST', ut.test_uri, '/v1/role-assignments', headers = {
            "Authorization": "Bearer " + user.access_token
        }, data = {
            "assignee": group['self'],
            "role": role['self'],
            "futureExpansion": {}
        })

        # wait 5 seconds for the couch db index update
        time.sleep(5)
        # publish an item
        item = curlJson('POST', ut.test_uri, '/v1/items', headers = {
            "Authorization": "Bearer " + user.access_token
        }, data = {
            "genres": [],
            "defaultOffer": None,
            "rev": None,
            "currentRevision": None,
            "revisions": None,
            "type": "APP",
            "isDownloadable": True,
            "maxNumberOfPurchase": 1,
            "subtype": None,
            "developer": organization['self']
        })

    def testPublishOffer(self):
        user, organization = self.createDevOrgnization()
        adminToken = oauth.getServiceAccessToken('catalog.admin')
        print 'adminToken: ', adminToken
        item = curlJson('POST', ut.test_uri, '/v1/items', headers = {
            "Authorization": "Bearer " + user.access_token
        }, data = {
            "genres": [],
            "defaultOffer": None,
            "rev": None,
            "currentRevision": None,
            "revisions": None,
            "type": "APP",
            "isDownloadable": True,
            "maxNumberOfPurchase": 1,
            "subtype": None,
            "developer": organization['self']
        })
        itemRev = curlJson('POST', ut.test_uri, '/v1/item-revisions', headers = {
            "Authorization": "Bearer " + user.access_token
        }, data = self.getDigitalItemRevision(item, organization))
        itemRev['status'] = 'APPROVED'
        itemRev = curlJson('PUT', ut.test_uri, itemRev['self']['href'], headers = {
            "Authorization": "Bearer " + adminToken
        }, data = itemRev)

        offer = curlJson('POST', ut.test_uri, '/v1/offers', headers = {
            "Authorization": "Bearer " + user.access_token
        }, data = {
            "publisher": organization['self'],
            "rev": None,
            "isPublished": False,
            "environment": "DEV",
            "currentRevision": None,
            "categories": []
        })
        print 'user token', user.access_token

        # publish offer with no currentRevision
        offer['isPublished'] = True
        errorResp = curlJson('PUT', ut.test_uri, offer['self']['href'], headers = {
            "Authorization": "Bearer " + adminToken
        }, data = offer)

        offerRev = curlJson('POST', ut.test_uri, '/v1/offer-revisions', headers = {
            "Authorization": "Bearer " + user.access_token
        }, data = self.getDigitalOfferRevision(offer, item, organization))

        offerRev['status'] = 'APPROVED'
        offerRev = curlJson('PUT', ut.test_uri, offerRev['self']['href'], headers = {
            "Authorization": "Bearer " + adminToken
        }, data = offerRev)

        # developer publish the offer
        errorResp = curlJson('PUT', ut.test_uri, offer['self']['href'], headers = {
            "Authorization": "Bearer " + user.access_token
        }, data = offer, raiseOnError = False)
        assert errorResp['message'] == 'Forbidden'

        # publish offer with currentRevision
        offer = curlJson('GET', ut.test_uri, offer['self']['href'], headers = {
            "Authorization": "Bearer " + user.access_token
        })
        offer['isPublished'] = True
        offer = curlJson('PUT', ut.test_uri, offer['self']['href'], headers = {
            "Authorization": "Bearer " + adminToken
        }, data = offer)

        userErrorLogs = curlJson('GET', ut.test_uri, '/v1/user-logs', query={
            "userId": user.json['self']['id'],
            "apiName": "offers",
            "isOK": False
        }, headers={
            "Authorization": "Bearer " + oauth.getServiceAccessToken('identity.service')
        })
        assert userErrorLogs['total'] == 1


    def testItemOfferE2EFlow(self):
        user1, organization1 = self.createDevOrgnization()
        user2, organization2 = self.createDevOrgnization()

        # publish an item
        item = curlJson('POST', ut.test_uri, '/v1/items', headers = {
            "Authorization": "Bearer " + user1.access_token
        }, data = {
            "genres": [],
            "defaultOffer": None,
            "rev": None,
            "currentRevision": None,
            "revisions": None,
            "type": "APP",
            "isDownloadable": True,
            "maxNumberOfPurchase": 1,
            "subtype": None,
            "developer": organization1['self']
        })

        itemRev = curlJson('POST', ut.test_uri, '/v1/item-revisions', headers = {
            "Authorization": "Bearer " + user1.access_token
        }, data = self.getDigitalItemRevision(item, organization1))

        # verify the item is now available to public
        item = curlJson('GET', ut.test_uri, item['self']['href'], headers={
            "Authorization": "Bearer " + user1.access_token
        })

        # verify user1 can get item revision (DRAFT)
        itemRev = curlJson('GET', ut.test_uri, itemRev['self']['href'], headers={
            "Authorization": "Bearer " + user1.access_token
        })

        # verify user1 can search his DRAFT item revision
        results = curlJson('GET', ut.test_uri, '/v1/item-revisions?itemId=' + item['self']['id']
                           + '&developerId=' + organization1['self']['id'], headers={
            "Authorization": "Bearer " + user1.access_token
        })
        assert results['total'] == 1

        # verify user2 can't find user1's DRAFT item revision
        errorResp = curlJson('GET', ut.test_uri, itemRev['self']['href'], headers={
            "Authorization": "Bearer " + user2.access_token
        }, raiseOnError=False)
        assert errorResp['message'] == 'Resource Not Found'

        # verify user2 can't search user1's DRAFT item revision
        results = curlJson('GET', ut.test_uri, '/v1/item-revisions?itemId=' + item['self']['id']
                           + '&developerId=' + organization1['self']['id'], headers={
            "Authorization": "Bearer " + user2.access_token
        })
        assert results['total'] == 0

        # verify user2 can't search user1's item revision with filter status=DRAFT
        errorResp = curlJson('GET', ut.test_uri, '/v1/item-revisions?itemId=' + item['self']['id']
                           + '&developerId=' + organization1['self']['id'] + '&status=DRAFT', headers={
            "Authorization": "Bearer " + user2.access_token
        }, raiseOnError = False)
        assert errorResp['message'] == 'Forbidden'

        # verify public can't get user1's DRAFT item revision
        errorResp = curlJson('GET', ut.test_uri, itemRev['self']['href'], raiseOnError=False)
        assert errorResp['message'] == 'Resource Not Found'

        results = curlJson('GET', ut.test_uri, '/v1/item-revisions?itemId=' + item['self']['id'])

        # verify normal user can't search user1's item revision with filter status=DRAFT
        errorResp = curlJson('GET', ut.test_uri, '/v1/item-revisions?itemId=' + item['self']['id']
                           + '&developerId=' + organization1['self']['id'] + '&status=DRAFT',  raiseOnError = False)
        assert errorResp['message'] == 'Forbidden'

        # verify normal user can't search user1's DRAFT item revision
        results = curlJson('GET', ut.test_uri, '/v1/item-revisions?itemId=' + item['self']['id']
                           + '&developerId=' + organization1['self']['id'])
        assert results['total'] == 0

        adminToken = oauth.getServiceAccessToken('catalog.admin')

        # verify admin user can search item revision with filter status=DRAFT
        results = curlJson('GET', ut.test_uri, '/v1/item-revisions?itemId=' + item['self']['id'], headers={
            "Authorization": "Bearer " + adminToken
        }, raiseOnError = False)
        print results
        assert results['total'] == 1

        # submit item for review
        itemRev['status'] = 'PENDING_REVIEW'
        itemRev = curlJson('PUT', ut.test_uri, itemRev['self']['href'], headers = {
            "Authorization": "Bearer " + user1.access_token
        }, data = itemRev)

        # verify admin user can search item revision with filter status=PENDING_REVIEW
        results = curlJson('GET', ut.test_uri, '/v1/item-revisions?itemId=' + item['self']['id'] + "&status=PENDING_REVIEW",
                           headers={ "Authorization": "Bearer " + adminToken}, raiseOnError = False)
        assert results['total'] == 1

        # approve the item revision
        itemRev['status'] = 'APPROVED'
        itemRev = curlJson('PUT', ut.test_uri, itemRev['self']['href'], headers = {
            "Authorization": "Bearer " + adminToken
        }, data = itemRev)

        # verify user2 can get the APPROVED item revision
        curlJson('GET', ut.test_uri, itemRev['self']['href'], headers={
            "Authorization": "Bearer " + user2.access_token
        })

         # verify user2 can't search user1's DRAFT item revision
        results = curlJson('GET', ut.test_uri, '/v1/item-revisions?itemId=' + item['self']['id']
                           + '&developerId=' + organization1['self']['id'], headers={
            "Authorization": "Bearer " + user2.access_token
        })
        assert results['total'] == 1

        # verify the item revision is open to public
        curlJson('GET', ut.test_uri, itemRev['self']['href'])

        # verify normal user can't search user1's DRAFT item revision
        results = curlJson('GET', ut.test_uri, '/v1/item-revisions?itemId=' + item['self']['id']
                           + '&developerId=' + organization1['self']['id'])
        assert results['total'] == 1

        # publish an offer
        offer = curlJson('POST', ut.test_uri, '/v1/offers', headers = {
            "Authorization": "Bearer " + user1.access_token
        }, data = {
            "publisher": organization1['self'],
            "rev": None,
            "isPublished": False,
            "environment": "DEV",
            "currentRevision": None,
            "categories": []
        })

        offerRev = curlJson('POST', ut.test_uri, '/v1/offer-revisions', headers = {
            "Authorization": "Bearer " + user1.access_token
        }, data = self.getDigitalOfferRevision(offer, item, organization1))

        # verify user1 can get his unpublished offer
        offer = curlJson('GET', ut.test_uri, offer['self']['href'], headers = {
            "Authorization": "Bearer " + user1.access_token
        })

        # verify user1 can get his DRAFT offer revision
        offerRev = curlJson('GET', ut.test_uri, offerRev['self']['href'], headers = {
            "Authorization": "Bearer " + user1.access_token
        })

        # verify user1 can search user1's unpublished offer
        results = curlJson('GET', ut.test_uri, '/v1/offers?offerId=' + offer['self']['id']
                           + '&publisherId=' + organization1['self']['id'], headers={
            "Authorization": "Bearer " + user1.access_token
        })
        assert results['total'] == 1

        # verify admin can search user1's unpublished offer
        results = curlJson('GET', ut.test_uri, '/v1/offers?offerId=' + offer['self']['id'], headers={
            "Authorization": "Bearer " + adminToken
        })
        assert results['total'] == 1

        # verify user2 can't get user1's unpublished offer
        errorResp = curlJson('GET', ut.test_uri, offer['self']['href'], headers = {
            "Authorization": "Bearer " + user2.access_token
        }, raiseOnError = False)
        assert errorResp['message'] == 'Resource Not Found'

        # verify user2 can't search user1's unpublished offer
        results = curlJson('GET', ut.test_uri, '/v1/offers?offerId=' + offer['self']['id']
                           + '&publisherId=' + organization1['self']['id'], headers={
            "Authorization": "Bearer " + user2.access_token
        })
        assert results['total'] == 0

        # verify user2 can't search user1's offers with published=false
        errorResp = curlJson('GET', ut.test_uri, '/v1/offers?itemId=' + item['self']['id']
                           + '&publisherId=' + organization1['self']['id'] + '&published=false', headers={
            "Authorization": "Bearer " + user2.access_token
        }, raiseOnError = False)
        assert errorResp['message'] == 'Forbidden'

        # verify admin can search user1's DRAFT offer revision
        results = curlJson('GET', ut.test_uri, '/v1/offer-revisions?publisherId=' + organization1['self']['id'], headers={
            "Authorization": "Bearer " + adminToken
        })
        assert results['total'] > 0

        # verify user2 can't search user1's DRAFT offer revision
        results = curlJson('GET', ut.test_uri, '/v1/offer-revisions?publisherId=' + organization1['self']['id'], headers={
            "Authorization": "Bearer " + user2.access_token
        })
        assert results['total'] == 0

        # verify user2 can't get user1's DRAFT offer revision
        errorResp = curlJson('GET', ut.test_uri, offerRev['self']['href'], headers = {
            "Authorization": "Bearer " + user2.access_token
        }, raiseOnError = False)
        assert errorResp['message'] == 'Resource Not Found'

        # verify user2 can't search user1's offers with published=false
        errorResp = curlJson('GET', ut.test_uri, '/v1/offers?publisherId=' + organization1['self']['id'] + '&published=false', headers={
            "Authorization": "Bearer " + user2.access_token
        }, raiseOnError = False)
        assert errorResp['message'] == 'Forbidden'

        # verify normal user can't get user1's unpublished offer
        errorResp = curlJson('GET', ut.test_uri, offer['self']['href'], raiseOnError = False)
        assert errorResp['message'] == 'Resource Not Found'

        # verify normal user can't get user1's DRAFT offer revision
        errorResp = curlJson('GET', ut.test_uri, offerRev['self']['href'], raiseOnError = False)
        assert errorResp['message'] == 'Resource Not Found'

        # verify normal user can't search user1's DRAFT offer revision
        results = curlJson('GET', ut.test_uri, '/v1/offer-revisions?publisherId=' + organization1['self']['id'])
        assert results['total'] == 0

        # verify normal user can't search user1's offers with published=false
        errorResp = curlJson('GET', ut.test_uri, '/v1/offers?publisherId=' + organization1['self']['id'] + '&published=false', raiseOnError = False)
        assert errorResp['message'] == 'Forbidden'

        offerRev['status'] = 'APPROVED'
        offerRev = curlJson('PUT', ut.test_uri, offerRev['self']['href'], headers = {
            "Authorization": "Bearer " + adminToken
        }, data = offerRev)

        offer = curlJson('GET', ut.test_uri, offer['self']['href'], headers = {
            "Authorization": "Bearer " + user1.access_token
        })
        offer['isPublished'] = True
        offer = curlJson('PUT', ut.test_uri, offer['self']['href'], headers = {
            "Authorization": "Bearer " + adminToken
        }, data = offer)

        # verify user2 can get user1's published offer
        offer = curlJson('GET', ut.test_uri, offer['self']['href'], headers = {
            "Authorization": "Bearer " + user2.access_token
        })

        # verify user2 can get user1's APPROVED offer revision
        offerRev = curlJson('GET', ut.test_uri, offerRev['self']['href'], headers = {
            "Authorization": "Bearer " + user2.access_token
        })

        # verify user2 can search user1's published offer
        results = curlJson('GET', ut.test_uri, '/v1/offers?itemId=' + item['self']['id']
                           + '&publisherId=' + organization1['self']['id'], headers={
            "Authorization": "Bearer " + user2.access_token
        })
        assert results['total'] == 1

        # verify user2 can search user1's APPROVED offer revision
        results = curlJson('GET', ut.test_uri, '/v1/offer-revisions?publisherId=' + organization1['self']['id'], headers={
            "Authorization": "Bearer " + user2.access_token
        })
        assert results['total'] == 1

        # verify normal user can search user1's published offer
        results = curlJson('GET', ut.test_uri, '/v1/offers?itemId=' + item['self']['id']
                           + '&publisherId=' + organization1['self']['id'])
        assert results['total'] == 1

        # verify normal can search user1's APPROVED offer revision
        results = curlJson('GET', ut.test_uri, '/v1/offer-revisions?publisherId=' + organization1['self']['id'])
        assert results['total'] == 1

    # sample data
    def getDigitalItemRevision(self, item, developer):
        return {
            "status": "DRAFT",
            "distributionChannel": [ "STORE" ],
            "countries": {
                "JP": {
                    "supportPhone": "9876543210"
                },
                "US": {
                    "supportPhone": "1234567890"
                }
            },
            "item": item['self'],
            "supportedInputDevices": [ "KEYBOARD", "MOUSE" ],
            "locales": {
                "en_US": {
                    "minimumSystemRequirements": {
                        "PC": {
                            "notice": "notice",
                            "multiplayer": "multiplayer",
                            "memory": "16G",
                            "hardDrive": "1T",
                            "processor": "4 CPUs",
                            "noticeImportant": "Notice important",
                            "other": "Other",
                            "graphics": "Intel",
                            "soundCard": "soundCard",
                            "operatingSystem": "Win7",
                            "directX": "Direct 11"
                        }
                    },
                    "legalInformation": "legalInformation",
                    "name": "utItem_CartCheckout_Digital1",
                    "copyright": "copyright",
                    "knownBugs": "knownBugs",
                    "color": "black",
                    "releaseNotes": {
                        "longNotes": "Long notes",
                        "shortNotes": "Short Notes"
                    },
                    "communityForumLink": "http://www.google.com",
                    "longDescription": "long description",
                    "credits": "credits",
                    "supportEmail": "jasonfu@silkcloud.com",
                    "videos": [],
                    "images": {
                        "main": {
                            "20x20":{
                            "y": 15,
                            "x": 20,
                            "href": "http://www.google.com",
                            "id": "id of image",
                            "size": 300
                            }
                        },
                        "thumbnail": {
                            "20x20":{
                            "y": 10,
                            "x": 20,
                            "href": "http://www.google.com",
                            "id": "id of image",
                            "size": 200
                            }
                        }
                    },
                    "recommendedSystemRequirements": {
                        "PC": {
                            "notice": "notice",
                            "multiplayer": "multiplayer",
                            "memory": "16G",
                            "hardDrive": "1T",
                            "processor": "4 CPUs",
                            "noticeImportant": "Notice important",
                            "other": "Other",
                            "graphics": "Intel",
                            "soundCard": "soundCard",
                            "operatingSystem": "Win7",
                            "directX": "Direct 11"
                        }
                    },
                    "shortDescription": "short description",
                    "manualDocument": "http://www.google.com/manual.pdf",
                    "size": "small"
                }
            },
            "futureExpansion": {},
            "binaries": {
                "PC": {
                    "href": "http://www.google.com/downlaod/angrybird1_0.exe",
                    "version": "1.0",
                    "size": 1024
                },
                "MAC": {
                    "href": "http://www.google.com/download/angrybird1_1mac.dmg",
                    "version": "1.1",
                    "size": 1024
                }
            },
            "downloadName": "download name",
            "msrp": {
                "prices": {
                    "JP": {
                        "JPY": 1000
                    },
                    "US": {
                        "USD": 10.0
                    }
                },
                "priceTier": None,
                "priceType": "CUSTOM"
            },
            "developer": developer['self']
        }

    def getDigitalOfferRevision(self, offer, item, developer):
        return {
            "status": "DRAFT",
            "publisher": developer['self'],
            "distributionChannel": [
                "STORE"
            ],
            "offer": offer['self'],
            "items": [
                {
                    "item": item['self'],
                    "quantity": 1
                }
            ],
            "restrictions": None,
            "rev": None,
            "eventActions": None,
            "subOffers": [],
            "regions": {
                "JP": {
                    "releaseDate": "2013-06-10T09:05:58Z",
                    "isPurchasable": True
                },
                "US": {
                    "releaseDate": "2013-06-10T09:05:58Z",
                    "isPurchasable": True
                }
            },
            "futureExpansion": {},
            "price": {
                "prices": {
                    "JP": {
                        "JPY": 1000
                    },
                    "US": {
                        "USD": 10.0
                    }
                },
                "priceTier": None,
                "priceType": "CUSTOM"
            },
            "locales": {
                "en_US": {
                    "images": {
                        "main": {},
                        "thumbnail": {
                            "20x20":{
                            "y": 10,
                            "x": 20,
                            "href": "http://www.google.com",
                            "id": "id of image",
                            "size": 200
                            }
                        }
                    },
                    "shortDescription": "short description",
                    "name": "utOffer_CartCheckout_Digital1",
                    "videos": [
                        {
                            "id": "id of the video",
                            "service": "YOUTUBE",
                            "title": "title of the video"
                        }
                    ],
                    "longDescription": "long description"
                }
            }
        }

if __name__ == '__main__':
    silkcloud_utmain()