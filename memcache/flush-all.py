#!/usr/bin/python

import sys
import os
import subprocess
import re
import httplib
import json
import string
import memcache


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

    mc = memcache.Client(['127.0.0.1:11211'], debug=1)
    # set and get a dummy key to ensure memcached is up and running
    mc.set("dummy_test_key", "dummy_value")
    if not mc.get("dummy_test_key"):
        error("memcached server is not working properly, please check your environment")
    mc.flush_all()
    info("Local memcache is cleared.")

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
