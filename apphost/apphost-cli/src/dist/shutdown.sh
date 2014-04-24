#!/usr/bin/env bash

kill -9 `cat "./logs/app.pid"` >/dev/null 2>&1