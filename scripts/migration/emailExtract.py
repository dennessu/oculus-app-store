#!/usr/bin/python

import sys
import os
import re
import httplib
import string
import json
import time
import argparse
import random
from threading import Thread
from Queue import Queue
g_access_token = ''

def main():
    # Enforce python version
    if sys.version_info[0] != 2 or sys.version_info[1] < 7:
        error("The script only works in python 2.x where x >= 7")
    args = read_args()

    global g_access_token
    g_access_token = args.access_token
    test_access_token(args.access_token)
    
    list=[]
    with open(args.input) as f:
        content = f.readlines()
    
        for i in range(len(content)):
            splitValue = content[i].split('\t')
            resp = json.loads(curl('https://api.oculus.com/v1/personal-info/%s' % splitValue[1], 'GET', '', { 'Content-Type': 'application/json', 'Authorization': 'Bearer %s' % g_access_token }))
            print content[i].replace('\n', '\t') + resp["value"]["info"]
            list.append(content[i].replace('\n', '\t') + resp["value"]["info"])
    with open(args.output, 'w') as outfile:
        json.dump(list, outfile)

def read_args():
    parser = argparse.ArgumentParser(description='Id Migration Script')
    parser.add_argument('-i', action="store", metavar='input_file', dest="input", help='the input file', required=True)
    parser.add_argument('-o', action="store", metavar='output_file', dest="output", help='the output file, default is results.json', default='results.json')
    parser.add_argument('-access_token', action="store", metavar='access_token', dest="access_token", help="access_token used to get email", required=False)
    return parser.parse_args()

def test_access_token(access_token):
    curl('https://api.oculus.com/v1/oauth2/access-token/%s' % access_token)

def curl(url, method = 'GET', body = None, headers = None, raiseOn5xxError = True, raiseOn4xxError = True):
    if headers is None: headers = {}

    resp, status, reason = curlRaw(url, method, body, headers)
    if status >= 500:
        if raiseOn5xxError:
            raise Exception('%s %s in %s %s\n%s' % (status, reason, method, url, resp))
    elif status >= 400 and raiseOn4xxError:
        raise Exception('%s %s in %s %s\n%s' % (status, reason, method, url, resp))
    return resp

def curlRaw(url, method = 'GET', body = None, headers = None):
    if headers is None: headers = {}

    conn = None
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

        verbose(method + " " + url)
        if body: verbose(body)

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

        # uncomment to turn on trace
        # conn.set_debuglevel(1)      # turn on trace
        conn.request(method, path, body, headers)
        resp = conn.getresponse()
        body = resp.read()
        verbose("%d %s" % (resp.status, resp.reason))
        if body:
            verbose(body)
        return (body, resp.status, resp.reason)
    finally:
        if conn:
            conn.close()
    
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

main()
