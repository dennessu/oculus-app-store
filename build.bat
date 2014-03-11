@echo off
set gradle_arg=clean build install %1
for %%i in (%*) do (
  set gradle_arg=%gradle_arg% %%i
)
for /F "tokens=*" %%A in (dirs) do (
    call :GRADLE %%A || goto :ERROR
)

echo Build done
goto :EOF

:ERROR
popd
echo Build error
exit /b 1

:GRADLE
set DIR=%1
set DIR=%DIR:/=\%
pushd %DIR%
call gradle %gradle_arg% || goto :ERROR
popd
goto :EOF
