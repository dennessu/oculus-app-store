#!/bin/bash
cd ..
echo ${PWD}
bundleName=$1
projRoot=${PWD}
cd "${projRoot}/bootstrap/${bundleName}-bundle"
gradle clean build installApp

cd "${projRoot}/bootstrap/${bundleName}-bundle/build/install/${bundleName}-bundle"
./startup.sh