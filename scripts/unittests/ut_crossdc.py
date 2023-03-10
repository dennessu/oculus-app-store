#!/usr/bin/python
import silkcloudut as ut
from silkcloudut import *

class CrossDCTests(ut.TestBase):
    def testRegister(self, scope = 'identity'):
        ut.cookies.clear()
        location = curlRedirect('GET', ut.test_uri, '/v1/oauth2/authorize', query = {
            'client_id': ut.test_client_id,
            'response_type': 'code',
            'scope': scope,
            'redirect_uri': ut.test_redirect_uri
        })
        cid = getqueryparam(location, 'cid')

        view = curlJson('GET', ut.test_uri, '/v1/oauth2/authorize', query = { 'cid': cid })
        assert view["view"] == 'login'

        view = curlForm('POST', ut.test_uri, '/v1/oauth2/authorize', data = { 'cid': cid, 'event': 'register' })
        assert view["view"] == 'register'

        # generate user info        
        user = self.genUserInfo()

        view = curlForm('POST', ut.test_uri, '/v1/oauth2/authorize', data = {
            'cid': cid,
            'event': 'next',
            'username': user.username,
            'password': user.password,
            'email': user.email,
            'nickname': user.nickname,
            'first_name': user.first_name,
            'last_name': user.last_name,
            'gener': user.gender,
            'dob': user.dob,
            'pin': user.pin
        })
        assert view["view"] == 'payment-method'

        # skip the payment-method view
        view = curlForm('POST', ut.test_uri, '/v1/oauth2/authorize', data = { 'cid': cid, 'event': 'skip' })
        assert view["view"] == 'emailVerifyRequired'

        # click the email verification link
        link = view["model"]["link"]
        evc = getqueryparam(link, 'evc')
        curl('GET', ut.test_uri, '/v1/oauth2/verify-email', query = { 'evc': evc })

        view = curlForm('POST', ut.test_uri, '/v1/oauth2/authorize', data = { 'cid': cid, 'event': 'next' })
        assert view["view"] == 'redirect'
        link = view["model"]["location"]
        cid = getqueryparam(link, 'cid')

        location = curlRedirect('GET', ut.test_uri, '/v1/oauth2/authorize', query = { 'cid': cid})
        cid = getqueryparam(location, 'cid')
        if cid:
            # the TFA flow
            # TODO:
            view = curlJson('GET', ut.test_uri, '/v1/oauth2/authorize', query = { 'cid': cid })
            self.assertEqual(view["view"], 'TFARequiredView')
            pass

        auth_code = getqueryparam(location, 'code')
        assert auth_code is not None

        response = curlForm('POST', ut.test_uri, '/v1/oauth2/token', data = {
            'code': auth_code,
            'client_id': ut.test_client_id,
            'client_secret': ut.test_client_secret,
            'grant_type': 'authorization_code',
            'redirect_uri': ut.test_redirect_uri
        })
        access_token = response["access_token"]
        auth_header = { 'Authorization': 'Bearer ' + access_token }
        token_info = curlJson('GET', ut.test_remote_uri, '/v1/oauth2/tokeninfo', query = { 'access_token': access_token })
        user_href = token_info['sub']['href']

        user_info = curlJson('GET', ut.test_uri, user_href, headers = auth_header)

        name = curlJson('POST', ut.test_uri, '/v1/personal-info', headers = auth_header, data = {
            "type": "NAME",
            "value": {
                "givenName": "Johann",
                "middleName": None,
                "familyName": "Smith"
            },
            "user": user_info['self']
        })

        address = curlJson('POST', ut.test_uri, '/v1/personal-info', headers = auth_header, data = {
            "type": "ADDRESS",
            "user": user_info['self'],
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

        user_info.update({
            "name": name['self'],
            "addresses": [{
                "value": address['self'],
                "isDefault": True,
                "label": None
            }]
        })
        user_info = curlJson('PUT', ut.test_uri, user_info['self']['href'], headers = auth_header, data = user_info)

        name = curlJson('GET', ut.test_remote_uri, name['self']['href'], headers = auth_header)
        address = curlJson('GET', ut.test_remote_uri, address['self']['href'], headers = auth_header)
        user_info = curlJson('GET', ut.test_remote_uri, user_href, headers = auth_header)

    def genUserInfo(self):
        user = Object()
        
        user.username = randomstr(1, string.ascii_lowercase) + randomstr(10)
        user.password = randompwd()
        user.email = 'silkcloudtest' + randomstr(1, string.ascii_letters) + randomstr(10) + '@silkcloud.com'
        user.nickname = randomstr(1, string.ascii_letters) + randomstr(10)
        user.first_name = randomstr(10)
        user.last_name = randomstr(10)
        user.gender = 'male'
        user.dob = '1980-01-01'
        user.pin = '1234'

        return user

if __name__ == '__main__':
    silkcloud_utmain()
