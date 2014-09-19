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

    command, env, dbPrefix = readParams()
    executeDbCommand(command, env, dbPrefix)


def readParams():
    # Validate command
    def printValidCommands():
        info("Available Commands: ")
        info(
            "   genkey                  Generate an API key of cloudant\n" +
            "   listdbs                 Show all databases\n" +
            "   diff                    Show diff info\n" +
            "   dumpdbs                 Show all databases and the views\n" +
            "   createdbs               Create all databases\n" +
            "   dropdbs                 Drop all databases\n" +
            "   purgedbs                Purge all databases\n")
        sys.stdout.flush()

    def printUsage():
        error("Usage: python ./couchdbcmd.py <command> [<env>] [--yes] [--verbose] [--prefix={prefix}] [--key={key}]\n")

    # Read input params
    sys.argv.pop(0)  # skip argv[0]

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

    def readOptionalArg(default=None):
        if len(sys.argv) > 0:
            return sys.argv.pop(0).strip()
        return default

    env = readOptionalArg("onebox")

    # Validate params
    command = command.lower()
    if command not in set(["genkey", "listdbs", "diff", "dumpdbs", "createdbs", "dropdbs", "purgedbs", "diffdbs"]):
        printValidCommands()
        error("Invalid command: " + command)

    if command in set(["createdbs", "dropdbs"]) and not confirmed:
        # Ask for confirmation
        response = readInput(
            "WARNING! The command will make changes to couchdb in env %s. Are you absolutely sure? ('yes'/'no'): " % env)
        if response.lower() != "yes":
            error("Aborting...")

    if command in set(["dropdbs", "purgedbs"]) and env not in set(["onebox", "lt", "onebox.int"]):
        answer = ''.join(random.choice(string.ascii_letters) for _ in range(10))
        input = readInput(
            "The environment is not test environment. Are you sure you want to delete data? Repeat '%s' to confirm: " % answer)
        if answer != input:
            # Don't allow dropdbs in these environments
            error("The command " + command + " is now allowed in env " + env)

    verbose("Read parameters: (command, env, dbPrefix) = (%s, %s, %s)" % (command, env, dbPrefix))
    return (command, env, dbPrefix)


dbsConfigKey = 'dbs'
apiKeysConfigKey = 'apikeys'
authDbsConfigKey = 'authdbs'


def readConfig(env):
    global dbsConfigKey, apiKeysConfigKey, authDbsConfigKey
    verbose("Reading configurations for " + env)
    envConf = readJsonFile("conf/%s.json" % env)
    finalEnvConf = {}
    finalEnvConf[dbsConfigKey] = {}
    finalEnvConf[apiKeysConfigKey] = envConf[apiKeysConfigKey] if apiKeysConfigKey in envConf else None
    finalEnvConf[authDbsConfigKey] = envConf[authDbsConfigKey] if authDbsConfigKey in envConf else None

    encryptedSuffix = ".encrypted"
    for k, v in envConf[dbsConfigKey].items():
        if isinstance(v, basestring):
            v = [v]

        if k.endswith(encryptedSuffix):
            key = k[0:len(k) - len(encryptedSuffix)]
            isEncrypted = True
        else:
            key = k
            isEncrypted = False

        finalEnvConf[dbsConfigKey][key] = []
        for item in v:
            if isEncrypted:
                finalEnvConf[dbsConfigKey][key].append(decrypt(item))
            else:
                finalEnvConf[dbsConfigKey][key].append(item)

    return finalEnvConf


def readDbs():
    filename = 'changelogs/couchdb.json'
    verbose("Reading couchdb master configuration: " + filename)
    return readJsonFile(filename)


def executeDbCommand(command, env, dbPrefix):
    envConf = readConfig(env)

    if command == "genkey":
        genkey(envConf)
    elif command == "listdbs":
        listdbs(envConf, dbPrefix)
    elif command == "diff":
        diffdbs(envConf, dbPrefix)
    elif command == "dumpdbs":
        dumpdbs(envConf, dbPrefix)
    elif command == "createdbs":
        createdbs(envConf, dbPrefix)
    elif command == "dropdbs":
        dropdbs(envConf, dbPrefix)
    elif command == "purgedbs":
        purgedbs(envConf, dbPrefix)


def genkey(envConf):
    global dbsConfigKey
    url = envConf[dbsConfigKey]["cloudant"][0]
    credentials = re.compile("https://(.*)@.*\.cloudant\.com").match(url).group(1)
    result = curlJson("https://%s@cloudant.com/api/generate_api_key" % credentials, method='POST')
    print json.dumps(result, indent=2)


def listdbs(envConf, dbPrefix):
    global dbsConfigKey
    if not dbPrefix:
        dbPattern = '.*'
    else:
        dbPattern = dbPrefix

    result = []
    for key, list in envConf[dbsConfigKey].items():
        for url in list:
            result.extend(
                [db for db in curlJson(url + "/_all_dbs") if re.match(dbPattern, db) and not db.startswith("_")])

    print string.join(sorted(result), "\r\n")


