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

    white_list_user_names = []
    if args.white_list_user is not None:
        user_name_file = open(str(args.white_list_user), "r")
        for line in user_name_file.readlines():
            line = line.rstrip()
            white_list_user_names.append(line.lower())
        user_name_file.close()

    input_users = []
    for user in users:
        if args.mask_user == "true" and user["username"].lower() not in white_list_user_names:
            user["username"] = randomword(20)
            user["email"] = randomword(20) + '@oculusTest.com'
            user["firstName"] = randomword(30)
            user["lastName"] = randomword(30)
        input_users.append(user)

    with open(args.output, 'w') as outfile:
        json.dump(input_users, outfile)

def read_args():
    parser = argparse.ArgumentParser(description='Id Migration Script')
    parser.add_argument('-i', action="store", metavar='input_file', dest="input", help='the input file', required=True)
    parser.add_argument('-o', action="store", metavar='output_file', dest="output", help='the output file, default is results.json', default='results.json')
    parser.add_argument('-mask_user', action="store", metavar='mask_user_true_or_false', dest="mask_user", help="whether we need to mask user name and mail", required=False)
    parser.add_argument('-white_list_user', action="store", metavar="white_list_user_config_file", dest="white_list_user", help="The username list to be white list", required=False)
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

