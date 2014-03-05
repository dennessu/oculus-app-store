@echo on

pushd "identity-bundle"
call gradle clean build installApp distTar
if %errorlevel% == 1 (
	popd
	goto END
)
popd


pushd "catalog-bundle"
call gradle clean build installApp distTar
if %errorlevel% == 1 (
	popd
	goto END
)
popd


pushd "commerce-bundle"
call gradle clean build installApp distTar
if %errorlevel% == 1 (
	popd
	goto END
)
popd

:END
echo Build done
