@echo on
set gradle_arg=clean install %1
for %%i in (%*) do (
  set gradle_arg=%gradle_arg% %%i
)
pushd "gradle\bootstrap"
call gradle %gradle_arg%
if %errorlevel% == 1 (
	popd
	goto END
)
popd

pushd oom
call gradle %gradle_arg%
if %errorlevel% == 1 (
	popd
	goto END
)
popd

pushd langur
call gradle %gradle_arg%
if %errorlevel% == 1 (
	popd
	goto END
)
popd


pushd common
call gradle %gradle_arg%
if %errorlevel% == 1 (
	popd
	goto END
)
popd

pushd identity
call gradle %gradle_arg%
if %errorlevel% == 1 (
	popd
	goto END
)
popd

pushd oauth
call gradle %gradle_arg%
if %errorlevel% == 1 (
	popd
	goto END
)
popd

pushd catalog
call gradle %gradle_arg%
if %errorlevel% == 1 (
	popd
	goto END
)
popd

pushd rating
call gradle %gradle_arg%
if %errorlevel% == 1 (
	popd
	goto END
)
popd

pushd payment
call gradle %gradle_arg%
if %errorlevel% == 1 (
	popd
	goto END
)
popd

pushd billing
call gradle %gradle_arg%
if %errorlevel% == 1 (
	popd
	goto END
)
popd

pushd cart
call gradle %gradle_arg%
if %errorlevel% == 1 (
	popd
	goto END
)
popd

pushd drm
call gradle %gradle_arg%
if %errorlevel% == 1 (
	popd
	goto END
)
popd

pushd entitlement
call gradle %gradle_arg%
if %errorlevel% == 1 (
	popd
	goto END
)
popd

pushd ewallet
call gradle %gradle_arg%
if %errorlevel% == 1 (
	popd
	goto END
)
popd

pushd fulfilment
call gradle %gradle_arg%
if %errorlevel% == 1 (
	popd
	goto END
)
popd

pushd order
call gradle %gradle_arg%
if %errorlevel% == 1 (
	popd
	goto END
)
popd

pushd subscription
call gradle %gradle_arg%
if %errorlevel% == 1 (
	popd
	goto END
)
popd

pushd "bootstrap"
call gradle %gradle_arg%
if %errorlevel% == 1 (
	popd
	goto END
)
popd

goto :END

:END
echo Build done
