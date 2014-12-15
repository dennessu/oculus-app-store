#!/usr/bin/python
import silkcloudut as ut
import ut_oauth
from silkcloudut import *
from datetime import datetime
import uuid
import json
import ut_checkout
import hmac
import hashlib
import base64


oauth = ut_oauth.OAuthTests('testRegister')
checkout = ut_checkout.CheckoutTests('getEncryptedCardInfo')
class PaymentTests(ut.TestBase):

    def testProxy(self):
        curlRaw('GET', 'https://www.facebook.com', proxy = os.getenv('facebookProxy'))

    def createUserAndAddress(self, paymentToken):
        user = oauth.testRegister('identity commerce')
        name = curlJson('POST', ut.test_uri, '/v1/personal-info', headers={
            "Authorization": "Bearer " + paymentToken
        }, data={
            "type": "NAME",
            "value": {
                "givenName": "Johann",
                "middleName": None,
                "familyName": "Smith"
            },
            "user": user.json['self']
        })
        address = curlJson('POST', ut.test_uri, '/v1/personal-info', headers={
            "Authorization": "Bearer " + paymentToken
        }, data={
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
        user.json = curlJson('PUT', ut.test_uri, user.href, headers={
            "Authorization": "Bearer " + paymentToken
        }, data=user.json)
        return address, user

    def testAdyenCreditCard(self):
        paymentToken = oauth.getServiceAccessToken('payment.service identity.service')
        #prepare user:
        address, user = self.createUserAndAddress(paymentToken)

        piTypes = curlJson('GET', ut.test_uri, '/v1/payment-instrument-types')
        ccPiType = [ piType for piType in piTypes['results'] if piType['typeCode'] == 'CREDITCARD' ][0]

        cardInfo = checkout.getEncryptedCardInfo()
        pi = curlJson('POST', ut.test_uri, '/v1/payment-instruments', headers = {
            "Authorization": "Bearer " + user.access_token
        }, data = {
            "type": ccPiType['self'],
            "accountName": cardInfo['holderName'],
            "accountNumber": cardInfo['encryptedCardInfo'],
            "label": "my credit card",
            "user": user.json['self'],
            "billingAddress": address['self'],
            "futureExpansion": { }
        })

        #auth the pi
        authPI = curlJson('POST', ut.test_uri, '/v1/payment-transactions/authorization', headers = {
            "Authorization": "Bearer " + paymentToken
        }, data = {
            "trackingUuid": str(uuid.uuid1()),
            "user": user.json['self'],
            "billingRefId": "ut-testCreditcard",
            "paymentInstrument":pi['self'],
            "chargeInfo":{
                "currency":"USD",
                "amount":"100"
            },
            "futureExpansion": { }
        })
        assert authPI['status'] == 'AUTHORIZED'
        #capture the pi(partial)
        capturePI = curlJson('POST', ut.test_uri, '/v1/payment-transactions/' + authPI['self']['id'] + '/capture', headers = {
            "Authorization": "Bearer " + paymentToken
        }, data = {
            "trackingUuid": str(uuid.uuid1()),
            "user": user.json['self'],
            "billingRefId": "ut-testCreditcard",
            "paymentInstrument":pi['self'],
            "chargeInfo":{
                "currency":"USD",
                "amount":"10"
            },
            "futureExpansion": { }
        })
        assert capturePI['status'] == 'SETTLEMENT_SUBMITTED'

        chargePI = curlJson('POST', ut.test_uri, '/v1/payment-transactions/charge', headers = {
            "Authorization": "Bearer " + paymentToken
        }, data = {
            "trackingUuid": str(uuid.uuid1()),
            "user": user.json['self'],
            "billingRefId": "ut-testCreditcard",
            "paymentInstrument":pi['self'],
            "chargeInfo":{
                "currency":"USD",
                "amount":"100"
            },
            "futureExpansion": { }
        })
        assert authPI['status'] == 'SETTLEMENT_SUBMITTED'

        refundPI = curlJson('PUT', ut.test_uri, '/v1/payment-transactions/' + chargePI['self']['id'] + '/refund', headers = {
            "Authorization": "Bearer " + paymentToken
        }, data = {
            "trackingUuid": str(uuid.uuid1()),
            "user": user.json['self'],
            "billingRefId": "ut-testCreditcard",
            "paymentInstrument":pi['self'],
            "chargeInfo":{
                "currency":"USD",
                "amount":"100"
            },
            "futureExpansion": { }
        })
        assert authPI['status'] == 'REFUNDED'

        refundPI = curlJson('PUT', ut.test_uri, '/v1/payment-transactions/' + chargePI['self']['id'] + '/refund', headers = {
            "Authorization": "Bearer " + paymentToken
        }, data = {
            "trackingUuid": str(uuid.uuid1()),
            "user": user.json['self'],
            "billingRefId": "ut-testCreditcard",
            "paymentInstrument":pi['self'],
            "chargeInfo":{
                "currency":"USD",
                "amount":"100"
            },
            "futureExpansion": { }
        })

        #get the payment details
        getPayment = curlJson('GET', ut.test_uri, '/v1/payment-transactions/' + chargePI['self']['id'], headers = {
            "Authorization": "Bearer " + paymentToken
        })
        #charge 2, refund 2, refund 2
        assert len(getPayment['paymentEvents']) == 6

    def testAdyenWebPayment(self):
        paymentToken = oauth.getServiceAccessToken('payment.service identity.service')
        #prepare user:
        address, user = self.createUserAndAddress(paymentToken)

        piTypes = curlJson('GET', ut.test_uri, '/v1/payment-instrument-types')
        svPiType = [ piType for piType in piTypes['results'] if piType['typeCode'] == 'OTHERS' ][0]
        #add pi
        pi = curlJson('POST', ut.test_uri, '/v1/payment-instruments', headers = {
            "Authorization": "Bearer " + paymentToken
        }, data = {
            "type": svPiType['self'],
            "accountName": 'test',
            "accountNumber": 'zwh@123.com',
            "label": "my paypal",
            "user": user.json['self'],
            "billingAddress": address['self'],
            "futureExpansion": { }
        })

        #get the pi details
        piId = pi['self']['id']
        getPI = curlJson('GET', ut.test_uri, '/v1/payment-instruments/' + piId, headers = {
            "Authorization": "Bearer " + paymentToken
        })
        assert getPI['self']['id'] == piId

         #charge the pi
        chargePI = curlJson('POST', ut.test_uri, '/v1/payment-transactions/charge', headers = {
            "Authorization": "Bearer " + paymentToken
        }, data = {
            "trackingUuid": str(uuid.uuid1()),
            "user": user.json['self'],
            "billingRefId": "ut-testStoredValue",
            "paymentInstrument":pi['self'],
            "chargeInfo":{
                "currency":"USD",
                "amount":"100"
            },
            "webPaymentInfo":{
                "returnURL":"http://www.abc.com/",
                "cancelURL":"http://www.abc.com/"
            },
            "futureExpansion": { }
        })
        assert len(chargePI['webPaymentInfo']['redirectURL']) > 1

        #get payment status
        getPayment = curlJson('GET', ut.test_uri, '/v1/payment-transactions/' + chargePI['self']['id'], headers = {
            "Authorization": "Bearer " + paymentToken
        })
        assert getPayment['status'] == 'UNCONFIRMED'

        #mock the callback properties
        strToSign="AUTHORISEDut1234" + chargePI['self']['id'] + "0ceFRQOp3650-3399-5423";
        dig = hmac.new(b'1234', msg=strToSign.encode(), digestmod=hashlib.sha1).digest()
        sign = base64.b64encode(dig)
        print 'sign:' + sign
        callbackRequest = 'provider=Adyen&merchantReference=' + chargePI['self']['id'] + '&skinCode=0ceFRQOp&shopperLocale=en_GB' + '&paymentMethod=unionpay&authResult=AUTHORISED&pspReference=ut1234&merchantReturnData=3650-3399-5423' + '&merchantSig=' + sign

        addProperties = curl('POST', ut.test_uri, '/v1/payment-callback/properties', headers = {
            "Authorization": "Bearer " + paymentToken
        }, body = callbackRequest)

        #get payment status
        getPayment = curlJson('GET', ut.test_uri, '/v1/payment-transactions/' + chargePI['self']['id'], headers = {
            "Authorization": "Bearer " + paymentToken
        })
        assert getPayment['status'] == 'SETTLEMENT_SUBMITTED'

    def testStoredValue(self):
        paymentToken = oauth.getServiceAccessToken('payment.service identity.service')
        #prepare user:
        address, user = self.createUserAndAddress(paymentToken)

        piTypes = curlJson('GET', ut.test_uri, '/v1/payment-instrument-types')
        svPiType = [ piType for piType in piTypes['results'] if piType['typeCode'] == 'STOREDVALUE' ][0]
        #add storedvalue pi
        pi = curlJson('POST', ut.test_uri, '/v1/payment-instruments', headers = {
            "Authorization": "Bearer " + paymentToken
        }, data = {
            "type": svPiType['self'],
            "accountName": 'test',
            "label": "my stored value",
            "user": user.json['self'],
            "billingAddress": address['self'],
            "typeSpecificDetails":{
                "storedValueCurrency": {
                    "href": "/v1/currencies/USD",
                    "id": "USD"
                },
            },
            "futureExpansion": { }
        })

        #credit storedvalue
        creditPI = curlJson('POST', ut.test_uri, '/v1/payment-transactions/credit', headers = {
            "Authorization": "Bearer " + paymentToken
        }, data = {
            "trackingUuid": str(uuid.uuid1()),
            "user": user.json['self'],
            "billingRefId": "ut-testStoredValue",
            "paymentInstrument":pi['self'],
            "chargeInfo":{
                "currency":"USD",
                "amount":"1000"
            },
            "futureExpansion": { }
        })
        #get the pi details
        piId = pi['self']['id']
        getPI = curlJson('GET', ut.test_uri, '/v1/payment-instruments/' + piId, headers = {
            "Authorization": "Bearer " + paymentToken
        })
        self.assertEqual(getPI['typeSpecificDetails']['storedValueBalance'], 1000, "Failed to credit balance for stored value")
        #charge the pi
        creditPI = curlJson('POST', ut.test_uri, '/v1/payment-transactions/charge', headers = {
            "Authorization": "Bearer " + paymentToken
        }, data = {
            "trackingUuid": str(uuid.uuid1()),
            "user": user.json['self'],
            "billingRefId": "ut-testStoredValue",
            "paymentInstrument":pi['self'],
            "chargeInfo":{
                "currency":"USD",
                "amount":"100"
            },
            "futureExpansion": { }
        })
        #get the pi details
        getPI = curlJson('GET', ut.test_uri, '/v1/payment-instruments/' + piId, headers = {
            "Authorization": "Bearer " + paymentToken
        })
        self.assertEqual(getPI['typeSpecificDetails']['storedValueBalance'], 900, "Failed to credit balance for stored value")

    def testPaypal(self):
        paymentToken = oauth.getServiceAccessToken('payment.service identity.service')
        #prepare user:
        address, user = self.createUserAndAddress(paymentToken)

        piTypes = curlJson('GET', ut.test_uri, '/v1/payment-instrument-types')
        svPiType = [ piType for piType in piTypes['results'] if piType['typeCode'] == 'PAYPAL' ][0]
        #add paypal pi
        pi = curlJson('POST', ut.test_uri, '/v1/payment-instruments', headers = {
            "Authorization": "Bearer " + paymentToken
        }, data = {
            "type": svPiType['self'],
            "accountName": 'test',
            "accountNumber": 'zwh@123.com',
            "label": "my paypal",
            "user": user.json['self'],
            "billingAddress": address['self'],
            "futureExpansion": { }
        })

        #get the pi details
        piId = pi['self']['id']
        getPI = curlJson('GET', ut.test_uri, '/v1/payment-instruments/' + piId, headers = {
            "Authorization": "Bearer " + paymentToken
        })
        assert getPI['self']['id'] == piId

         #charge the pi
        chargePI = curlJson('POST', ut.test_uri, '/v1/payment-transactions/charge', headers = {
            "Authorization": "Bearer " + paymentToken
        }, data = {
            "trackingUuid": str(uuid.uuid1()),
            "user": user.json['self'],
            "billingRefId": "ut-testStoredValue",
            "paymentInstrument":pi['self'],
            "chargeInfo":{
                "currency":"USD",
                "amount":"100"
            },
            "webPaymentInfo":{
                "returnURL":"http://www.abc.com/",
                "cancelURL":"http://www.abc.com/"
            },
            "futureExpansion": { }
        })
        assert len(chargePI['webPaymentInfo']['redirectURL']) > 1
        #mock the payment by add callback properties
        callbackRequest = "provider=PayPal&paymentId="
        callbackRequest += chargePI['self']['id']
        callbackRequest += "&token=" + chargePI['webPaymentInfo']['token']
        callbackRequest += "&billingId=123&PayerID=CCZA9BJT9NKTS"

        addProperties = curl('POST', ut.test_uri, '/v1/payment-callback/properties', headers = {
            "Authorization": "Bearer " + paymentToken
        }, body = callbackRequest)
        #check status
        checkStatus = curl('POST', ut.test_uri, '/v1/payment-transactions/'+ chargePI['self']['id'] + '/check', headers = {
            "Authorization": "Bearer " + paymentToken
        })
        checkResult = json.loads(checkStatus)
        assert checkResult['status'] == 'UNCONFIRMED'
        #TODO: need manual confirm the redirectURL and then call confirm.


    def testSearchPIwithoutExternalToken(self):
        paymentToken = oauth.getServiceAccessToken('payment.service identity.service')
        #prepare user:
        address, user = self.createUserAndAddress(paymentToken)

        piTypes = curlJson('GET', ut.test_uri, '/v1/payment-instrument-types')
        adyenPiType = [ piType for piType in piTypes['results'] if piType['typeCode'] == 'OTHERS' ][0]
        #add storedvalue pi
        pi = curlJson('POST', ut.test_uri, '/v1/payment-instruments', headers = {
            "Authorization": "Bearer " + paymentToken
        }, data = {
            "type": adyenPiType['self'],
            "accountName": 'test',
            "label": "my stored value",
            "user": user.json['self'],
            "billingAddress": address['self'],
            "futureExpansion": { }
        })

        #get the pi details
        piId = pi['self']['id']
        getPI = curlJson('GET', ut.test_uri, '/v1/payment-instruments/' + piId, headers = {
            "Authorization": "Bearer " + paymentToken
        })
        assert getPI['self']['id'] == piId

        #search won't show the PI without external token
        searchPI = curlJson('GET', ut.test_uri, '/v1/payment-instruments', query = {
            "userId": user.json['self']['id']
        },headers = {
            "Authorization": "Bearer " + paymentToken
        })
        assert len(searchPI['results']) == 0
		
if __name__ == '__main__':
    silkcloud_utmain()
            
        