def diffdbs(envConf, dbPrefix):
    result = {}
    result["newDbs"] = getNewDbs(envConf, dbPrefix)
    result["viewDiff"] = diffViews(envConf, dbPrefix)
    print json.dumps(result, indent=2)


def getNewDbs(envConf, dbPrefix):
    global dbsConfigKey
    if not dbPrefix:
        dbPattern = '.*'
    else:
        dbPattern = dbPrefix

    dbs = readDbs()

    result = []
    for key, list in envConf[dbsConfigKey].items():
        url = list[0]
        existingDbs = [db for db in curlJson(url + "/_all_dbs") if re.match(dbPattern, db) and not db.startswith("_")]
        currentDbs = [dbPrefix + dbName for dbName in dbs[key].keys()]
        result.extend([db for db in currentDbs if db not in existingDbs])
    return result


def diffViews(envConf, dbPrefix):
    global dbsConfigKey
    if not dbPrefix:
        dbPrefix = ""

    dbs = readDbs()
    result = {}
    for key, list in envConf[dbsConfigKey].items():
        url = list[0]
        for db, views in dbs[key].items():
            fullDbName = dbPrefix + db
            viewDiffResult = diffViewPerDb(url, fullDbName, views)
            if viewDiffResult:
                result[fullDbName] = viewDiffResult

    return result


def diffViewPerDb(url, fullDbName, views):
    existingResponse = curlJson(url + "/" + fullDbName + "/_design/views", raiseOnError=False)
    existing = {type: existingResponse[type]
                if "error" not in existingResponse and type in existingResponse else {}
                for type in ["views", "indexes"]}

    result = {}
    viewDiff = diffView("views", existing, views)
    if viewDiff: result["views"] = viewDiff
    indexDiff = diffView("indexes", existing, views)
    if indexDiff: result["indexes"] = indexDiff

    return result


def diffView(type, existing, new):
    actions = ["create", "update", "delete"]
    result = {action: {} for action in actions}
    if type in new:
        for key, value in new[type].items():
            if type in existing and key in existing[type]:
                if value != existing[type][key]:
                    result["update"][key] = {"old": existing[type][key], "new": value}
            else:
                result["create"][key] = value
    if type in existing:
        result["delete"] = {key: existing[type][key] for key in existing[type] if key not in new[type]}

    [result.pop(action) for action in actions if not result[action]]
    return result


def dumpdbs(envConf, dbPrefix):
    global dbsConfigKey
    if not dbPrefix: dbPrefix = ''
    dbs = readDbs()

    result = {}
    for key, list in envConf[dbsConfigKey].items():
        for url in list:
            if not key in dbs:
                continue
            result[key] = {}
            for db in dbs[key]:
                fullDbName = dbPrefix + db
                data = curlJson(url + "/" + fullDbName + "/_design/views", raiseOnError=False)
                result[key][db] = {}

                if "views" in data and any(data["views"]):
                    result[key][db]["views"] = data["views"]
                if "indexes" in data and any(data["indexes"]):
                    result[key][db]["indexes"] = data["indexes"]

    print json.dumps(result, indent=2, sort_keys=True)


allRoles = ["_reader", "_writer", "_admin", "_replicator"]


def createdbs(envConf, dbPrefix):
    global allRoles, dbsConfigKey, apiKeysConfigKey, authDbsConfigKey
    if not dbPrefix: dbPrefix = ''
    dbs = readDbs()

    for key, list in envConf[dbsConfigKey].items():
        index = -1
        for url in list:
            index += 1
            if not key in dbs:
                continue

            authdbs = envConf[authDbsConfigKey]
            username = parseUsername(url)

            if isCloudantUrl(url) and authdbs and username in authdbs:
                for authdb in authdbs[username]:
                    authUrl = url.replace(username + '.', authdb + '.')
                    creatdb(authUrl, envConf[apiKeysConfigKey], dbs[key].items(), key, index, authdb, dbPrefix)
            else:
                creatdb(url, envConf[apiKeysConfigKey], dbs[key].items(), key, index, username, dbPrefix)

            for db, dbDef in dbs[key].items():
                fullDbName = dbPrefix + db
                createviews(dbDef, url, fullDbName)


def creatdb(url, apikeyConf, alldbs, key, index, username, dbPrefix):
    existingDbs = [db for db in curlJson(url + "/_all_dbs") if not db.startswith("_")]
    for db, dbDef in alldbs:
        fullDbName = dbPrefix + db
        if not fullDbName in existingDbs:
            info("Creating DB '%s' of '%s[%d]' in '%s'" % (fullDbName, key, index, username))
            curl(url + "/" + fullDbName, "PUT")
        else:
            info("DB '%s' of '%s[%d]' in '%s' exists" % (fullDbName, key, index, username))
        grantPermissions(apikeyConf, url, fullDbName, username)


