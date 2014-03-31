#!/bin/env python
from pylib.inputParam import inputParam
from pylib.liquibase import run
import sys

if len(sys.argv) <= 3:
    sys.stderr.write("Usage: ./updatedb.py <DATABASE_NAME> <ENVIRONMENT> <VERSION>\n")
    sys.exit(1)

inputParam.database = sys.argv.pop(1)
inputParam.environment = sys.argv.pop(1)
inputParam.dbVersion = sys.argv.pop(1)

run(inputParam)
