#!/bin/env python2.7

import sys
import ConfigParser
import os
import subprocess
import errno
import re
import fileinput
import random
from getpass import *

def error(message):
	sys.stdout.write(message)	
	sys.exit(1)

def run(inputParam):
    # Enforce python version
    if sys.version_info[0] <= 2 and sys.version_info[1] <= 5:
        error("must use python 2.6 or greater")
    
    if sys.version_info[0] >= 3:
        error("must use python 2.6 or greater")
 
	# Enforce to current directory
	try:
		os.chdir(os.path.dirname(sys.argv[0]))
	except OSError, e:
		error(e+"\nUnable to change into directory : " + os.path.dirname(sys.argv[0]) + ". Quitting...\n")

    #===============================================================================
    # Get command line arguments
    #===============================================================================
    sys.argv.pop(0) 
    if len(sys.argv) > 0:
        # Incorrect usage
        if len(sys.argv) < 1 or len(sys.argv) > 3:
            error("""
        ./liquibase.py PROJECT ENVIRONMENT DBVERSION [context]
            """)
			
        # Enable batch mode and get the args
		
        Args = [""]*4 # Initialize list to a size of 4
        for i in range(len(sys.argv)):
            Args[i] = sys.argv[i]
        inputParam.environment = Args[0]
        inputParam.command = Args[1]
        exit(inputParam.command)   
    
    #===============================================================================
    # Get environment config
    #===============================================================================
    
    # Prompt user for Environment
    if not os.path.isdir("conf/"+inputParam.project+"/"+inputParam.environment) or len(str.strip(inputParam.environment)) == 0 :
        error("Environment config '"+inputParam.environment+"' not found under ./conf/"+inputParam.project+". Quitting...\n")
    
    # Encrypt passwords in configs
    fname = "conf/"+inputParam.project+"/"+inputParam.environment+"/db.conf"
    # The configuration file should be xx=xx format
    # if the config line starts with #, just ignore this line
    if os.path.exists(fname):
		file = open(fname, "rb")
		file_content = ""
		for line in file:
			delim = None
			if line[0] == "#":
				continue
			for char in line:
				if char == "=":
					delim = char
					break
			if delim is not None:
				key, value = line.split(delim, 1)
			file_content = file_content+line
		file.close()
		# Rewrite the file with an encrypted password instead
		file = open(fname, "wb")
		file.write(file_content)
		file.close()  

    # Load environment config. This contains the usernames, passwords, and etc
    try:
        configuration = ConfigParser.SafeConfigParser()
        configuration.readfp(open("conf/"+inputParam.project+"/"+inputParam.environment+"/db.conf"))
        # Push the config into this map instead
        config = {}
        # Pass config values in as properties so we can use them in changelogs
        for key, value in configuration.items("liquibase"):
            config[key] = value
    except IOError, ie:
        error(e+"\nUnable to open environment config file.\n")
    except (ConfigParser.NoSectionError, ConfigParser.MissingSectionHeaderError), e:
        error("Invalid configuration file\n")
    
    #===============================================================================
    # Prompt user for Liquibase Command
    #===============================================================================
    sys.stdout.write("""
		update                  Update schema to current version
	""")
    sys.stdout.flush()
    if inputParam.command == "":
        inputParam.command = raw_input("Liquibase command to run: ")
	
	#Build the command line args to run
    cmd = list([os.path.abspath('.') + "/liquibase"])
    cmd.append("--logLevel=info")
    cmd.append("--driver=" + config["jdbc_driver"])
    log_file_name = "changelogs/"+inputParam.project+"/"+inputParam.dbVersion+"/changelog.xml"	
    cmd.append("--changeLogFile="+log_file_name)
    cmd.append("--username="+config["liquibase_username"])
    cmd.append("--password="+config["liquibase_password"])
    jdbc_url=config["jdbc_url"]
    cmd.append("--url="+jdbc_url)
    cmd.append(inputParam.command)

    try:
        return_code = subprocess.call(" ".join(cmd), shell=True)
    except OSError, e:
        error("Error processing liquibase. ")
	
    if return_code !=0:
        error("Error processing liquibase." + str(return_code))
    