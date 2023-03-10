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

def main():
    # Enforce python version
    if sys.version_info[0] != 2 or sys.version_info[1] < 7:
        error("The script only works in python 2.x where x >= 7")
    args = read_args()

    global g_access_token
    g_access_token = args.access_token
    test_access_token(args.access_token)

    users_file = file(args.input)
    users = json.load(users_file)
    size = len(users)
    if users[size-1] == {}:
        del users[size-1]

    num_worker_threads = 30

    results = {}
    start_time = time.time()
    q = Queue()
    write_q = Queue()
    output = open(args.output, "w")
    output.write("{" + os.linesep)

    write_thread = Thread(target=write_worker, args=[users, write_q, results, output])
    write_thread.start()

    for i in range(num_worker_threads):
         t = Thread(target=worker, args=[q, write_q])
         t.daemon = True
         t.start()
    
    count = 0
    input_users = []
    for user in users:
        input_users.append(user)
        count += 1
        if count == 20:
            q.put(input_users)
            count = 0
            input_users = []
    if len(input_users) > 0:
        q.put(input_users)

    q.join()       # block until all tasks are done

    elapsed_time = time.time() - start_time
    print "elapsed time: " + str(elapsed_time)

    write_thread.join()
    output.write("}")
    output.close()

def read_args():
    parser = argparse.ArgumentParser(description='Id Migration Script')
    parser.add_argument('-i', action="store", metavar='input_file', dest="input", help='the input file', required=True)
    parser.add_argument('-o', action="store", metavar='output_file', dest="output", help='the output file, default is results.json', default='results.json')
    parser.add_argument('-access_token', action="store", metavar='access_token', dest="access_token", help='the accessToken to do the migration', required=True)
    return parser.parse_args()

def worker(q, write_q):
    while True:
        users = q.get()
        startTime = time.time()
        print 'Processing %d users.' % len(users)
        for user in users:
            result = {}
            try:
                global g_access_token
                resp = curl('http://127.0.0.1:8080/v1/imports/username-email-block', 'POST', json.dumps(user), { 'Content-Type': 'application/json', 'Authorization': 'Bearer %s' % g_access_token }, raiseOn4xxError = True)
                result = {str(user['username']): "success"}
            except Exception, e:
                print e
                result = {str(user['username']): {"error":str(e)}}
            write_q.put(result)
        print 'Processing done, elapsed time: %s' % (time.time() - startTime)
        q.task_done()

def write_worker(users, write_q, results, output):
    start = 0
    end = len(users)
    while (start < end):
        if not write_q.empty():
            results.update(write_q.get())
        user_name = users[start]['username']
        user = results.pop(str(user_name), None)
        if user:
            output.write('"' + str(user_name) + '"')
            output.write(":")
            output.write(json.dumps(user))
            if (start == end-1):
                output.write(os.linesep)
            else:
                output.write("," + os.linesep)
            start += 1
    
def test_access_token(access_token):
    curl('http://127.0.0.1:8080/v1/oauth2/access-token/%s' % access_token)

    

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

