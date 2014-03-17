#!/bin/sh
psqlDir="$1"

createdb() {
	local dbname="$1"
	local executable="$psqlDir\bin\psql.exe"
	echo "$executable"
	echo "create database $dbname"
	"$executable" -U postgres -c "DROP DATABASE IF EXISTS \"$dbname\""
	"$executable" -U postgres -c "CREATE DATABASE \"$dbname\""
}

updatedb() {
	pwd
	local dbname="$1"
	echo "update database $dbname"
	cd common/liquibase
	./updatedb.py $dbname onebox 0 update
	cd ../..
}

createAndUpdate() {
	local dbname="$1"
	createdb $dbname
	updatedb $dbname
}

function print_help(){
 
   echo "Usage: setupdb.sh {psqlDir}"
   echo "Usage sample: setupdb.sh D:\Program Files\PostgreSQL\9.3" 
   exit 1 
}

echo $psqlDirLen
if [ ${#psqlDir} == 0 ]; then
    print_help
fi

for i in `ls common/liquibase/conf`
do
	createAndUpdate $i
done
