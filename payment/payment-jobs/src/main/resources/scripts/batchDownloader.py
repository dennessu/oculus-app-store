
import sys
import os
import getpass
import subprocess
import re
import ConfigParser
import AESCipher as cipher


def main():
    # Enforce python version
    print("start to download batch file")
    if sys.version_info[0] != 2 or sys.version_info[1] < 7:
        error("The script only works in python 2.x where x >= 7")
    #read the cipher key is specified
    readParams()
    configFile = readConfigFile()
    executeCommand("bash ./download.sh '%s' '%s' '%s'" % (configFile.username, configFile.password, configFile.url))

def readParams():
    def printUsage():
        error("Usage: python ./batchDownloader.py [-key:cipherkey]\n")

    def readArg(argv, argName, currentValue):
        argPrefix = "-" + argName + ":"
        if argv.startswith(argPrefix):
            return (argv[len(argPrefix):])
        return None

    global cipherKey
    cipherKey = None

    while len(sys.argv) > 0:
        argv = sys.argv.pop(0)
        cipherKey = readArg(argv, "key", cipherKey)

class ConfigFile:
    def __init__(self):
        self.username = None
        self.password = None
        self.url = None

def safeStrip(value):
    if value is not None:
        return value.strip()
    else:
        return None

def readPassword(message):
    return getpass.getpass(message)

def error(message):
    sys.stderr.write("ERROR: " + message + "\n")
    sys.exit(1)

def isNoneOrEmpty(value):
    return value is None or len(value) == 0

def readConfigFile():
    print("read config file")
    configFilePath = "batch.conf"
    if not os.path.isfile(configFilePath):
        error("Config file %s not found." % configFilePath)

    try:
        configuration = ConfigParser.SafeConfigParser()
        configuration.readfp(open(configFilePath))

        section = "payment"

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

        if readOption("intended_empty") == "true":
            # intended empty config file
            sys.exit(0)
        configFile = ConfigFile()
        configFile.url = readRequiredOption("url")
        configFile.username = readRequiredOption("username")
        configFile.password = readEncryptedOption("password")
        return configFile
    except IOError as e:
        error("Failed to open config file %s. Exception: %s" % (configFilePath, e))
    except (ConfigParser.NoSectionError, ConfigParser.MissingSectionHeaderError) as e:
        error("Invalid configuration file %s." % configFilePath)

def executeCommand(command):
    try:
        returnCode = subprocess.call(command, shell = True)
        if returnCode != 0:
            error("Return code %d in command: %s" % (returnCode, command))
    except OSError as e:
        error("Error executing command: " + command)

main()