#!/usr/bin/python

import sys
import os
import getpass
import subprocess
import re
import ConfigParser
import AESCipher as cipher
from collections import OrderedDict

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

    dbName, env, dbVersion, command = readParams()
    configFile = readConfigFile(dbName, env)
    executeDbCommand(command, dbVersion, configFile)

def readParams():
    def printUsage():
        error("Usage: python ./dbcmd.py -db:<dbName> -env:<env> -ver:<dbVersion> [-cmd:<command>] [-yes] [-key:cipherkey] [-debug]\n")

    # Read input params
    sys.argv.pop(0)     # skip argv[0]
    if len(sys.argv) < 3:
        printUsage()

    def readArg(argv, argName, currentValue, isMatch):
        if isMatch:
            return (currentValue, True)
        argPrefix = "-" + argName + ":"
        if argv.startswith(argPrefix):
            return (argv[len(argPrefix):], True)
        return (currentValue, False)

    # Optional params
    dbName = None
    env = None
    dbVersion = None
    command = None
    confirmed = False

    global cipherKey
    global debug

    cipherKey = None
    debug = False

    while len(sys.argv) > 0:
        argv = sys.argv.pop(0)

        isMatch = False
        (dbName, isMatch) = readArg(argv, "db", dbName, isMatch)
        (env, isMatch) = readArg(argv, "env", env, isMatch)
        (dbVersion, isMatch) = readArg(argv, "ver", dbVersion, isMatch)
        (command, isMatch) = readArg(argv, "cmd", command, isMatch)
        (cipherKey, isMatch) = readArg(argv, "key", cipherKey, isMatch)

        if not isMatch:
            if argv == "-yes":
                confirmed = True
            elif argv == "-debug":
                debug = True
            else:
                error("Unknown argument: " + argv)

    # Required params
    if not dbName or not env or not dbVersion:
        printUsage()

    # Validate command
    def printValidCommands():
        info("Available Commands: ")
        info(
            "   status                  Show pending DB schema changes\n" +
            "   update                  Update the DB schema to latest version\n" +
            "   updateSQL               Output the SQL to be run for updating the DB schema to the latest version\n" +
            "   validate                Validate change logs\n" +
            "   create                  Create the database in environment\n" +
            "   drop                    Drop the database in test environment\n")
        sys.stdout.flush()

    if command is None:
        # Prompt for user input
        printValidCommands()
        command = readInput("Input the liquibase command: ")

    command = command.lower()
    if command not in set(["status", "update", "updatesql", "validate", "create", "drop"]):
        printValidCommands()
        error("Invalid command: " + command)

    if command in set(["update", "create", "drop"]) and not confirmed:
        # Ask for confirmation
        response = readInput("WARNING! Update action will make changes to database %s. Are you absolutely sure? ('yes'/'no'): " % dbName)
        if response.lower() != "yes":
            error("Aborting...")

    if command in set(["drop"]) and env in set(["int", "prod"]):
        error("drop is not supported in env %s. Please do it manually.", env)

    return (dbName, env, dbVersion, command)

class ConfigFile:
    def __init__(self):
        self.dbName = None
        self.env = None
        self.jdbcDriver = None
        self.shards = OrderedDict()

    def getShardConfig(self, shardId):
        shardConfig = self.shards[shardId]
        if shardConfig is None:
            error("Invalid shardId %s in database %s" % (shardId, self.dbName))
        return shardConfig

class ShardConfig:
    def __init__(self):
        self.shardId = None
        self.loginUserName = None
        self.loginPassword = None
        self.jdbcUrl = None
        self.schema = None

def readConfigFile(dbName, env):
    configFilePath = "conf/%s/%s.conf" % (env, dbName)
    if not os.path.isfile(configFilePath):
        error("Config file %s not found." % configFilePath)

    try:
        configuration = ConfigParser.SafeConfigParser()
        configuration.readfp(open(configFilePath))

        section = "liquibase"

        def readOption(option):
            if configuration.has_option(section, option):
                return safeStrip(configuration.get(section, option))
            return None

        def readEncryptedOption(option):
            global cipherKey
            optVal = readOption(option + '.encrypted')
            if optVal is not None:
                if cipherKey is None:
                    cipherKey = readPassword("Input the password cipher key: ")
                optVal = cipher.decryptData(cipherKey.strip(), optVal.strip())
            else:
                optVal = readOption(option)
            if isNoneOrEmpty(optVal):
                error("%s not found or empty in %s" % (option, configFilePath))
            return optVal

        def readRequiredOption(option):
            result = readOption(option)
            if isNoneOrEmpty(result):
                error("%s not found or empty in %s" % (option, configFilePath))
            return result

        configFile = ConfigFile()
        configFile.dbName = dbName
        configFile.env = env
        configFile.jdbcDriver = readRequiredOption("jdbc_driver")

        shardRange = readOption("shard_range")
        if isNoneOrEmpty(shardRange):
            # default to only one shard (shard 0)
            configFile.shards[None] = ShardConfig()
        else:
            for key in splitRange(shardRange):
                configFile.shards[key] = ShardConfig()
            if len(configFile.shards) == 0:
                error("Invalid shard_range: %s in %s" % (shardRange, configFilePath))

        loginUserName = readRequiredOption("login_username")

        # Password is optional. In PostgreSQL we recommend to disable password
        # We don't allow trailing spaces in passwords
        loginPassword = readEncryptedOption("login_password")

        for shardId, shardConfig in configFile.shards.items():
            shardConfig.shardId = shardId
            shardConfig.loginUserName = str.format(loginUserName, shardId)
            shardConfig.loginPassword = loginPassword   # only support same password

            jdbcUrlKey = "jdbc_url"
            if shardId is not None:
                jdbcUrlKey = "jdbc_url_%d" % shardId

            jdbcUrl = readRequiredOption(jdbcUrlKey)
            match = re.match(r'^(?P<jdbcUrl>jdbc:[^;]+)(;(?P<schema>\S+))?$', jdbcUrl)
            if match is None:
                error("Invalid format for %s in %s, expect <jdbcUrl>;<schema>" % (jdbcUrlKey, configFilePath))
            shardConfig.jdbcUrl = safeStrip(match.group("jdbcUrl"))
            shardConfig.schema = safeStrip(match.group("schema"))
            if shardConfig.schema is None:
                shardConfig.schema = "public"
        return configFile
    except IOError as e:
        error("Failed to open config file %s. Exception: %s" % (configFilePath, e))
    except (ConfigParser.NoSectionError, ConfigParser.MissingSectionHeaderError) as e:
        error("Invalid configuration file %s." % configFilePath)

