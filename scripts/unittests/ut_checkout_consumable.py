#!/usr/bin/python
import silkcloudut as ut
import ut_oauth
from silkcloudut import *
from subprocess import Popen, PIPE
from datetime import datetime

oauth = ut_oauth.OAuthTests('testRegister')
class CheckoutTests(ut.TestBase):

    def testDeveloper(self):
        user = oauth.testRegister('identity identity.pii catalog catalog.developer', True)

        # create the organization
        organization = curlJson('POST', ut.test_uri, '/v1/organizations', headers = {
            "Authorization": "Bearer " + user.access_token
        }, data = {
            "owner": {
                "href": user.href,
                "id": user.id
            },
            "isValidated": False,
            "fbPayoutOrgId": randomstr(10),
            "name": randomstr(10)
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

        # publish an item
        item = curlJson('POST', ut.test_uri, '/v1/items', headers = {
            "Authorization": "Bearer " + user.access_token
        }, data = {
            "genres": [],
            "defaultOffer": None,
            "rev": None,
            "currentRevision": None,
            "revisions": None,
            "type": "ADDITIONAL_CONTENT",
            "isDownloadable": False,
            "maxNumberOfPurchase": 1000000001,
            "subtype": None,
            "developer": organization['self']
        })

        itemRev = curlJson('POST', ut.test_uri, '/v1/item-revisions', headers = {
            "Authorization": "Bearer " + user.access_token
        }, data = self.getConsumableItemRevision(item, organization))

        itemRev['status'] = 'APPROVED'
        adminToken = oauth.getServiceAccessToken('catalog.admin')
        itemRev = curlJson('PUT', ut.test_uri, itemRev['self']['href'], headers = {
            # TODO: "Authorization": "Bearer " + adminToken
            "Authorization": "Bearer " + adminToken
        }, data = itemRev)

        # verify the item is now available to public
        item = curlJson('GET', ut.test_uri, item['self']['href'])
        assert item['currentRevision'] is not None
        self.assertEqual(item['currentRevision']['href'], itemRev['self']['href'])

        itemRev = curlJson('GET', ut.test_uri, item['currentRevision']['href'])

        # publish an offer
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

        offerRev = curlJson('POST', ut.test_uri, '/v1/offer-revisions', headers = {
            "Authorization": "Bearer " + user.access_token
        }, data = self.getConsumableOfferRevision(offer, item, organization))

        offerRev['status'] = 'APPROVED'
        offerRev = curlJson('PUT', ut.test_uri, offerRev['self']['href'], headers = {
            # TODO: "Authorization": "Bearer " + adminToken
            "Authorization": "Bearer " + adminToken
        }, data = offerRev)

        offer = curlJson('GET', ut.test_uri, offer['self']['href'], headers = {
            "Authorization": "Bearer " + user.access_token
        })
        offer['isPublished'] = True
        offer = curlJson('PUT', ut.test_uri, offer['self']['href'], headers = {
            "Authorization": "Bearer " + adminToken
        }, data = offer)

        # verify the item is now available to public
        offer = curlJson('GET', ut.test_uri, offer['self']['href'])
        assert offer['currentRevision'] is not None
        self.assertEqual(offer['currentRevision']['href'], offerRev['self']['href'])

        offerRev = curlJson('GET', ut.test_uri, offer['currentRevision']['href'])

        return {
            "developer": user,
            "organization": organization,
            "item": item,
            "itemRev": itemRev,
            "offer": offer,
            "offerRev": offerRev
        }

    def testCheckout(self):
        user = oauth.testRegister('identity identity.pii commerce commerce.checkout', True)
        devinfo = self.testDeveloper()

        name = curlJson('POST', ut.test_uri, '/v1/personal-info', headers = {
            "Authorization": "Bearer " + user.access_token
        }, data = {
            "type": "NAME",
            "value": {
                "givenName": "Johann",
                "middleName": None,
                "familyName": "Smith"
            },
            "user": user.json['self']
        })

        address = curlJson('POST', ut.test_uri, '/v1/personal-info', headers = {
            "Authorization": "Bearer " + user.access_token
        }, data = {
            "type": "ADDRESS",
            "user": user.json['self'],
            "value": {
                "street1": "800 West Campbell Road",
                "city": "Richardson",
                "subCountry": "TX",
                "country": {
                    "id": "US"
                },
                "postalCode": "75080"
            }
        })

        user.json.update({
            "name": name['self'],
            "addresses": [{
                "value": address['self'],
                "isDefault": True,
                "label": None
            }]
        })
        user.json = curlJson('PUT', ut.test_uri, user.href, headers = {
            "Authorization": "Bearer " + user.access_token
        }, data = user.json)

        cartUrl = curlRedirect('GET', ut.test_uri, user.href + '/carts/primary', headers = {
            "Authorization": "Bearer " + user.access_token
        })
        cartId = cartUrl.rsplit('/')[-1]

        cart = curlJson('GET', ut.test_uri, user.href + '/carts/' + cartId, headers = {
            "Authorization": "Bearer " + user.access_token
        })

        cart['offers'] = [{
            "offer": devinfo['offer']['self'],
            "quantity": 1,
            "isSelected": True,
            "isApproved": True
        }]

        cart = curlJson('PUT', ut.test_uri, cart['self']['href'], headers = {
            "Authorization": "Bearer " + user.access_token
        }, data = cart)

        piTypes = curlJson('GET', ut.test_uri, '/v1/payment-instrument-types')
        ccPiType = [ piType for piType in piTypes['results'] if piType['typeCode'] == 'CREDITCARD' ][0]

        cardInfo = self.getFBEncryptedCardInfo()
        pi = curlJson('POST', ut.test_uri, '/v1/payment-instruments', headers = {
            "Authorization": "Bearer " + user.access_token
        }, data = {
            "type": ccPiType['self'],
            "accountName": cardInfo['holderName'],
            "accountNumber": cardInfo['encryptedCardInfo'],
            "label": "my credit card",
            "user": user.json['self'],
            "billingAddress": address['self'],
            "typeSpecificDetails":{
                "expireDate":"2025-11"
            },
            "riskFeature":{
                "timeSinceUserAccountCreatedInDays":"10",
                "sourceDatr":"test_datr",
                "sourceCountry":"US",
                "currencyPurchasing":"USD"
            },
            "futureExpansion": { }
        })

        order = curlJson('POST', ut.test_uri, '/v1/orders', headers = {
            "Authorization": "Bearer " + user.access_token, 
            "oculus-end-user-ip": "127.0.0.1",
             "X-CLIENT-NAME": "OculusStore",
             "X-CLIENT-VERSION": "1.0.0",
             "X-PLATFORM-NAME": "Android",
             "X-PLATFORM-VERSION": "4.4.2"
        }, data = {
            "user": user.json['self'],
            "tentative": True,
            "countryOfPurchase": {
                "href": "/v1/countries/US",
                "id": "US"
            },
            "currency": {
                "href": "/v1/currencies/USD",
                "id": "USD"
            },
            "locale": {
                "href": "/v1/locales/en_US",
                "id": "en_US"
            },
            "shippingMethod": None,
            "orderItems": [{
                "offer": devinfo['offer']['self'],
                "quantity": 1
            }],
            "payments": [{
                "paymentInstrument": pi['self'],
            }],
            "futureExpansion": { }
        })

        order['tentative'] = False
        order = curlJson('PUT', ut.test_uri, order['self']['href'], headers = {
            "Authorization": "Bearer " + user.access_token,
            "oculus-end-user-ip": "127.0.0.1",
            "oculus-geoip-country-code": "US",
            "X-CLIENT-NAME": "OculusStore",
            "X-CLIENT-VERSION": "1.0.0",
            "X-PLATFORM-NAME": "Android",
            "X-PLATFORM-VERSION": "4.4.2"
        }, data = order)

        fulfilmentToken = oauth.getServiceAccessToken('fulfilment.service')
        fulfilment = curlJson('GET', ut.test_uri, '/v1/fulfilments', query = {
            'orderId': order['self']['id']
        }, headers = {
            "Authorization": "Bearer " + fulfilmentToken
        })
        fulfilmentAction = fulfilment['items'][0]['fulfilmentActions'][0]
        self.assertEqual(fulfilmentAction['type'], 'GRANT_ENTITLEMENT')
        self.assertEqual(fulfilmentAction['status'], 'SUCCEED')

        entitlementIds = fulfilmentAction['result']['entitlementIds']

        entitlements = []
        for entitlementId in entitlementIds:
            entitlement = curlJson('GET', ut.test_uri, '/v1/entitlements/' + entitlementId, headers = {
                "Authorization": "Bearer " + user.access_token
            })
            entitlements.append(entitlement)

        # try to get entitlements directly
        entitlementSearchResults = curlJson('GET', ut.test_uri, '/v1/entitlements', query = {
            "userId": user.json['self']['id'],
            "itemId": devinfo['item']['self']['id']
        }, headers = {
            "Authorization": "Bearer " + user.access_token
        })
        assert len(entitlementSearchResults['results']) == 1
        searchEntitlementIds = [ entitlement['self']['id'] for entitlement in entitlementSearchResults['results'] ]
        self.assertSetEqual(set(entitlementIds), set(searchEntitlementIds))

        return order

    def testCatalogGetAll(self):
        # verify get all items and offers
        curlJson('GET', ut.test_uri, '/v1/items')
        curlJson('GET', ut.test_uri, '/v1/item-revisions')
        curlJson('GET', ut.test_uri, '/v1/offers')
        curlJson('GET', ut.test_uri, '/v1/offer-revisions')

    # sample data
    def getConsumableItemRevision(self, item, developer):
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
                    "name": "utItem_CartCheckout_Consumable1",
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

    def getConsumableOfferRevision(self, offer, item, developer):
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
                    "name": "utOffer_CartCheckout_Consumable1",
                    "videos": [
                        {
                            "id": "id of the video",
                            "service": "YOUTUBE",
                            "title": "title of the video"
                        }
                    ],
                    "longDescription": "long description"
                }
            },
            "eventActions": {
                "PURCHASE": [ {
                    "type": "GRANT_ENTITLEMENT",
                    "condition" : None,
                    "itemId": item['self'],
                    "storedValueCurrency" : None,
                    "storedValueAmount" : None,
                    "useCount" : 999,
                    "price" : None
                 }]
            }
        }

    def getFBDefaultCardInfo(self):
        return {
            "number": "4111117711552927",
            "holderName": "John Doe",
            "cvc": "123",
            "expiryMonth": "06",
            "expiryYear": "2016"
        }

    def getFBEncryptedCardInfo(self, cardInfo = None):
        if cardInfo is None:
            cardInfo = self.getFBDefaultCardInfo()

        tokenResult = curl('POST', 'https://www.facebook.com', '/payments/generate_token', headers = {
            'User-Agent': 'curl/7.30.0'
        }, body = "creditCardNumber=4111117711552927&csc=123", proxy = os.getenv('facebookProxy'))

        jsonResult = json.loads(tokenResult)

        cardInfo['encryptedCardInfo'] = jsonResult['token']
        return cardInfo


    def getDefaultCardInfo(self):
        return {
            "number": "5555444433331111",
            "holderName": "John Doe",
            "cvc": "737",
            "expiryMonth": "06",
            "expiryYear": "2016",
            "generationtime": datetime.utcnow().strftime("%Y-%m-%dT%H:%M:%S.000Z")
        }

    def getEncryptedCardInfo(self, cardInfo = None):
        if cardInfo is None:
            cardInfo = self.getDefaultCardInfo()
        cardInfoStr = json.dumps(cardInfo)

        process = Popen(['node', 'adyen-cse.js'], stdin = PIPE, stdout = PIPE, stderr = PIPE)
        stdout, stderr = process.communicate(cardInfoStr)
        returnCode = process.returncode

        self.assertEqual(returnCode, 0, "Failed to call 'node adyen-cse.js', is node.js installed?")

        cardInfo['encryptedCardInfo'] = stdout
        return cardInfo

if __name__ == '__main__':
    silkcloud_utmain()