def grantPermissions(apikeyConf, url, fullDbName, username):
    if not apikeyConf:
        return
    if isCloudantUrl(url):
        for conf in apikeyConf:
            apikey = conf['key']
            permissionUrl = url + "/%s/_security" % fullDbName
            permissions = curlJson(permissionUrl)
            if not permissions:
                permissions['cloudant'] = {}
                permissions['cloudant'][username] = allRoles
                permissions['_id'] = "_security"
                permissions['cloudant'][apikey] = [role for role in conf['roles']]
            info("Granting permissions for APIKEY '%s' of DB '%s' in '%s'" % (apikey, fullDbName, username))
            grantPermission(permissionUrl, permissions)


def grantPermission(permissionUrl, permissions):
    result = curlJson(permissionUrl, "PUT", json.dumps(permissions))
    if "ok" not in result or result["ok"] is not True:
        raise Exception(json.dumps(result, indent=2))


def isCloudantUrl(url):
    return url.find("cloudant.com") != -1


userNamePattern = re.compile("https://(.*):.*@.*\.cloudant\.com")


def parseUsername(url):
    global userNamePattern
    match = userNamePattern.match(url)
    if not match:
        return None
    return match.group(1)


def createviews(dbDef, url, fullDbName):
    viewsResp, status, reason = curlRaw(url + "/" + fullDbName + "/_design/views")
    viewsRequest = {}
    if status == 200:
        viewsRequest = json.loads(viewsResp)
    else:
        viewsRequest["language"] = 'javascript'

    needPut = False
    if "views" in dbDef:
        viewDiff = diffView("views", viewsRequest, dbDef)
        if "update" in viewDiff or "delete" in viewDiff:
            raise Exception("update or delete view:\n" + json.dumps(viewDiff, indent=2))
        viewsRequest["views"] = dbDef["views"]
        needPut = True
    if "indexes" in dbDef:
        indexDiff = diffView("indexes", viewsRequest, dbDef)
        if "delete" in indexDiff:
            raise Exception("delete index:\n" + json.dumps(indexDiff, indent=2))
        if "update" in indexDiff:
            input = readInput("update index:\n%s\ncontinue? ('yes'/'no'):" % json.dumps(indexDiff, indent=2))
            if input != "yes":
                sys.exit(0)
        viewsRequest["indexes"] = dbDef["indexes"]
        needPut = True

    if needPut:

        info("Creating views for database: " + fullDbName)
        viewsRequestStr = json.dumps(viewsRequest, indent=2)
        curl(url + "/" + fullDbName + "/_design/views", "PUT", viewsRequestStr)


def dropdbs(envConf, dbPrefix):
    global dbsConfigKey
    if not dbPrefix: dbPrefix = ''
    dbs = readDbs()
    authdbs = envConf[authDbsConfigKey]

    for key, list in envConf[dbsConfigKey].items():
        index = -1
        for url in list:
            index += 1
            if not key in dbs:
                continue
            username = parseUsername(url)
            if isCloudantUrl(url) and authdbs and username in authdbs:
                for authdb in authdbs[username]:
                    authUrl = url.replace(username + '.', authdb + '.')
                    dropdb(authUrl, dbs, key, index, authdb, dbPrefix)
            else:
                dropdb(url, dbs, key, index, username, dbPrefix)


def dropdb(url, dbs, key, index, username, dbPrefix):
    existingDbs = [db for db in curlJson(url + "/_all_dbs") if not db.startswith("_")]
    for db in dbs[key]:
        fullDbName = dbPrefix + db
        if fullDbName in existingDbs:
            info("Dropping database '%s' from '%s[%d]' in '%s'" % (fullDbName, key, index, username))
            curl(url + "/" + fullDbName, "DELETE")


def purgedbs(envConf, dbPrefix):
    global dbsConfigKey
    if not dbPrefix: dbPrefix = ''
    dbs = readDbs()

    for key, list in envConf[dbsConfigKey].items():
        index = -1
        for url in list:
            index += 1
            if not key in dbs:
                continue

            existingDbs = [db for db in curlJson(url + "/_all_dbs") if not db.startswith("_")]
            for db in dbs[key]:
                fullDbName = dbPrefix + db

                if fullDbName in existingDbs:
                    info("Purging database '%s' from '%s[%d]'" % (fullDbName, key, index));
                    allDocs = curlJson(url + "/" + fullDbName + '/_all_docs', "GET")
                    docs = []
                    for row in allDocs['rows']:
                        docs.append({
                            '_id': row['id'],
                            '_rev': row['value']['rev'],
                            '_deleted': True
                        })
                    bulkStr = json.dumps({'docs': docs}, indent=2)
                    if len(docs) > 0:
                        curlJson(url + "/" + fullDbName + '/_bulk_docs', "POST", bulkStr)


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
