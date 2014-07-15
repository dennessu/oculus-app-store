#!/usr/bin/python

import binascii
import sys

def main():
    selfName = sys.argv.pop(0)
    if len(sys.argv) == 0:
        error("usage: %s <filename>" % selfName)
    filename = sys.argv.pop(0)
    with open(filename, 'rb') as f:
        content = f.read()
        print(binascii.hexlify(content))

def error(message):
    sys.stderr.write("ERROR: " + message + "\n")
    sys.exit(1)

main()
