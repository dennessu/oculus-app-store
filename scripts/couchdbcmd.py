#!/usr/bin/python

import sys
import os
import subprocess
import re
import httplib
import json
import string


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

    command, host, db = readParams()
    executeDbCommand(command, host, db)

def readParams():
    # Validate command
    def printValidCommands():
        info("Available Commands: ")
        info(
            "   listdbs                 Show all databases\n" +
            "   dropall                 Drop all databases\n" +
            "   drop                    Drop the specified database\n")
        sys.stdout.flush()
    
    def printUsage():
        error("Usage: python ./couchdbcmd.py <command> [<host>] [<db>] [--yes] [--verbose]\n")
    
    # Read input params
    sys.argv.pop(0)     # skip argv[0]
    
    # Read flags
    confirmed = False

    cleanArgv = []
    while len(sys.argv) > 0:
        arg = sys.argv.pop(0).strip()
        if arg == "--yes":
            confirmed = True
        elif arg == "--verbose":
            setVerbose(True)
        else:
            cleanArgv.append(arg)
    sys.argv = cleanArgv
    
    if len(sys.argv) < 1:
        printValidCommands()
        printUsage()

    # Required params
    command = sys.argv.pop(0).strip()
    def readOptionalArg(default = None):
        if len(sys.argv) > 0:
            return sys.argv.pop(0).strip()
        return default
    host = readOptionalArg("localhost:5984")
    dbPattern = readOptionalArg()

    # Validate params
    command = command.lower()
    if command not in set(["listdbs", "dropall", "drop"]):
        printValidCommands()
        error("Invalid command: " + command)
    
    if command in set(["dropall", "drop"]) and not confirmed:
        # Ask for confirmation
        response = readInput("WARNING! The command will make changes to couchdb %s. Are you absolutely sure? ('yes'/'no'): " % host)
        if response.lower() != "yes":
            error("Aborting...")

    if command in set(["drop"]):
        if isNoneOrEmpty(dbPattern):
            error("Argument db is required for " + command)
    
    return (command, host, dbPattern)

def executeDbCommand(command, host, dbPattern):
    if command == "listdbs":
        print string.join(listdbs(host, dbPattern), "\r\n")
    elif command == "dropall":
        for db in listdbs(host, dbPattern):
            dropdb(host, db)
    elif command == "drop":
        # in this case dbPattern is the db name
        drop(host, dbPattern)

def listdbs(host, dbPattern):
    if not dbPattern:
        dbPattern = '.*'
    return [db for db in curlJson(host + "/_all_dbs") if re.match(dbPattern, db) and not db.startswith("_")]

def dropdb(host, dbName):
    info("Dropping database %s in %s..." % (dbName, host));
    curl(host + "/" + dbName, "DELETE")
    info("Dropped database %s in %s." % (dbName, host));

def curlJson(url, method = 'GET', body = None, headers = {}):
    response = curl(url, method, body, headers)
    obj = json.loads(response)
    verbose(json.dumps(obj, indent = 2))
    return obj

def curl(url, method = 'GET', body = None, headers = {}, raiseOnError = True):
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
        if port:
            port = int(port)
        if protocol == "https://":
            conn = httplib.HTTPSConnection(host, port)
        else:
            conn = httplib.HTTPConnection(host, port)
 
        if userpass:
            import base64
            base64String = base64.encodestring(userpass)
            authheader = "Basic %s" % base64String
            headers['Authorization'] = authheader

        # uncomment to turn on trace
        # conn.set_debuglevel(1)      # turn on trace
        conn.request(method, path, body, headers)
        resp = conn.getresponse()
        verbose("%d %s" % (resp.status, resp.reason))
        if resp.status >= 400 and raiseOnError:
            raise Exception('%s %s in %s %s' % (resp.status, resp.reason, method, url))
        return resp.read()
    finally:
        if conn:
            conn.close()


def executeCommand(command):
    try:
        returnCode = subprocess.call(command, shell = True)
        if returnCode != 0:
            error("Return code %d in command: %s" % (returnCode, command))
    except OSError as e:
        error("Error executing command: " + command)

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
