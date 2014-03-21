#!/bin/env python

import sys
import ConfigParser
import os
import subprocess
import errno
import re
import fileinput
import random
from handythread import *
from getpass import *

def error(message):
	sys.stdout.write(message)	
	sys.exit(1)

def split_range(param):
    result = set()
    for part in param.split(','):
        x = part.split('..')
        result.update(range(int(x[0]), int(x[-1]) + 1))
    return sorted(result)

def run(inputParam):
    # Enforce python version
    if sys.version_info[0] != 2:
        error("must use python 2.x")

	# Enforce to current directory
	try:
		os.chdir(os.path.dirname(sys.argv[0]))
	except OSError, e:
		error(e+"\nUnable to change into directory : " + os.path.dirname(sys.argv[0]) + ". Quitting...\n")

    #========================================
    # Script arguments
    #========================================
    sys.argv.pop(0)   # skipped the first arg which is script name

    if len(sys.argv) > 0:
        # arguments check
        if len(sys.argv) != 3:
            error("""
        ./liquibase.py DATABASE ENVIRONMENT DBVERSION
            """)
    
    #=========================================
    # Environment config
    #=========================================
    
    # Check config file existence
    if not os.path.isdir("conf/"+inputParam.database+"/"+inputParam.environment) or len(str.strip(inputParam.environment)) == 0 :
        error("Environment config '"+inputParam.environment+"' not found under ./conf/"+inputParam.database+". Quitting...\n")

    # Load environment config.
    try:
        configuration = ConfigParser.SafeConfigParser()
        configuration.readfp(open("conf/"+inputParam.database+"/"+inputParam.environment+"/db.conf"))
        # Push the config into this map instead
        config = {}
        # Pass config values in as properties so we can use them in changelogs
        for key, value in configuration.items("liquibase"):
            config[key] = value
    except IOError, ie:
        error(e+"\nUnable to open environment config file.\n")
    except (ConfigParser.NoSectionError, ConfigParser.MissingSectionHeaderError), e:
        error("Invalid configuration file\n")
    
    #============================================
    # Prompt Liquibase command
    #============================================
    sys.stdout.write("""
        status                  Show unrun changes list
        update                  Update db schema to latest version
        updateSQL               Output sql for manually update db schema to latest version
        validate                Checks changelog for errors
	""")
    sys.stdout.flush()
    inputParam.command = raw_input("Liquibase command to run: ")

    c = str.upper(inputParam.command) # Get the command
    if c == "UPDATE":
        # Prompt for confirmation on direct changes
        response = raw_input("WARNING! This action will make changes to the database. Are you absolutely sure? ('yes'/'no'): ")
        if response != "yes":
            error("Cancelled...\n")
    elif not re.compile("^(STATUS|UPDATESQL|VALIDATE)$").match(c):
        error("Please enter a valid command\n")

    def partition(shardid):
        sys.stdout.write("Running against shard "+shardid+"...\n");
        #Build the command line args to run
        cmd = list([os.path.abspath('.') + "/liquibase"])
        cmd.append("--logLevel=debug")
        cmd.append("--driver=" + config["jdbc_driver"])
        change_log_file = "changelogs/"+inputParam.database+"/"+inputParam.dbVersion+"/changelog.xml"
        cmd.append("--changeLogFile="+change_log_file)
        login_user = config["login_username"].replace('{0}', shardid)
        cmd.append("--username="+login_user)
        cmd.append("--password="+config["login_password"])
        cmd.append("--defaultSchemaName=" + login_user)
        if (config.has_key("jdbc_url") and len(config["jdbc_url"]) != 0):
            jdbc_url=config["jdbc_url"]
        else:
            jdbc_url=config["jdbc_url_" + shardid]

        cmd.append("--url="+jdbc_url)
        cmd.append(inputParam.command)

        try:
            return_code = subprocess.call(" ".join(cmd), shell=True)
        except OSError, e:
            error("Error processing liquibase. ")

        if return_code !=0:
            error("Error processing liquibase." + str(return_code))

    if config.has_key("shard_range"):
        shard_range = [str(x) for x in split_range(config["shard_range"])]
        foreach(partition, shard_range, 1)
    else:
        partition("noshard")