def executeDbCommand(command, dbVersion, configFile):
    if command == "create":
        for shardId in configFile.shards.keys():
            createDb(shardId, configFile)
    elif command == "drop":
        for shardId in configFile.shards.keys():
            dropDb(shardId, configFile)
    else:
        for shardId in configFile.shards.keys():
            liquibase(command, dbVersion, shardId, configFile)

def createDb(shardId, configFile):
    shardConfig = configFile.getShardConfig(shardId)
    os.environ["PGSQL_USER"] = shardConfig.loginUserName
    os.environ["PGPASSWORD"] = shardConfig.loginPassword

    from urlparse import urlparse
    uri = urlparse(shardConfig.jdbcUrl.replace('jdbc:postgresql:', ''))
    (host, port) = uri.netloc.split(':')
    
    info("Creating %s.%s in %s:%s %s (shard %s)..." %
        (configFile.dbName, shardConfig.schema, host, port, configFile.env, shardConfig.shardId));

    command = "bash ./scripts/createdb.sh '%s' '%s' '%s' '%s'" % (configFile.dbName, shardConfig.schema, host, port)
    executeCommand(command)

def dropDb(shardId, configFile):
    shardConfig = configFile.getShardConfig(shardId)
    os.environ["PGSQL_USER"] = shardConfig.loginUserName
    os.environ["PGPASSWORD"] = shardConfig.loginPassword

    from urlparse import urlparse
    uri = urlparse(shardConfig.jdbcUrl.replace('jdbc:postgresql:', ''))
    (host, port) = uri.netloc.split(':')

    info("Dropping database %s in %s:%s %s..." % (configFile.dbName, host, port, configFile.env));

    # Double check to avoid dropping in prod and int
    if configFile.env in set(["prod", "int"]):
        error("drop is not supported in %s" % configFile.env)

    executeCommand("bash ./scripts/dropdb.sh '%s' '%s' '%s'" % (configFile.dbName, host, port))

def liquibase(command, dbVersion, shardId, configFile):
    shardConfig = configFile.getShardConfig(shardId)
    info("Executing Liquibase in %s for %s.%s (shard %s)..." %
        (configFile.env, configFile.dbName, shardConfig.schema, shardConfig.shardId));

    changeLogPath = "changelogs/%s/%s/changelog.xml" % (configFile.dbName, dbVersion)

    cmd = list([os.path.abspath('.') + "/liquibase"])
    # uncomment to enable debug trace
    global debug
    if debug:
        cmd.append("--logLevel=debug")
    cmd.append("--driver=" + configFile.jdbcDriver)
    cmd.append("--changeLogFile=" + changeLogPath)
    cmd.append("--username=" + shardConfig.loginUserName)
    if not isNoneOrEmpty(shardConfig.loginPassword):
        if os.name == "posix":
            cmd.append("--password='" + shardConfig.loginPassword + "'")
        else:
            cmd.append("--password=" + shardConfig.loginPassword)
    cmd.append("--url=" + shardConfig.jdbcUrl)
    if not isNoneOrEmpty(shardConfig.schema):
        cmd.append("--defaultSchemaName=" + shardConfig.schema)
    cmd.append(command)
    executeCommand(" ".join(cmd))

def executeCommand(command):
    try:
        returnCode = subprocess.call(command, shell = True)
        if returnCode != 0:
            error("Return code %d in command: %s" % (returnCode, command))
    except OSError as e:
        error("Error executing command: " + command)

def psql(command):
    pass

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

def info(message):
    print message
    sys.stdout.flush()

def error(message):
    sys.stderr.write("ERROR: " + message + "\n")
    sys.exit(1)

main()
