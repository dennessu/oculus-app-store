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

    input_users = []
    for user in users:
        input_user = {}
        input_user["username"] = user["username"]
        input_user["email"] = user["email"]
        input_users.append(input_user)

    with open(args.output, 'w') as outfile:
        json.dump(input_users, outfile)

def read_args():
    parser = argparse.ArgumentParser(description='Id Migration Script')
    parser.add_argument('-i', action="store", metavar='input_file', dest="input", help='the input file', required=True)
    parser.add_argument('-o', action="store", metavar='output_file', dest="output", help='the output file, default is results.json', default='results.json')
    return parser.parse_args()

def randomword(length):
   return ''.join(random.choice(string.lowercase) for i in range(length)) 

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

