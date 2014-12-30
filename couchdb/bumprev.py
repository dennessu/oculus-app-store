#!/usr/bin/python

import sys
import os
import getpass
import subprocess
import re
import httplib
import json
import string
import random
import AESCipher as cipher


def main():
    # Enforce python version
    if sys.version_info[0] != 2 or sys.version_info[1] < 7:
        error("The script only works in python 2.x where x >= 7")

    # Enforce to script directory
    currentDir = os.path.dirname(os.path.realpath(__file__))
    try:
        os.chdir(currentDir)
    except OSError as e:
        error(e + "\nUnable to change current directory to : " + currentDir + ". Aborting...")

    db = readParams()
    bumprev(db)


def readParams():
    # Validate command
    def printUsage():
        error("Usage: python ./bumprev.py {db}\n")

    # Read input params
    sys.argv.pop(0)  # skip argv[0]

    if len(sys.argv) < 1:
        printUsage()

    # Read flags
    db = sys.argv.pop(0)

    print "bumping rev for docs in %s" % db

    return db


def bumprev(db):
    startKey = ""
    batchSize = 1000
    rowsUpdated = 0
    while True:
        allDocs = curlJson(db + '/_all_docs?limit=%d&startkey="%s"&include_docs=true' % (batchSize + 1, startKey), "GET")
        docs = []
        firstRow = (startKey != "")
        for row in allDocs['rows']:
            if firstRow:
                # skip first row
                firstRow = False
                continue
            docs.append(row['doc'])
            rowsUpdated += 1
            startKey = row['doc']['_id']
        bulkStr = json.dumps({'docs': docs}, indent=2)
        if len(docs) > 0:
            curlJson(db + '/_bulk_docs', "POST", bulkStr)
        else:
            break
    print "%d docs updated" % rowsUpdated

def curlJson(url, method='GET', body=None, headers=None, raiseOnError=True):
    if headers is None:
        headers = {
            'Content-Type': 'application/json'
        }

    response = curl(url, method, body, headers, raiseOnError)
    obj = json.loads(response)
    verbose(json.dumps(obj, indent=2))
    return obj


def curl(url, method='GET', body=None, headers=None, raiseOnError=True):
    if headers is None: headers = {}

    resp, status, reason = curlRaw(url, method, body, headers)
    if status >= 400 and raiseOnError:
        raise Exception('%s %s in %s %s\n%s' % (status, reason, method, url, resp))
    return resp


connCache = {}


def curlRaw(url, method='GET', body=None, headers=None):
    if headers is None: headers = {}

    conn = None
    try:
        urlRegex = r'^(?P<protocol>http[s]?://)?((?P<userpass>(?P<user>[^/@:]*):([^/@:]*))@)?(?P<host>[^/:]+)(:(?P<port>\d+))?(?P<path>(/|\?).*)$'
        m = re.match(urlRegex, url)
        if m is None:
            raise Exception('Invalid url: ' + url)

        protocol = m.group('protocol')
        host = m.group('host')
        port = m.group('port')
        path = m.group('path')

        userpass = m.group('userpass')
        user = m.group('user')

        verbose(method + " " + url)
        if body: verbose(body)

        if port:
            port = int(port)

        global connCache
        cacheKey = protocol + host + ":" + str(port)
        if connCache.has_key(cacheKey):
            conn = connCache[cacheKey]
        else:
            if protocol == "https://":
                conn = httplib.HTTPSConnection(host, port)
            else:
                conn = httplib.HTTPConnection(host, port)
            connCache[cacheKey] = conn

        if userpass:
            import base64

            base64String = base64.encodestring(userpass).strip()
            authheader = "Basic %s" % base64String
            headers['Authorization'] = authheader

            # add the user header to pick user for cloudant
            headers['X-Cloudant-User'] = user

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


def executeCommand(command):
    try:
        returnCode = subprocess.call(command, shell=True)
        if returnCode != 0:
            error("Return code %d in command: %s" % (returnCode, command))
    except OSError as e:
        error("Error executing command: " + command)


def decrypt(value):
    global cipherKey
    if cipherKey is None:
        cipherKey = readPassword("Input the password cipher key: ")
    return cipher.decryptData(cipherKey.strip(), value.strip())


def safeStrip(value):
    if value is not None:
        return value.strip()
    else:
        return None


def isNoneOrEmpty(value):
    return value is None or len(value) == 0


def splitRange(param):
    result = set()
    for part in param.split(','):
        x = part.split('..')
        result.update(range(int(x[0]), int(x[-1]) + 1))
    return sorted(result)


def readInput(message):
    sys.stdout.write(message)
    sys.stdout.flush()
    return sys.stdin.readline().strip()


def readPassword(message):
    return getpass.getpass(message)


def readJsonFile(filename):
    if not os.path.isfile(filename):
        error("File %s not found." % filename)

    with open(filename, "r") as configFile:
        data = configFile.read()
        return json.loads(data)


gIsVerbose = False
gIgnoreDiff = False

def setVerbose(isVerbose):
    global gIsVerbose
    gIsVerbose = isVerbose

def setIgnoreDiff(ignoreDiff):
    global  gIgnoreDiff
    gIgnoreDiff = ignoreDiff

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
