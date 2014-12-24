#!/usr/bin/python

import sys
import os
import re
import httplib
import json
import time
import argparse
from threading import Lock, Thread
from Queue import Queue, Empty
lock = Lock()
global_count = 0
global_limit = 20
g_access_token = ''
    
def main():
    # Enforce python version
    if sys.version_info[0] != 2 or sys.version_info[1] < 7:
        error("The script only works in python 2.x where x >= 7")
    args = read_args()

    global g_access_token
    g_access_token = args.access_token
    test_access_token(args.access_token)

    num_worker_threads = 10

    start_time = time.time()
    output = open(args.output, "w")
    output.write("{" + os.linesep)

    write_q = Queue()
    write_thread = Thread(target=write_to_file, args=[write_q, output])
    write_thread.start()
    
    threads = []
    for i in range(num_worker_threads):
        t = Thread(target=updateHtmlThread, args=[write_q])
        threads.append(t)
        t.start()
        
    for t in threads:
        t.join()
    
    write_thread.join()
         
    elapsed_time = time.time() - start_time
    print "elapsed time: " + str(elapsed_time)
    
    output.write("}")
    output.close()

def read_args():
    parser = argparse.ArgumentParser(description='user html code update Script')
    parser.add_argument('-o', action="store", metavar='output_file', dest="output", help='the output file, default is results.json', default='results.json')
    parser.add_argument('-access_token', action="store", metavar='access_token', dest="access_token", help='the accessToken to do the migration', required=True)
    return parser.parse_args()

def updateHtmlThread(write_q):
    while True:
        startTime = time.time()
        try:
            lock.acquire()
            global global_count
            global global_limit
            cursor = global_count
            global_count = global_count + global_limit
            lock.release()
            global g_access_token
            resp = curl('http://127.0.0.1:8080/v1/users?cursor=%s' % str(cursor) + '&count=' + str(global_limit), 'GET', '', { 'Content-Type': 'application/json', 'Authorization': 'Bearer %s' % g_access_token })
            user_search = json.loads(resp)
            users = user_search['results']
            if len(users) == 0:
                break
            for user in users:
                resp = curl('http://127.0.0.1:8080/v1/imports/update-html/%s' % str(user['self']['id']), 'POST', '', { 'Content-Type': 'application/json', 'Authorization': 'Bearer %s' % g_access_token }, raiseOn4xxError = False)
                result = json.loads(resp)
                write_q.put(result)
            print 'Processing %d started users.' % cursor
        except Exception, e:
            print e
            result = {(str(user['id']), {"error":str(e)}) for user in users}
            write_q.put(result)
        print 'Processing done, elapsed time: %s' % (time.time() - startTime)

def write_to_file(write_q, output):
    count = 0
    while True:
        try:
            msg = write_q.get(1, 1)
            output.write(json.dumps(msg))
            output.write(os.linesep)
            count = 0
        except Empty:
            print 'exception to empty %s' % count
            count = count + 1
            if count > 10:
                break
            pass
        
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

