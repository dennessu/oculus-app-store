#!/usr/bin/python
import silkcloudut as ut
from silkcloudut import *

class OAuthTests(ut.TestBase):
    def testRegister(self, scope = 'identity'):
        ut.cookies.clear()
        location = curlRedirect('GET', ut.test_uri, '/v1/oauth2/authorize', query = {
            'client_id': ut.test_client_id,
            'response_type': 'code',
            'scope': scope,
            'redirect_uri': ut.test_redirect_uri
        }, headers = {'oculus-internal': 'true'})
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
            view = curlJson('GET', ut.test_uri, '/v1/oauth2/authorize', query = { 'cid': cid })
            self.assertEqual(view["view"], 'TFARequiredView')

            location = curlFormRedirect('POST', ut.test_uri, '/v1/oauth2/authorize',
                                        data={'cid': cid, 'event': 'verifyPIN', 'pin': user.pin})

        auth_code = getqueryparam(location, 'code')
        assert auth_code is not None

        response = curlForm('POST', ut.test_uri, '/v1/oauth2/token', data = {
            'code': auth_code,
            'client_id': ut.test_client_id,
            'client_secret': ut.test_client_secret,
            'grant_type': 'authorization_code',
            'redirect_uri': ut.test_redirect_uri
        }, headers = {'oculus-internal': 'true'})
        access_token = response["access_token"]
        refresh_token = response["refresh_token"]
        auth_header = { 'Authorization': 'Bearer ' + access_token }
        token_info = curlJson('GET', ut.test_uri, '/v1/oauth2/tokeninfo', query = { 'access_token': access_token })
        user_href = token_info['sub']['href']

        user_info = curlJson('GET', ut.test_uri, user_href, headers = auth_header)
        user_name_personal_info = curlJson('GET', ut.test_uri, str(user_info['username']['href']), headers = auth_header)
        assert user_name_personal_info['value']['userName'] == user.username

        # add additional information to user
        user.id = token_info['sub']['id']
        user.href = user_href
        user.json = user_info
        user.access_token = access_token
        user.refresh_token = refresh_token
        return user

    def testRegisterWithTosChallenge(self, scope = 'identity'):
        ut.cookies.clear()
        location = curlRedirect('GET', ut.test_uri, '/v1/oauth2/authorize', query = {
            'client_id': ut.test_client_id,
            'response_type': 'code',
            'scope': scope,
            'redirect_uri': ut.test_redirect_uri
        }, headers = {'oculus-internal': 'true'})
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
            'pin': user.pin,
            'country': 'US'
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
        self.assertEqual(view["view"], 'tosChallenge')

        view = curlForm('POST', ut.test_uri, '/v1/oauth2/authorize', data = {
            'cid': cid,
            'event': 'next',
            'tos': view['model']['tosChallenge'][0]['href']
        })
        assert view["view"] == 'redirect'
        link = view["model"]["location"]
        cid = getqueryparam(link, 'cid')

        location = curlRedirect('GET', ut.test_uri, '/v1/oauth2/authorize', query = { 'cid': cid})
        auth_code = getqueryparam(location, 'code')
        assert auth_code is not None

        response = curlForm('POST', ut.test_uri, '/v1/oauth2/token', data = {
            'code': auth_code,
            'client_id': ut.test_client_id,
            'client_secret': ut.test_client_secret,
            'grant_type': 'authorization_code',
            'redirect_uri': ut.test_redirect_uri
        }, headers = {'oculus-internal': 'true'})
        access_token = response["access_token"]
        refresh_token = response["refresh_token"]
        auth_header = { 'Authorization': 'Bearer ' + access_token }
        token_info = curlJson('GET', ut.test_uri, '/v1/oauth2/tokeninfo', query = { 'access_token': access_token })
        user_href = token_info['sub']['href']

        user_info = curlJson('GET', ut.test_uri, user_href, headers = auth_header)
        user_name_personal_info = curlJson('GET', ut.test_uri, str(user_info['username']['href']), headers = auth_header)
        assert user_name_personal_info['value']['userName'] == user.username

        pass

    def testSilentLoginWithTosChallenge(self):
        user = self.testRegister()

        # update the user country to US to trigger tos challenge
        user.json['cor'] = {
            "href": "/v1/countries/US",
            "id": "US"
        }

        resp = curlJson('PUT', ut.test_uri, user.href, data = user.json, headers = {
            'Authorization': 'Bearer ' + user.access_token
        })

        # silent login
        location = curlRedirect('GET', ut.test_uri, '/v1/oauth2/authorize', query = {
            'client_id': ut.test_client_id,
            'response_type': 'token',
            'scope': 'identity openid',
            'redirect_uri': ut.test_redirect_uri,
            'state': 'testState',
            'nonce': 'testNonce',
            'prompt': 'none'
        }, headers = {
            'oculus-internal': 'true'
        })

        assert location == 'http://localhost#error=tos_challenge_required&state=testState'

        # silent login with prompt=tos
        location = curlRedirect('GET', ut.test_uri, '/v1/oauth2/authorize', query = {
            'client_id': ut.test_client_id,
            'response_type': 'token',
            'scope': 'identity openid',
            'redirect_uri': ut.test_redirect_uri,
            'state': 'testState',
            'nonce': 'testNonce',
            'prompt': 'tos'
        }, headers = {
            'oculus-internal': 'true'
        })

        cid = getqueryparam(location, 'cid')
        view = curlJson('GET', ut.test_uri, '/v1/oauth2/authorize', query = { 'cid': cid })
        self.assertEqual(view["view"], 'tosChallenge')

        # accept the tos
        view = curlForm('POST', ut.test_uri, '/v1/oauth2/authorize', data = {
            'cid': cid,
            'event': 'next',
            'tos': view['model']['tosChallenge'][0]['href']
        })
        assert view["view"] == 'redirect'
        link = view["model"]["location"]
        cid = getqueryparam(link, 'cid')

        location = curlRedirect('GET', ut.test_uri, '/v1/oauth2/authorize', query = { 'cid': cid})

        access_token = getqueryparam(location, 'access_token')
        assert access_token is not None

    def testLoginWithTosChallenge(self):
        user = self.testRegister()

        # test silent sign-in
        location = curlRedirect('GET', ut.test_uri, '/v1/oauth2/authorize', query = {
            'client_id': ut.test_client_id,
            'response_type': 'token id_token',
            'scope': 'identity openid',
            'redirect_uri': ut.test_redirect_uri,
            'state': 'testState',
            'nonce': 'testNonce'
        }, headers = {
            'oculus-internal': 'true'
        })

        id_token = getqueryparam(location, 'id_token')
        assert id_token is not None

        # logout
        location = curlRedirect('GET', ut.test_uri, '/v1/oauth2/end-session', query = {
            'post_logout_redirect_uri': ut.test_logout_redirect_uri,
            'id_token_hint': id_token
        })
        assert location.startswith(ut.test_logout_redirect_uri)

        # update the user country to US to trigger tos challenge
        user.json['cor'] = {
            "href": "/v1/countries/US",
            "id": "US"
        }

        resp = curlJson('PUT', ut.test_uri, user.href, data = user.json, headers = {
            'Authorization': 'Bearer ' + user.access_token
        })

        # test login again
        location = curlRedirect('GET', ut.test_uri, '/v1/oauth2/authorize', query = {
            'client_id': ut.test_client_id,
            'response_type': 'token',
            'scope': 'identity openid',
            'redirect_uri': ut.test_redirect_uri,
            'state': 'testState',
            'nonce': 'testNonce'
        }, headers = {
            'oculus-internal': 'true'
        })

        cid = getqueryparam(location, 'cid')
        view = curlJson('GET', ut.test_uri, '/v1/oauth2/authorize', query = { 'cid': cid })
        self.assertEqual(view["view"], 'login')

        view = curlForm('POST', ut.test_uri, '/v1/oauth2/authorize', data = {
            'cid': cid,
            'event': 'next',
            'username': user.email,
            'password': user.password
        })

        self.assertEqual(view["view"], 'tosChallenge')

        # accept the tos
        view = curlForm('POST', ut.test_uri, '/v1/oauth2/authorize', data = {
            'cid': cid,
            'event': 'next',
            'tos': view['model']['tosChallenge'][0]['href']
        })
        assert view["view"] == 'redirect'
        location = curlRedirect('GET', ut.test_uri, '/v1/oauth2/authorize', query = { 'cid': cid})

        access_token = getqueryparam(location, 'access_token')
        assert access_token is not None

    def testLogin(self):
        user = self.testRegister()

        # test silent sign-in
        location = curlRedirect('GET', ut.test_uri, '/v1/oauth2/authorize', query = {
            'client_id': ut.test_client_id,
            'response_type': 'token id_token',
            'scope': 'identity openid',
            'redirect_uri': ut.test_redirect_uri,
            'state': 'testState',
            'nonce': 'testNonce'
        }, headers = {
            'oculus-internal': 'true'
        })

        id_token = getqueryparam(location, 'id_token')
        assert id_token is not None

        # logout
        location = curlRedirect('GET', ut.test_uri, '/v1/oauth2/end-session', query = {
            'post_logout_redirect_uri': ut.test_logout_redirect_uri,
            'id_token_hint': id_token
        })
        assert location.startswith(ut.test_logout_redirect_uri)

        # test login again
        location = curlRedirect('GET', ut.test_uri, '/v1/oauth2/authorize', query = {
            'client_id': ut.test_client_id,
            'response_type': 'token',
            'scope': 'identity openid',
            'redirect_uri': ut.test_redirect_uri,
            'state': 'testState',
            'nonce': 'testNonce'
        }, headers = {
            'oculus-internal': 'true'
        })

        cid = getqueryparam(location, 'cid')
        view = curlJson('GET', ut.test_uri, '/v1/oauth2/authorize', query = { 'cid': cid })
        self.assertEqual(view["view"], 'login')

        view = curlForm('POST', ut.test_uri, '/v1/oauth2/authorize', data = {
            'cid': cid,
            'event': 'next',
            'username': user.email,
            'password': user.password
        })
        assert view["view"] == 'redirect'
        link = view["model"]["location"]
        cid = getqueryparam(link, 'cid')

        location = curlRedirect('GET', ut.test_uri, '/v1/oauth2/authorize', query = { 'cid': cid})

        access_token = getqueryparam(location, 'access_token')
        assert access_token is not None

    def testLogout(self):
        user = self.testRegister()

        # test silent sign-in
        location = curlRedirect('GET', ut.test_uri, '/v1/oauth2/authorize', query = {
            'client_id': ut.test_client_id,
            'response_type': 'token id_token',
            'scope': 'identity openid',
            'redirect_uri': ut.test_redirect_uri,
            'state': 'testState',
            'nonce': 'testNonce'
        }, headers = {
            'oculus-internal': 'true'
        })

        id_token = getqueryparam(location, 'id_token')
        access_token = getqueryparam(location, 'access_token')
        assert id_token is not None
        assert access_token is not None

        # logout
        location = curlRedirect('GET', ut.test_uri, '/v1/oauth2/end-session', query = {
            'post_logout_redirect_uri': ut.test_logout_redirect_uri,
            'id_token_hint': id_token
        })
        assert location.startswith(ut.test_logout_redirect_uri)

        # verify that all access tokens generated by this login state has been revoked
        error = curlJson('GET', ut.test_uri, '/v1/oauth2/tokeninfo?access_token=' + user.access_token, raiseOnError=False)
        assert error['message'] == 'Invalid Access Token'

        error = curlJson('GET', ut.test_uri, '/v1/oauth2/tokeninfo?access_token=' + access_token, raiseOnError=False)
        assert error['message'] == 'Invalid Access Token'


        # test silent sign-in again
        location = curlRedirect('GET', ut.test_uri, '/v1/oauth2/authorize', query = {
            'client_id': ut.test_client_id,
            'response_type': 'code',
            'scope': 'identity',
            'redirect_uri': ut.test_redirect_uri
        }, headers = {
            'oculus-internal': 'true'
        })
        assert getqueryparam(location, 'code') is None
        assert getqueryparam(location, 'cid') is not None
        pass

    def testLogoutConfirm(self):
        user = self.testRegister()

        # test silent sign-in
        location = curlRedirect('GET', ut.test_uri, '/v1/oauth2/authorize', query = {
            'client_id': ut.test_client_id,
            'response_type': 'token id_token',
            'scope': 'identity openid',
            'redirect_uri': ut.test_redirect_uri,
            'state': 'testState',
            'nonce': 'testNonce'
        }, headers = {
            'oculus-internal': 'true'
        })
        id_token = getqueryparam(location, 'id_token')
        assert id_token is not None

        # logout without id_token
        location = curlRedirect('GET', ut.test_uri, '/v1/oauth2/end-session', query = {
            'post_logout_redirect_uri': ut.test_logout_redirect_uri
        })
        cid = getqueryparam(location, 'cid')
        view = curlJson('GET', ut.test_uri, '/v1/oauth2/end-session', query = { 'cid': cid })
        assert view["view"] == 'logout-confirm'

        # confirm logout with 'no'
        curl('GET', ut.test_uri, '/v1/oauth2/end-session', query = { 'cid': cid, 'event': 'no' })

        # test silent sign-in
        location = curlRedirect('GET', ut.test_uri, '/v1/oauth2/authorize', query = {
            'client_id': ut.test_client_id,
            'response_type': 'token id_token',
            'scope': 'identity openid',
            'redirect_uri': ut.test_redirect_uri,
            'state': 'testState',
            'nonce': 'testNonce'
        }, headers = {
            'oculus-internal': 'true'
        })
        id_token = getqueryparam(location, 'id_token')
        assert id_token is not None

        # logout without id_token
        location = curlRedirect('GET', ut.test_uri, '/v1/oauth2/end-session', query = {
            'post_logout_redirect_uri': ut.test_logout_redirect_uri
        })
        cid = getqueryparam(location, 'cid')
        view = curlJson('GET', ut.test_uri, '/v1/oauth2/end-session', query = { 'cid': cid })
        assert view["view"] == 'logout-confirm'

        # confirm logout with 'yes'
        curl('GET', ut.test_uri, '/v1/oauth2/end-session', query = { 'cid': cid, 'event': 'yes' })

        # test sign-in again to confirm logout
        location = curlRedirect('GET', ut.test_uri, '/v1/oauth2/authorize', query = {
            'client_id': ut.test_client_id,
            'response_type': 'code',
            'scope': 'identity',
            'redirect_uri': ut.test_redirect_uri
        }, headers = {
            'oculus-internal': 'true'
        })
        assert getqueryparam(location, 'code') is None
        assert getqueryparam(location, 'cid') is not None

        pass

    def testLogoutConfirm(self):
        user = self.testRegister()

        # test silent sign-in
        location = curlRedirect('GET', ut.test_uri, '/v1/oauth2/authorize', query = {
            'client_id': ut.test_client_id,
            'response_type': 'token id_token',
            'scope': 'identity openid',
            'redirect_uri': ut.test_redirect_uri,
            'state': 'testState',
            'nonce': 'testNonce'
        }, headers = {
            'oculus-internal': 'true'
        })
        id_token = getqueryparam(location, 'id_token')
        assert id_token is not None

        # logout without id_token
        location = curlRedirect('GET', ut.test_uri, '/v1/oauth2/end-session', query = {
            'post_logout_redirect_uri': ut.test_logout_redirect_uri
        })
        cid = getqueryparam(location, 'cid')
        view = curlJson('GET', ut.test_uri, '/v1/oauth2/end-session', query = { 'cid': cid })
        assert view["view"] == 'logout-confirm'

        # confirm logout with 'no'
        curl('GET', ut.test_uri, '/v1/oauth2/end-session', query = { 'cid': cid, 'event': 'no' })

        # test silent sign-in
        location = curlRedirect('GET', ut.test_uri, '/v1/oauth2/authorize', query = {
            'client_id': ut.test_client_id,
            'response_type': 'token id_token',
            'scope': 'identity openid',
            'redirect_uri': ut.test_redirect_uri,
            'state': 'testState',
            'nonce': 'testNonce'
        }, headers = {
            'oculus-internal': 'true'
        })
        id_token = getqueryparam(location, 'id_token')
        assert id_token is not None

        # logout without id_token
        location = curlRedirect('GET', ut.test_uri, '/v1/oauth2/end-session', query = {
            'post_logout_redirect_uri': ut.test_logout_redirect_uri
        })
        cid = getqueryparam(location, 'cid')
        view = curlJson('GET', ut.test_uri, '/v1/oauth2/end-session', query = { 'cid': cid })
        assert view["view"] == 'logout-confirm'

        # confirm logout with 'yes'
        curl('GET', ut.test_uri, '/v1/oauth2/end-session', query = { 'cid': cid, 'event': 'yes' })

        # test silent sign-in again
        location = curlRedirect('GET', ut.test_uri, '/v1/oauth2/authorize', query = {
            'client_id': ut.test_client_id,
            'response_type': 'code',
            'scope': 'identity',
            'redirect_uri': ut.test_redirect_uri
        }, headers = {
            'oculus-internal': 'true'
        })
        assert getqueryparam(location, 'code') is None
        assert getqueryparam(location, 'cid') is not None

        pass

    def testValidWildcardRedirectUri(self):
        user = self.testRegister()

        location = curlRedirect('GET', ut.test_uri, '/v1/oauth2/authorize', query = {
            'client_id': ut.test_client_id,
            'response_type': 'code',
            'scope': 'identity',
            'redirect_uri': 'https://www.oculus.com/abc'
        }, headers = {
            'oculus-internal': 'true'
        })

        auth_code = getqueryparam(location, 'code')
        assert auth_code is not None
        pass

    def testInvalidRedirectUri(self):
        user = self.testRegister()

        response = curlJson('GET', ut.test_uri, '/v1/oauth2/authorize', query = {
            'client_id': ut.test_client_id,
            'response_type': 'token id_token',
            'scope': 'identity openid',
            'redirect_uri': 'https://abc.a.com/oculus.com/',
            'state': 'testState',
            'nonce': 'testNonce'
        }, headers = {
            'oculus-internal': 'true'
        }, raiseOnError = False)

        assert response['message'] == 'Input Error'
        assert response['details'][0]['field'] == 'redirect_uri'

        response = curlJson('GET', ut.test_uri, '/v1/oauth2/authorize', query = {
            'client_id': ut.test_client_id,
            'response_type': 'token id_token',
            'scope': 'identity openid',
            'redirect_uri': 'https://abc.a.com/oculus.com/',
            'state': 'testState',
            'nonce': 'testNonce'
        }, headers = {
            'oculus-internal': 'true'
        }, raiseOnError = False)

        assert response['message'] == 'Input Error'
        assert response['details'][0]['field'] == 'redirect_uri'
        pass

    def testWildcardLogout(self):
        user = self.testRegister()

        # test silent sign-in
        location = curlRedirect('GET', ut.test_uri, '/v1/oauth2/authorize', query = {
            'client_id': ut.test_client_id,
            'response_type': 'token id_token',
            'scope': 'identity openid',
            'redirect_uri': ut.test_redirect_uri,
            'state': 'testState',
            'nonce': 'testNonce'
        }, headers = {
            'oculus-internal': 'true'
        })

        id_token = getqueryparam(location, 'id_token')
        assert id_token is not None

        # logout
        location = curlRedirect('GET', ut.test_uri, '/v1/oauth2/end-session', query = {
            'post_logout_redirect_uri': ut.test_wildcard_logout_redirect_uri,
            'id_token_hint': id_token
        })
        assert location.startswith(ut.test_wildcard_logout_redirect_uri)

        # test sign-in again to confirm logout
        location = curlRedirect('GET', ut.test_uri, '/v1/oauth2/authorize', query = {
            'client_id': ut.test_client_id,
            'response_type': 'code',
            'scope': 'identity',
            'redirect_uri': ut.test_redirect_uri
        }, headers = {
            'oculus-internal': 'true'
        })
        assert getqueryparam(location, 'code') is None
        assert getqueryparam(location, 'cid') is not None
        pass

    def testSilentLogin(self):
        user = self.testRegister()

        # test silent sign-in
        location = curlRedirect('GET', ut.test_uri, '/v1/oauth2/authorize', query = {
            'client_id': ut.test_client_id,
            'response_type': 'code',
            'scope': 'identity openid',
            'redirect_uri': ut.test_redirect_uri,
            'state': 'testState',
            'nonce': 'testNonce'
        }, headers = {
            'oculus-internal': 'true'
        })
        auth_code = getqueryparam(location, 'code')
        assert auth_code is not None

        response = curlForm('POST', ut.test_uri, '/v1/oauth2/token', data = {
            'code': auth_code,
            'client_id': ut.test_client_id,
            'client_secret': ut.test_client_secret,
            'grant_type': 'authorization_code',
            'redirect_uri': ut.test_redirect_uri
        }, headers = {
            'oculus-internal': 'true'
        })
        access_token = response["access_token"]
        assert access_token is not None
        id_token = response["id_token"]
        assert id_token is not None
        auth_header = { 'Authorization': 'Bearer ' + access_token }
        token_info = curlJson('GET', ut.test_uri, '/v1/oauth2/tokeninfo', query = { 'access_token': access_token })
        user_href = token_info['sub']['href']
        user_info = curlJson('GET', ut.test_uri, user_href, headers = auth_header)
        user_name_personal_info = curlJson('GET', ut.test_uri, str(user_info['username']['href']), headers = auth_header)
        assert user_name_personal_info['value']['userName'] == user.username

        # logout
        curl('GET', ut.test_uri, '/v1/oauth2/end-session', query = {
            'post_logout_redirect_uri': ut.test_redirect_uri,
            'id_token_hint': id_token
        })

        # test silent sign-in again
        location = curlRedirect('GET', ut.test_uri, '/v1/oauth2/authorize', query = {
            'client_id': ut.test_client_id,
            'response_type': 'code',
            'scope': 'identity',
            'redirect_uri': ut.test_redirect_uri
        }, headers = {
            'oculus-internal': 'true'
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
        }, headers = {
            'oculus-internal': 'true'
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
        location = curlRedirect('GET', ut.test_uri, '/v1/oauth2/verify-email', query = { 'evc': evc })
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
        user_name_personal_info = curlJson('GET', ut.test_uri, str(user_info['username']['href']), headers = auth_header)
        assert user_name_personal_info['value']['userName'] == user.username

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
            'response_type': 'token id_token',
            'scope': 'identity openid',
            'redirect_uri': ut.test_redirect_uri,
            'state': nounce,
            'nonce': 'testNonce'
        }, headers = {
            'oculus-internal': 'true'
        })

        id_token = getqueryparam(location, 'id_token')
        assert id_token is not None

        access_token = getqueryparam(location, 'access_token')
        assert nounce == getqueryparam(location, 'state')
        auth_header = { 'Authorization': 'Bearer ' + access_token }
        token_info = curlJson('GET', ut.test_uri, '/v1/oauth2/tokeninfo', query = { 'access_token': access_token })
        user_href = token_info['sub']['href']
        user_info = curlJson('GET', ut.test_uri, user_href, headers = auth_header)
        user_name_personal_info = curlJson('GET', ut.test_uri, str(user_info['username']['href']), headers = auth_header)
        assert user_name_personal_info['value']['userName'] == user.username

        # logout
        curl('GET', ut.test_uri, '/v1/oauth2/end-session', query = {
            'post_logout_redirect_uri': ut.test_redirect_uri,
            'id_token_hint': id_token
        })

        # test silent sign-in again
        nounce = randomstr(10)
        location = curlRedirect('GET', ut.test_uri, '/v1/oauth2/authorize', query = {
            'client_id': ut.test_client_id,
            'response_type': 'token',
            'scope': 'identity',
            'redirect_uri': ut.test_redirect_uri,
            'state': nounce
        }, headers = {
            'oculus-internal': 'true'
        })

        assert getqueryparam(location, 'access_token') is None
        assert getqueryparam(location, 'cid') is not None
        pass

    def testResetPassword(self):
        user = self.testRegisterImplicitFlow()

        # Get another access token
        location = curlRedirect('GET', ut.test_uri, '/v1/oauth2/authorize', query = {
            'client_id': ut.test_client_id,
            'response_type': 'token',
            'scope': 'identity',
            'redirect_uri': ut.test_redirect_uri,
            'state': 'testState'
        }, headers = {
            'oculus-internal': 'true'
        })
        access_token = getqueryparam(location, 'access_token')

        resetUrl, resp = curlFormRaw('POST', ut.test_uri, '/v1/oauth2/reset-password', headers = {
            'Authorization': 'Bearer ' + user.access_token
        }, data = {
            'user_email': user.email,
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

        time.sleep(3)
        # the access token should have been revoked
        error = curlJson('GET', ut.test_uri, '/v1/oauth2/tokeninfo?access_token=' + user.access_token, raiseOnError=False)
        assert error['message'] == 'Invalid Access Token'

        # the another access token should have been revoked
        error = curlJson('GET', ut.test_uri, '/v1/oauth2/tokeninfo?access_token=' + access_token, raiseOnError=False)
        assert error['message'] == 'Invalid Access Token'

        # the login state should have been deleted, the user need to login again
        location = curlRedirect('GET', ut.test_uri, '/v1/oauth2/authorize', query = {
            'client_id': ut.test_client_id,
            'response_type': 'token',
            'scope': 'identity',
            'redirect_uri': ut.test_redirect_uri,
            'state': 'testState'
        }, headers = {
            'oculus-internal': 'true'
        })

        assert getqueryparam(location, 'access_token') is None
        assert getqueryparam(location, 'cid') is not None
        pass

    def testClientGroup(self):
        user = self.testRegister()

        adminToken = self.getServiceAccessToken('identity.service')

        organization = curlJson('GET', ut.test_uri, '/v1/organizations?name=Oculus', headers = {
            "Authorization": "Bearer " + adminToken
        })

        groups = curlJson('GET', ut.test_uri, '/v1/groups?name=ShareAdmin&organizationId=' + organization['results'][0]['self']['id'], headers = {
            "Authorization": "Bearer " + adminToken
        })

        group = groups['results'][0]

        # create the group membership
        groupMembership = curlJson('POST', ut.test_uri, '/v1/user-group-memberships', headers =  {
            "Authorization": "Bearer " + adminToken
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

        location = curlRedirect('GET', ut.test_uri, '/v1/oauth2/authorize', query = {
            'client_id': 'share-admin-tool',
            'response_type': 'token id_token',
            'scope': 'identity',
            'redirect_uri': ut.test_redirect_uri,
            'state': 'test'
        }, headers = {'oculus-internal': 'true'})

        assert getqueryparam(location, 'access_token') is not None
        assert getqueryparam(location, 'cid') is None

        id_token = getqueryparam(location, 'id_token')

        # logout
        curl('GET', ut.test_uri, '/v1/oauth2/end-session', query = {
            'post_logout_redirect_uri': ut.test_redirect_uri,
            'id_token_hint': id_token
        })

        # create normal user
        user1 = self.testRegister()

        # try get token for share-admin-tool
        location = curlRedirect('GET', ut.test_uri, '/v1/oauth2/authorize', query = {
            'client_id': 'share-admin-tool',
            'response_type': 'token',
            'scope': 'identity',
            'redirect_uri': ut.test_redirect_uri,
            'state': 'test'
        }, headers = {'oculus-internal': 'true'})

        # expect access_denied error
        assert location.endswith('denied/')
        pass

    def testInvalidScope(self):
        user = self.testRegister()

        adminToken = self.getServiceAccessToken('identity.service')

        organization = curlJson('GET', ut.test_uri, '/v1/organizations?name=Oculus', headers = {
            "Authorization": "Bearer " + adminToken
        })

        groups = curlJson('GET', ut.test_uri, '/v1/groups?name=IdentityAdmin&organizationId=' + organization['results'][0]['self']['id'], headers = {
            "Authorization": "Bearer " + adminToken
        })

        group = groups['results'][0]

        # create the group membership
        groupMembership = curlJson('POST', ut.test_uri, '/v1/user-group-memberships', headers =  {
            "Authorization": "Bearer " + adminToken
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

        # test get access token with a user in group IdentityAdmin
        location = curlRedirect('GET', ut.test_uri, '/v1/oauth2/authorize', query = {
            'client_id': 'curationTool',
            'response_type': 'token id_token',
            'scope': 'scope.manage catalog.admin',
            'redirect_uri': ut.test_redirect_uri,
            'state': 'test'
        }, headers = {'oculus-internal': 'true'})

        assert getqueryparam(location, 'access_token') is not None
        assert getqueryparam(location, 'cid') is None

        tokenInfo = curlJson('GET', ut.test_uri, '/v1/oauth2/tokeninfo?access_token=' + getqueryparam(location, 'access_token'))
        # should only get scope.manage scope
        assert 'scope.manage' in tokenInfo['scopes']
        assert 'catalog.admin' not in tokenInfo['scopes']

        groups = curlJson('GET', ut.test_uri, '/v1/groups?name=CatalogAdmin&organizationId=' + organization['results'][0]['self']['id'], headers = {
            "Authorization": "Bearer " + adminToken
        })

        group = groups['results'][0]

        # create the group membership
        groupMembership = curlJson('POST', ut.test_uri, '/v1/user-group-memberships', headers =  {
            "Authorization": "Bearer " + adminToken
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

        # test get access token with a user in group IdentityAdmin
        location = curlRedirect('GET', ut.test_uri, '/v1/oauth2/authorize', query = {
            'client_id': 'curationTool',
            'response_type': 'token id_token',
            'scope': 'scope.manage catalog.admin',
            'redirect_uri': ut.test_redirect_uri,
            'state': 'test'
        }, headers = {'oculus-internal': 'true'})

        assert getqueryparam(location, 'access_token') is not None
        assert getqueryparam(location, 'cid') is None

        tokenInfo = curlJson('GET', ut.test_uri, '/v1/oauth2/tokeninfo?access_token=' + getqueryparam(location, 'access_token'))
        # should have scope.manage
        assert 'scope.manage' in tokenInfo['scopes']
        assert 'catalog.admin' in tokenInfo['scopes']

        # create normal user
        user1 = self.testRegister()

        # try get token for curationTool with a normal user
        location = curlRedirect('GET', ut.test_uri, '/v1/oauth2/authorize', query = {
            'client_id': 'curationTool',
            'response_type': 'token id_token',
            'scope': 'identity scope.manage catalog.admin',
            'redirect_uri': ut.test_redirect_uri,
            'state': 'test'
        }, headers = {'oculus-internal': 'true'})
        # expect access_denied error
        assert location.endswith('denied/')

    # disable this case
    # def testInternalClient(self):
    #     error = curlForm('POST', ut.test_uri, '/v1/oauth2/token', data = {
    #         'client_id': ut.test_service_client_id,
    #         'client_secret': ut.test_service_client_secret,
    #         'scope': 'identity.service',
    #         'grant_type': 'client_credentials'
    #     }, raiseOnError = False)
    #     assert error['message'] == 'Forbidden'
    #
    #     error = curlForm('POST', ut.test_uri, '/v1/oauth2/token', data = {
    #         'client_id': ut.test_service_client_id,
    #         'client_secret': ut.test_service_client_secret,
    #         'scope': 'identity.service',
    #         'grant_type': 'client_credentials'
    #     }, headers = {
    #         'oculus-internal': 'false'
    #     }, raiseOnError = False)
    #     assert error['message'] == 'Forbidden'
    #
    #     error = curlForm('POST', ut.test_uri, '/v1/oauth2/token', data = {
    #         'client_id': ut.test_service_client_id,
    #         'client_secret': ut.test_service_client_secret,
    #         'scope': 'identity.service',
    #         'grant_type': 'client_credentials'
    #     }, headers = {
    #         'oculus-internal': 'arbitrary_value'
    #     }, raiseOnError = False)
    #     assert error['message'] == 'Forbidden'
    #
    #     token = curlForm('POST', ut.test_uri, '/v1/oauth2/token', data = {
    #         'client_id': ut.test_service_client_id,
    #         'client_secret': ut.test_service_client_secret,
    #         'scope': 'identity.service',
    #         'grant_type': 'client_credentials'
    #     }, headers = {
    #         'oculus-internal': 'true'
    #     })
    #     assert token['access_token'] is not None
    #     pass

    def testConversationWithDifferentIp(self, scope = 'identity'):
        ut.cookies.clear()
        location = curlRedirect('GET', ut.test_uri, '/v1/oauth2/authorize', query = {
            'client_id': ut.test_client_id,
            'response_type': 'code',
            'scope': scope,
            'redirect_uri': ut.test_redirect_uri
        }, headers = {'oculus-internal': 'true'})
        cid = getqueryparam(location, 'cid')

        error = curlJson('GET', ut.test_uri, '/v1/oauth2/authorize', query = { 'cid': cid }, headers = {
            'oculus-end-user-ip': '1.2.3.4'
        }, raiseOnError=False)
        assert error['details'][0]['reason'] == 'Conversation Ip Violation'
        pass

    def testInvalidRedirectUri(self, scope = 'identity'):
        ut.cookies.clear()
        error = curlJson('GET', ut.test_uri, '/v1/oauth2/authorize', query = {
            'client_id': ut.test_client_id,
            'response_type': 'code',
            'scope': scope,
            'redirect_uri': 'https://.google.c.om.oculus.com'
        }, raiseOnError=False)
        assert error['details'][0]['field'] == 'redirect_uri'
        assert error['details'][0]['reason'] == 'Field value is invalid. https://.google.c.om.oculus.com'

        error = curlJson('GET', ut.test_uri, '/v1/oauth2/authorize', query = {
            'client_id': ut.test_client_id,
            'response_type': 'code',
            'scope': scope,
            'redirect_uri': 'https:///auth'
        }, raiseOnError=False)
        assert error['details'][0]['field'] == 'redirect_uri'
        assert error['details'][0]['reason'] == 'Field value is invalid. https:///auth'

        error = curlJson('GET', ut.test_uri, '/v1/oauth2/authorize', query = {
            'client_id': ut.test_client_id,
            'response_type': 'code',
            'scope': scope,
            'redirect_uri': 'https://.oculus.com/'
        }, raiseOnError=False)
        assert error['details'][0]['field'] == 'redirect_uri'
        assert error['details'][0]['reason'] == 'Field value is invalid. https://.oculus.com/'

        error = curlJson('GET', ut.test_uri, '/v1/oauth2/authorize', query = {
            'client_id': ut.test_client_id,
            'response_type': 'code',
            'scope': scope,
            'redirect_uri': 'https:/oculus.com/'
        }, raiseOnError=False)
        assert error['details'][0]['field'] == 'redirect_uri'
        assert error['details'][0]['reason'] == 'Field value is invalid. https:/oculus.com/'
        pass

    def testConversationWithInvalidEvent(self, scope = 'identity'):
        ut.cookies.clear()
        location = curlRedirect('GET', ut.test_uri, '/v1/oauth2/authorize', query = {
            'client_id': ut.test_client_id,
            'response_type': 'code',
            'scope': scope,
            'redirect_uri': ut.test_redirect_uri
        }, headers = {'oculus-internal': 'true'})
        cid = getqueryparam(location, 'cid')

        view = curlJson('GET', ut.test_uri, '/v1/oauth2/authorize', query = { 'cid': cid })
        assert view["view"] == 'login'

        error = curlForm('POST', ut.test_uri, '/v1/oauth2/authorize', data = { 'cid': cid, 'event': 'invalid' },
                         raiseOnError=False)
        assert error['details'][0]['reason'] == 'no transition found for event invalid'

    def testGetCountries(self):
        return curlJson('GET', ut.test_uri, '/v1/countries')

    def testTfa(self):
        # TODO: test modify user PII and sign-up using TFA flow
        pass

    def genUserInfo(self):
        user = Object()

        user.username = randomstr(1, string.ascii_lowercase) + randomstr(10)
        user.password = randompwd()
        user.email = 'silkcloudtest+' + randomstr(1, string.ascii_letters) + randomstr(10) + '@gmail.com'
        user.nickname = randomstr(1, string.ascii_letters) + randomstr(10)
        user.first_name = randomstr(10)
        user.last_name = randomstr(10)
        user.gender = 'male'
        user.dob = '1980-01-01'
        user.pin = '1234'

        return user

    def getServiceAccessToken(self, scope = 'identity.service'):
        token = curlForm('POST', ut.test_uri, '/v1/oauth2/token', data = {
            'client_id': ut.test_service_client_id,
            'client_secret': ut.test_service_client_secret,
            'scope': scope,
            'grant_type': 'client_credentials'
        }, headers = {'oculus-internal': 'true'})
        return token['access_token']

if __name__ == '__main__':
    silkcloud_utmain()
