import argparse
import httplib
import json
import os
import random
import re
import string
import sys
import time
import unittest

from cookielib import CookieJar
from urllib import urlencode

def silkcloud_utmain():
    # Enforce python version
    if sys.version_info[0] != 2 or sys.version_info[1] < 7:
        error("The script only works in python 2.x where x >= 7")

    # Enforce to script directory
    currentDir = os.path.dirname(os.path.realpath(__file__))
    try:
        os.chdir(currentDir)
    except OSError as e:
        error(e + "\nUnable to change current directory to : " + currentDir + ". Aborting...")

    parser = argparse.ArgumentParser()
    parser.add_argument("-uri", nargs = '?', help = "The URI to the silkcloud service.", default = 'http://localhost:8080/')
    parser.add_argument("-client", nargs = '?', help = "The client ID used in test cases.", default = 'client')
    parser.add_argument("-secret", nargs = '?', help = "The client secret used in the test cases.", default = 'secret')
    parser.add_argument("-sleep", nargs = '?', help = "The sleep between API calls.")
    parser.add_argument('tests', metavar='test', nargs='*', help='The test cases to run.')

    opts = parser.parse_args()

    global test_uri
    global test_client_id
    global test_client_secret
    global test_redirect_uri
    global test_sleep
    global cookies

    test_uri = opts.uri
    test_client_id = opts.client
    test_client_secret = opts.secret
    test_redirect_uri = 'http://localhost'
    test_sleep = opts.sleep

    cookies = CookieJar()

    setVerbose(True)

    unittest.main(argv = [ sys.argv[0] ] + opts.tests)

def curlRedirect(method, baseUrl, url = None, query = None, headers = None, body = None, raiseOnError = True):
    body, resp = curlRaw(method, baseUrl, url, query, headers, body, raiseOnError)
    if resp.status < 300:
        raise Exception('Expected redirect. Actual: %s %s in %s %s\n%s' % (resp.status, resp.reason, method, url, body))
    locations = [v for k, v in resp.getheaders() if k.lower() == 'location']
    if len(locations) != 1:
        raise Exception('Unexpected location header count: %s, locations: %s' % (len(locations), locations))
    return locations[0]

def curlForm(method, baseUrl, url = None, query= None, headers = None, data = None, raiseOnError = True):
    resp = curlFormRaw(method, baseUrl, url, query, headers, data, raiseOnError)
    return json.loads(resp)

def curlFormRaw(method, baseUrl, url = None, query= None, headers = None, data = None, raiseOnError = True):
    if data is None: data = {}
    if headers is None:
        headers = {
            'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
        }

    body = urlencode(data)
    return curl(method, baseUrl, url, query, headers, body, raiseOnError)

def curlJson(method, baseUrl, url = None, query= None, headers = None, data = None, raiseOnError = True):
    if headers is None:
        headers = {
            'Content-Type': 'application/json'
        }

    body = None
    if data is not None:
        body = json.dumps(data)
    response = curl(method, baseUrl, url, query, headers, body, raiseOnError)
    return json.loads(response)

def curl(method, baseUrl, url = None, query= None, headers = None, body = None, raiseOnError = True):
    if headers is None: headers = {}

    body, resp = curlRaw(method, baseUrl, url, query, headers, body, raiseOnError)
    return body

