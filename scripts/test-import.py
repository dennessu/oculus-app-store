#!/usr/bin/python

import sys
import os
import re
import httplib
import string
import time
from threading import Thread
from Queue import Queue

def main():
    # Enforce python version
    if sys.version_info[0] != 2 or sys.version_info[1] < 7:
        error("The script only works in python 2.x where x >= 7")

    # Enforce to current directory
    currentDir = os.path.dirname(sys.argv[0])
    try:
        os.chdir(currentDir)
    except OSError as e:
        error(e + "\nUnable to change current directory to : " + currentDir + ". Aborting...")

    num_worker_threads = 4

    start_time = time.time()
    q = Queue()
    for i in range(num_worker_threads):
         t = Thread(target=worker, args=[q])
         t.daemon = True
         t.start()

    count = 0
    lines = []
    for line in sys.stdin:
        lines.append(line)
        count += 1
        if count == 20:
            q.put(lines)
            count = 0
            lines = []
    if len(lines) > 0:
        q.put(lines)

    q.join()       # block until all tasks are done

    elapsed_time = time.time() - start_time
    print "elapsed time: " + str(elapsed_time)

def worker(q):
    while True:
        lines = '['
        inputLines = q.get()
        for line in inputLines:
            lines += line + ','
        lines = lines[0 : len(lines) - 1] + ']'
        startTime = time.time()
        try:
            print 'Processing %d lines.' % len(inputLines)
            curl('http://127.0.0.1:8080/v1/imports/bulk', 'POST', lines, { 'Content-Type': 'application/json' })
        except Exception, e:
            print e
        print 'Processing done, elapsed time: %s' % (time.time() - startTime)
        q.task_done()

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

