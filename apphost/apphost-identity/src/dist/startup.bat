@echo off
if not exist .\logs\nul (
    md logs
)

set APPHOST_CLI_OPTS=-DconfigDir=./conf -Denvironment=onebox
./bin/apphost-cli.bat
