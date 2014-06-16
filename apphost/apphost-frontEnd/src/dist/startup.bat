@echo off
if not exist .\logs\nul (
    md logs
)

set APPHOST_FRONTEND_OPTS=-DconfigDir=./conf -Denvironment=onebox
./bin/apphost-frontEnd.bat
