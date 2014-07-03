#!/usr/bin/python

import sys
import os
import re
import httplib
import string
import json
import time
from threading import Thread
from Queue import Queue

def main():
    # Enforce python version
    if sys.version_info[0] != 2 or sys.version_info[1] < 7:
        error("The script only works in python 2.x where x >= 7")
    
    file_name = sys.argv[1]
    users_file = file(file_name)
    users = json.load(users_file)

    num_worker_threads = 30

    results = {}
    start_time = time.time()
    q = Queue()
    write_q = Queue()
    output = open("results.json", "w")
    output.write("{" + os.linesep)

    write_thread = Thread(target=write_worker, args=[users, write_q, results, output])
    write_thread.start()

    for i in range(num_worker_threads):
         t = Thread(target=worker, args=[q, write_q])
         t.daemon = True
         t.start()
    
    chunks = [users[i:i+20] for i in xrange(0, len(users), 20)]

    for input_users in chunks:
        q.put(input_users)

    q.join()       # block until all tasks are done

    elapsed_time = time.time() - start_time
    print "elapsed time: " + str(elapsed_time)

    write_thread.join()
    output.write("}")
    output.close()

def worker(q, write_q):
    while True:
        users = q.get()
        startTime = time.time()
        try:
            print 'Processing %d users.' % len(users)
            resp = curl('http://127.0.0.1:8080/v1/imports/bulk', 'POST', json.dumps(users), { 'Content-Type': 'application/json' })
            result = json.loads(resp)
            write_q.put(result)
        except Exception, e:
            print e
        print 'Processing done, elapsed time: %s' % (time.time() - startTime)
        q.task_done()

def write_worker(users, write_q, results, output):
    start = 0
    end = len(users)
    while (start < end):
        if not write_q.empty():
            results.update(write_q.get())
        user_id = users[start]['id']
        user = results.pop(str(user_id), None)
        if user:
            output.write('"' + str(user_id) + '"')
            output.write(":")
            if not user["error"]: del user["error"]
            output.write(json.dumps(user))
            if (start == end-1):
                output.write(os.linesep)
            else:
                output.write("," + os.linesep)
            start += 1

def curl(url, method = 'GET', body = None, headers = None, raiseOnError = True):
    if headers is None: headers = {}

    resp, status, reason = curlRaw(url, method, body, headers)
    if status >= 400 and raiseOnError:
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