def curlRaw(method, baseUrl, url = None, query= None, headers = None, body = None, raiseOnError = True):
    global cookies

    url = combineUrl(baseUrl, url, query)
    if headers is None: headers = {}

    conn = None
    start_time = time.time()
    try:
        urlRegex = r'^(?P<protocol>http[s]?://)?((?P<userpass>([^/@:]*):([^/@:]*))@)?(?P<host>[^/:]+)(:(?P<port>\d+))?(?P<path>(/|\?).*)$'
        m = re.match(urlRegex, url)
        if m is None:
            raise Exception('Invalid url: ' + url)

        protocol = m.group('protocol')
        host = m.group('host')
        port = m.group('port')
        path = m.group('path')

        userpass = m.group('userpass')

        verbose("[req] " + method + " " + url)
        for k, v in headers.items():
            verbose("[req][header] %s: %s" % (k, v))
        if body: verbose("[req][body] " + body)

        if port:
            port = int(port)
        if protocol == "https://":
            conn = httplib.HTTPSConnection(host, port)
        else:
            conn = httplib.HTTPConnection(host, port)
 
        if userpass:
            import base64
            base64String = base64.encodestring(userpass).strip()
            authheader = "Basic %s" % base64String
            headers['Authorization'] = authheader

        # process cookies
        request = HttpRequest(protocol, host, port, path, headers)
        cookies.add_cookie_header(request)
        headers = request.get_headers()

        # uncomment to turn on trace
        # conn.set_debuglevel(1)      # turn on trace
        conn.request(method, path, body, headers)
        resp = conn.getresponse()
        body = resp.read()
        verbose("[resp] %d %s" % (resp.status, resp.reason))
        for k, v in resp.getheaders():
            verbose("[resp][headers] %s: %s" % (k, v))

        if body:
            verbose("[resp][body] " + body)
        verbose("")
        
        if resp.status >= 400 and raiseOnError:
            raise Exception('%s %s in %s %s\n%s' % (resp.status, resp.reason, method, url, body))

        # HACK: pretend we are urllib2 response
        resp.info = lambda : resp.msg
        cookies.extract_cookies(resp, request)

        if test_sleep:
            time.sleep(test_sleep)

        return (body, resp)
    finally:
        if conn:
            conn.close()
        elapsed_time = time.time() - start_time
        verbose("[resp][elapsed] %dms" % (elapsed_time * 1000))

class HttpRequest:
    """
    HACK: Data container for HTTP request (used for cookie processing).
    http://stackoverflow.com/questions/1016765/how-to-keep-alive-with-cookielib-and-httplib-in-python
    """

    def __init__(self, protocol, host, port, url, headers = None):
        self._protocol = protocol
        self._host = host
        self._port = port
        self._url = url
        self._headers = {}
        for key, value in headers.items():
            self.add_header(key, value)

    def has_header(self, name):
        return name in self._headers

    def add_header(self, key, val):
        self._headers[key.capitalize()] = val

    def add_unredirected_header(self, key, val):
        self._headers[key.capitalize()] = val

    def is_unverifiable(self):
        return True

    def get_type(self):
        return self._protocol[:self._protocol.index(':')]

    def get_full_url(self):
        full_url = self._protocol + self._host
        if self._port is not None:
            full_url += ":" + str(self._port)
        full_url + self._url
        return full_url

    def get_header(self, header_name, default = None):
        return self._headers.get(header_name, default)

    def get_host(self):
        return self._host

    get_origin_req_host = get_host

    def get_headers(self):
        return self._headers

class Object:
    """
    Empty class. We can add arbitrary attribute to it.
    """
    def __init__(self, **entries):
        self.__dict__.update(entries)

    def __getitem__(self, item):
        return self.__dict__[item]

    def __getattr__(self, item):
        return None

def getqueryparam(url, param):
    match = re.search(r'\b%s=(?P<value>[^&]+)' % param, url)
    if match is not None:
        return match.group('value')
    return None

def randomstr(length, charset = None):
    if charset is None:
        charset = string.ascii_lowercase + string.digits
    return ''.join(random.choice(charset) for _ in range(length))

def combineUrl(baseUrl, relativeUrl, queryParams):
    url = baseUrl
    if relativeUrl is not None:
        url = url.rstrip('/') + '/' + relativeUrl.lstrip('/')
    if queryParams is not None:
        url += '?' + urlencode(queryParams)
    return url

def safeStrip(value):
    if value is not None:
        return value.strip()
    else:
        return None

def xstr(s):
    if s is None:
        return ''
    return str(s)

def isNoneOrEmpty(value):
    return value is None or len(value) == 0

gIsVerbose = False
def setVerbose(isVerbose):
    global gIsVerbose
    gIsVerbose = isVerbose

def verbose(message):
    global gIsVerbose
    if gIsVerbose:
        info(message)

def info(message):
    print message
    sys.stdout.flush()

def error(message):
    sys.stderr.write("ERROR: " + message + "\n")
    sys.exit(1)