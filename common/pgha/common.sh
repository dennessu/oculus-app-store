#!/bin/bash

function forceKill {
	port=$1

	echo "kill process running with [$port] port"
	if (lsof -i:$port -t)
    then
	    kill -n 9 $(lsof -i:$port -t)
    else
	    echo "no process running with [$port] port..."
    fi
}