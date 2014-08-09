#!/usr/bin/python
import silkcloudut as ut
import ut_oauth
from silkcloudut import *

oauth = ut_oauth.OAuthTests('testRegister')
class CatalogTests(ut.TestBase):

    def testDeveloper(self):
        user1 = oauth.testRegister('identity catalog catalog.developer')

        # create the organization
        organization1 = curlJson('POST', ut.test_uri, '/v1/organizations', headers = {
            "Authorization": "Bearer " + user1.access_token
        }, data = {
            "owner": {
                "href": user1.href,
                "id": user1.id
            },
            "isValidated": False,
            "name": randomstr(10)
        })

        role1 = curlJson('POST', ut.test_uri, '/v1/roles', headers = {
            "Authorization": "Bearer " + user1.access_token
        }, data = {
            "self": None,
            "rev": None,
            "name": "developer",
            "target": {
                "targetType": "organizations",
                "filterType": "SINGLEINSTANCEFILTER",
                "filterLink": organization1['self']
            },
            "futureExpansion": {},
            "createdTime": None,
            "updatedTime": None,
            "adminInfo": None
        })

        roleAssignment1 = curlJson('POST', ut.test_uri, '/v1/role-assignments', headers = {
            "Authorization": "Bearer " + user1.access_token
        }, data = {
            "assignee": user1.json['self'],
            "role": role1['self'],
            "futureExpansion": {}
        })

        user2 = oauth.testRegister('identity catalog catalog.developer')
        # create the organization
        organization2 = curlJson('POST', ut.test_uri, '/v1/organizations', headers = {
            "Authorization": "Bearer " + user2.access_token
        }, data = {
            "owner": {
                "href": user2.href,
                "id": user2.id
            },
            "isValidated": False,
            "name": randomstr(10)
        })

        role2 = curlJson('POST', ut.test_uri, '/v1/roles', headers = {
            "Authorization": "Bearer " + user2.access_token
        }, data = {
            "self": None,
            "rev": None,
            "name": "developer",
            "target": {
                "targetType": "organizations",
                "filterType": "SINGLEINSTANCEFILTER",
                "filterLink": organization2['self']
            },
            "futureExpansion": {},
            "createdTime": None,
            "updatedTime": None,
            "adminInfo": None
        })

        roleAssignment2 = curlJson('POST', ut.test_uri, '/v1/role-assignments', headers = {
            "Authorization": "Bearer " + user2.access_token
        }, data = {
            "assignee": user2.json['self'],
            "role": role2['self'],
            "futureExpansion": {}
        })

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

        # approve the item revision
        itemRev['status'] = 'APPROVED'
        adminToken = oauth.getServiceAccessToken('catalog.admin')
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
        results = curlJson('GET', ut.test_uri, '/v1/offers?itemId=' + item['self']['id']
                           + '&publisherId=' + organization1['self']['id'], headers={
            "Authorization": "Bearer " + user1.access_token
        })
        assert results['total'] == 0

        # verify user2 can't get user1's unpublished offer
        errorResp = curlJson('GET', ut.test_uri, offer['self']['href'], headers = {
            "Authorization": "Bearer " + user2.access_token
        }, raiseOnError = False)
        assert errorResp['message'] == 'Resource Not Found'

        # verify user2 can't search user1's unpublished offer
        results = curlJson('GET', ut.test_uri, '/v1/offers?itemId=' + item['self']['id']
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
                    "name": "testItem_CartCheckout_Digital1",
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
                            "y": 15,
                            "x": 20,
                            "href": "http://www.google.com",
                            "id": "id of image",
                            "size": 300
                        },
                        "thumbnail": {
                            "y": 10,
                            "x": 20,
                            "href": "http://www.google.com",
                            "id": "id of image",
                            "size": 200
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
                            "y": 10,
                            "x": 20,
                            "href": "http://www.google.com",
                            "id": "id of image",
                            "size": 200
                        }
                    },
                    "shortDescription": "short description",
                    "name": "testOffer_CartCheckout_Digital1",
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