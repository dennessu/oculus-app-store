#!/usr/bin/python

import sys
import os
import re
import httplib
import string
import json
import time
import argparse
import random
from threading import Thread
from Queue import Queue

def main():
    # Enforce python version
    if sys.version_info[0] != 2 or sys.version_info[1] < 7:
        error("The script only works in python 2.x where x >= 7")
    args = read_args()

    users_file = file(args.input)
    users = json.load(users_file)
    size = len(users)
    if users[size-1] == {}:
        del users[size-1]

    migration_result_file = file(args.migration_result_json)
    migration_result = json.load(migration_result_file)
    
    error_users = []
    for user in users:
        migration_user = migration_result.get(str(user["id"]))
        try:
            if migration_user.get("error") is not None:
                error_users.append(user)
        except (ValueError, KeyError, TypeError):
            print "JSON format error"

    with open(args.output, 'w') as outfile:
        json.dump(error_users, outfile)

def read_args():
    parser = argparse.ArgumentParser(description='Id Migration Script')
    parser.add_argument('-i', action="store", metavar='input_file', dest="input", help='the input file', required=True)
    parser.add_argument('-r', action="store", metavar="results of the migration", dest="migration_result_json", help="The migration results", required=True)
    parser.add_argument('-o', action="store", metavar='output_file', dest="output", help='the output file, default is results.json', default='results.json')
    return parser.parse_args()

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

