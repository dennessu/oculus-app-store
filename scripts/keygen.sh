#!/bin/bash
length=${1:-24}
cat /dev/random | tr -dc 'a-zA-Z0-9-=!#^*()_+~' | fold -w $length | head -n 1
