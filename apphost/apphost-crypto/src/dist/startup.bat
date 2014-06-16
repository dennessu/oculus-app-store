@echo off
if not exist .\logs\nul (
    md logs
)

set APPHOST_CRYPTO_OPTS=-DconfigDir=./conf -Denvironment=onebox
./bin/apphost-crypto.bat
