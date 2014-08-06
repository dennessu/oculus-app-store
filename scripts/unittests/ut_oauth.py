#!/usr/bin/python
import silkcloudut as ut
from silkcloudut import *

class OAuthTests(unittest.TestCase):
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
        curl('GET', link)

        view = curlForm('POST', ut.test_uri, '/v1/oauth2/authorize', data = { 'cid': cid, 'event': 'next' })
        assert view["view"] == 'redirect'
        link = view["model"]["location"]

        location = curlRedirect('GET', link)
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
        refresh_token = response["refresh_token"]
        auth_header = { 'Authorization': 'Bearer ' + access_token }
        token_info = curlJson('GET', ut.test_uri, '/v1/oauth2/tokeninfo', query = { 'access_token': access_token })
        user_href = token_info['sub']['href']

        user_info = curlJson('GET', ut.test_uri, user_href, headers = auth_header)
        assert user.username == user_info['username']

        # add additional information to user
        user.id = token_info['sub']['id']
        user.href = user_href
        user.json = user_info
        user.access_token = access_token
        user.refresh_token = refresh_token
        return user

    def testSilentLogin(self):
        user = self.testRegister()

        # test silent sign-in
        location = curlRedirect('GET', ut.test_uri, '/v1/oauth2/authorize', query = {
            'client_id': ut.test_client_id,
            'response_type': 'code',
            'scope': 'identity',
            'redirect_uri': ut.test_redirect_uri
        })
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
        token_info = curlJson('GET', ut.test_uri, '/v1/oauth2/tokeninfo', query = { 'access_token': access_token })
        user_href = token_info['sub']['href']
        user_info = curlJson('GET', ut.test_uri, user_href, headers = auth_header)
        assert user.username == user_info['username']

        # logout
        curl('GET', ut.test_uri, '/v1/oauth2/end-session', query = {
            'post_logout_redirect_uri': ut.test_redirect_uri
        })

        # test silent sign-in again
        location = curlRedirect('GET', ut.test_uri, '/v1/oauth2/authorize', query = {
            'client_id': ut.test_client_id,
            'response_type': 'code',
            'scope': 'identity',
            'redirect_uri': ut.test_redirect_uri
        })
        assert getqueryparam(location, 'code') is None
        assert getqueryparam(location, 'cid') is not None

        pass

    def testRegisterImplicitFlow(self):
        ut.cookies.clear()

        nounce = randomstr(10)
        location = curlRedirect('GET', ut.test_uri, '/v1/oauth2/authorize', query = {
            'client_id': ut.test_client_id,
            'response_type': 'token',
            'scope': 'identity',
            'redirect_uri': ut.test_redirect_uri,
            'state': nounce
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
        location = curlRedirect('GET', link)
        assert location is not None

        view = curlForm('POST', ut.test_uri, '/v1/oauth2/authorize', data = { 'cid': cid, 'event': 'next' })
        assert view["view"] == 'redirect'
        link = view["model"]["location"]

        location = curlRedirect('GET', link)
        assert nounce == getqueryparam(location, 'state')
        access_token = getqueryparam(location, 'access_token')

        auth_header = { 'Authorization': 'Bearer ' + access_token }
        token_info = curlJson('GET', ut.test_uri, '/v1/oauth2/tokeninfo', query = { 'access_token': access_token })
        user_href = token_info['sub']['href']

        user_info = curlJson('GET', ut.test_uri, user_href, headers = auth_header)
        assert user.username == user_info['username']

        # add additional information to user
        user.id = token_info['sub']['id']
        user.access_token = access_token
        return user

    def testSilentLoginImplicitFlow(self):
        user = self.testRegisterImplicitFlow()

        # test silent sign-in
        nounce = randomstr(10)
        location = curlRedirect('GET', ut.test_uri, '/v1/oauth2/authorize', query = {
            'client_id': ut.test_client_id,
            'response_type': 'token',
            'scope': 'identity',
            'redirect_uri': ut.test_redirect_uri,
            'state': nounce
        })
        access_token = getqueryparam(location, 'access_token')
        assert nounce == getqueryparam(location, 'state')
        auth_header = { 'Authorization': 'Bearer ' + access_token }
        token_info = curlJson('GET', ut.test_uri, '/v1/oauth2/tokeninfo', query = { 'access_token': access_token })
        user_href = token_info['sub']['href']
        user_info = curlJson('GET', ut.test_uri, user_href, headers = auth_header)
        assert user.username == user_info['username']

        # logout
        curl('GET', ut.test_uri, '/v1/oauth2/end-session', query = {
            'post_logout_redirect_uri': ut.test_redirect_uri
        })

        # test silent sign-in again
        nounce = randomstr(10)
        location = curlRedirect('GET', ut.test_uri, '/v1/oauth2/authorize', query = {
            'client_id': ut.test_client_id,
            'response_type': 'token',
            'scope': 'identity',
            'redirect_uri': ut.test_redirect_uri,
            'state': nounce
        })

        assert getqueryparam(location, 'access_token') is None
        assert getqueryparam(location, 'cid') is not None
        pass

    def testResetPassword(self):
        user = self.testRegisterImplicitFlow()
        resetUrl = curlFormRaw('POST', ut.test_uri, '/v1/oauth2/reset-password', data = {
            'username': user.username,
            'locale': 'en_US'
        })
        location = curlRedirect('GET', resetUrl)
        cid = getqueryparam(location, 'cid')

        view = curlJson('GET', ut.test_uri, '/v1/oauth2/reset-password', query = { 'cid': cid })
        assert view["view"] == 'reset_password'

        user.password = randompwd()
        view = curlForm('POST', ut.test_uri, '/v1/oauth2/reset-password', data = {
            'cid': cid,
            'event': 'next',
            'password': user.password
        })
        assert view["view"] == 'reset_password_result'
        assert view["model"]["reset_password_success"] == "true"
        pass

    def testGetCountries(self):
        return curlJson('GET', ut.test_uri, '/v1/countries')

    def testTfa(self):
        # TODO: test modify user PII and sign-up using TFA flow
        pass

    def genUserInfo(self):
        user = Object()
        
        user.username = randomstr(1, string.ascii_lowercase) + randomstr(10)
        user.password = randompwd()
        user.email = randomstr(1, string.ascii_letters) + randomstr(10) + '@silkcloud.com'
        user.nickname = randomstr(1, string.ascii_letters) + randomstr(10)
        user.first_name = randomstr(10)
        user.last_name = randomstr(10)
        user.gender = 'male'
        user.dob = '1980-01-01'
        user.pin = '123456'

        return user

    def getServiceAccessToken(self, scope = 'identity.service'):
        token = curlForm('POST', ut.test_uri, '/v1/oauth2/token', data = {
            'client_id': ut.test_service_client_id,
            'client_secret': ut.test_service_client_secret,
            'scope': scope,
            'grant_type': 'client_credentials'
        })
        return token['access_token']

if __name__ == '__main__':
    silkcloud_utmain()
