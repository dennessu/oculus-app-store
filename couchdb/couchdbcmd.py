#!/usr/bin/python

import sys
import os
import getpass
import subprocess
import re
import httplib
import json
import string
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

    command, env, dbPrefix = readParams()
    executeDbCommand(command, env, dbPrefix)

def readParams():
    # Validate command
    def printValidCommands():
        info("Available Commands: ")
        info(
            "   listdbs                 Show all databases\n" +
            "   dumpdbs                 Show all databases and the views\n" +
            "   createdbs               Create all databases\n" +
            "   dropdbs                 Drop all databases\n")
        sys.stdout.flush()
    
    def printUsage():
        error("Usage: python ./couchdbcmd.py <command> [<env>] [--yes] [--verbose] [--prefix={prefix}] [--key={key}]\n")
    
    # Read input params
    sys.argv.pop(0)     # skip argv[0]
    
    # Read flags
    global cipherKey

    confirmed = False
    dbPrefix = None
    cipherKey = None

    cleanArgv = []
    while len(sys.argv) > 0:
        arg = sys.argv.pop(0).strip()
        if arg == "--yes":
            confirmed = True
        elif arg == "--verbose":
            setVerbose(True)
        elif arg.startswith("--prefix="):
            dbPrefix = arg[len("--prefix="):]
        elif arg.startswith("--key="):
            cipherKey = arg[len("--key="):]
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
    env = readOptionalArg("onebox")

    # Validate params
    command = command.lower()
    if command not in set(["listdbs", "dumpdbs", "createdbs", "dropdbs", "diffdbs", "updatedbs"]):
        printValidCommands()
        error("Invalid command: " + command)
    
    if command in set(["createdbs", "dropdbs"]) and not confirmed:
        # Ask for confirmation
        response = readInput("WARNING! The command will make changes to couchdb in env %s. Are you absolutely sure? ('yes'/'no'): " % env)
        if response.lower() != "yes":
            error("Aborting...")

    if command == "dropdbs" and env in set(["onebox.owpint", "ppe", "prod"]):
        # Don't allow dropdbs in these environments
        error("The command dropdbs is now allowed in env " + env)

    verbose("Read parameters: (command, env, dbPrefix) = (%s, %s, %s)" % (command, env, dbPrefix))

    return (command, env, dbPrefix)

def readConfig(env):
    verbose("Reading configurations for " + env)
    envConf = readJsonFile("conf/%s.json" % env)
    finalEnvConf = {}

    encryptedSuffix = ".encrypted"
    for k, v in envConf.items():
        if isinstance(v, basestring):
            v = [ v ]

        if k.endswith(encryptedSuffix):
            key = k[0:len(k) - len(encryptedSuffix)]
            isEncrypted = True
        else:
            key = k
            isEncrypted = False

        finalEnvConf[key] = []
        for item in v:
            if isEncrypted:
                finalEnvConf[key].append(decrypt(item))
            else:
                finalEnvConf[key].append(item)

    return finalEnvConf

def readDbs():
    filename = 'changelogs/couchdb.json'
    verbose("Reading couchdb master configuration: " + filename)
    return readJsonFile(filename)

def executeDbCommand(command, env, dbPrefix):
    envConf = readConfig(env)

    if command == "listdbs":
        listdbs(envConf, dbPrefix)
    elif command == "dumpdbs":
        dumpdbs(envConf, dbPrefix)
    elif command == "createdbs":
        createdbs(envConf, dbPrefix)
    elif command == "dropdbs":
        dropdbs(envConf, dbPrefix)

def listdbs(envConf, dbPrefix):
    if not dbPrefix:
        dbPattern = '.*'
    else:
        dbPattern = dbPrefix

    result = []
    for key, list in envConf.items():
        for url in list:
            result.extend([db for db in curlJson(url + "/_all_dbs") if re.match(dbPattern, db) and not db.startswith("_")])

    print string.join(sorted(result), "\r\n")

def dumpdbs(envConf, dbPrefix):
    if not dbPrefix: dbPrefix = ''
    dbs = readDbs()

    result = {}
    for key, list in envConf.items():
        for url in list:
            if not key in dbs:
                continue
            result[key] = {}
            for db in dbs[key]:
                fullDbName = dbPrefix + db
                data = curlJson(url + "/" + fullDbName + "/_design/views", raiseOnError = False)
                result[key][db] = {}

                if "views" in data and any(data["views"]):
                    result[key][db]["views"] = data["views"]
                if "indexes" in data and any(data["indexes"]):
                    result[key][db]["indexes"] = data["indexes"]

    print json.dumps(result, indent = 2, sort_keys = True)

def createdbs(envConf, dbPrefix):
    if not dbPrefix: dbPrefix = ''
    dbs = readDbs()

    for key, list in envConf.items():
        index = -1
        for url in list:
            index += 1
            if not key in dbs:
                continue

            existingDbs = [db for db in curlJson(url + "/_all_dbs") if not db.startswith("_")]
            for db, dbDef in dbs[key].items():
                fullDbName = dbPrefix + db
                if not fullDbName in existingDbs:
                    info("Creating DB '%s' in '%s[%d]'" % (fullDbName, key, index))
                    curl(url + "/" + fullDbName, "PUT")

                createviews(dbDef, url, fullDbName)

def createviews(dbDef, url, fullDbName):
    viewsResp, status, reason = curlRaw(url + "/" + fullDbName + "/_design/views")
    viewsRequest = {}
    if status == 200:
        viewsRequest = json.loads(viewsResp)
    else:
        viewsRequest["language"] = 'javascript'

    needPut = False
    if "views" in dbDef:
        viewsRequest["views"] = dbDef["views"]
        needPut = True
    if "indexes" in dbDef:
        viewsRequest["indexes"] = dbDef["indexes"]
        needPut = True

    if needPut:
        info("Creating views for database: " + fullDbName)
        viewsRequestStr = json.dumps(viewsRequest, indent = 2)
        curl(url + "/" + fullDbName + "/_design/views", "PUT", viewsRequestStr)

def dropdbs(envConf, dbPrefix):
    if not dbPrefix: dbPrefix = ''
    dbs = readDbs()

    for key, list in envConf.items():
        index = -1
        for url in list:
            index += 1
            if not key in dbs:
                continue

            existingDbs = [db for db in curlJson(url + "/_all_dbs") if not db.startswith("_")]
            for db in dbs[key]:
                fullDbName = dbPrefix + db

                if fullDbName in existingDbs:
                    info("Dropping database '%s' from '%s[%d]'" % (fullDbName, key, index));
                    curl(url + "/" + fullDbName, "DELETE")

def curlJson(url, method = 'GET', body = None, headers = None, raiseOnError = True):
    if headers is None:
        headers = {
            'Content-Type': 'application/json'
        }

    response = curl(url, method, body, headers, raiseOnError)
    obj = json.loads(response)
    verbose(json.dumps(obj, indent = 2))
    return obj

def curl(url, method = 'GET', body = None, headers = None, raiseOnError = True):
    if headers is None: headers = {}

    resp, status, reason = curlRaw(url, method, body, headers)
    if status >= 400 and raiseOnError:
        raise Exception('%s %s in %s %s\n%s' % (status, reason, method, url, resp))
    return resp

connCache = {}
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
        returnCode = subprocess.call(command, shell = True)
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

    with open (filename, "r") as configFile:
        data = configFile.read() 
        return json.loads(data)

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
