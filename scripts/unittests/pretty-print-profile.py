#!/usr/bin/python
import sys

def verbose(str):
    print str

profileInfo = "".join(sys.stdin.readlines())
indent = 0
for p in profileInfo.split('|'):
    if p == "]": indent -= 1
    verbose((" " * indent * 4) + p)
    if p == "[": indent += 1
verbose("")

