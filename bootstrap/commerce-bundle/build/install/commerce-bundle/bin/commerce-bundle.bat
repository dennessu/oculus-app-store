@if "%DEBUG%" == "" @echo off
@rem ##########################################################################
@rem
@rem  commerce-bundle startup script for Windows
@rem
@rem ##########################################################################

@rem Set local scope for the variables with windows NT shell
if "%OS%"=="Windows_NT" setlocal

@rem Add default JVM options here. You can also use JAVA_OPTS and COMMERCE_BUNDLE_OPTS to pass JVM options to this script.
set DEFAULT_JVM_OPTS=

set DIRNAME=%~dp0
if "%DIRNAME%" == "" set DIRNAME=.
set APP_BASE_NAME=%~n0
set APP_HOME=%DIRNAME%..

@rem Find java.exe
if defined JAVA_HOME goto findJavaFromJavaHome

set JAVA_EXE=java.exe
%JAVA_EXE% -version >NUL 2>&1
if "%ERRORLEVEL%" == "0" goto init

echo.
echo ERROR: JAVA_HOME is not set and no 'java' command could be found in your PATH.
echo.
echo Please set the JAVA_HOME variable in your environment to match the
echo location of your Java installation.

goto fail

:findJavaFromJavaHome
set JAVA_HOME=%JAVA_HOME:"=%
set JAVA_EXE=%JAVA_HOME%/bin/java.exe

if exist "%JAVA_EXE%" goto init

echo.
echo ERROR: JAVA_HOME is set to an invalid directory: %JAVA_HOME%
echo.
echo Please set the JAVA_HOME variable in your environment to match the
echo location of your Java installation.

goto fail

:init
@rem Get command-line arguments, handling Windowz variants

if not "%OS%" == "Windows_NT" goto win9xME_args
if "%@eval[2+2]" == "4" goto 4NT_args

:win9xME_args
@rem Slurp the command line arguments.
set CMD_LINE_ARGS=
set _SKIP=2

:win9xME_args_slurp
if "x%~1" == "x" goto execute

set CMD_LINE_ARGS=%*
goto execute

:4NT_args
@rem Get arguments from the 4NT Shell from JP Software
set CMD_LINE_ARGS=%$

:execute
@rem Setup the command line

set CLASSPATH=%APP_HOME%\lib\commerce-bundle-0.0.1-SNAPSHOT.jar;%APP_HOME%\lib\groovy-all-2.2.1.jar;%APP_HOME%\lib\rating-app-0.0.1-SNAPSHOT.jar;%APP_HOME%\lib\entitlement-app-0.0.1-SNAPSHOT.jar;%APP_HOME%\lib\fulfilment-app-0.0.1-SNAPSHOT.jar;%APP_HOME%\lib\cart-app-0.0.1-SNAPSHOT.jar;%APP_HOME%\lib\billing-app-0.0.1-SNAPSHOT.jar;%APP_HOME%\lib\order-app-0.0.1-SNAPSHOT.jar;%APP_HOME%\lib\payment-app-0.0.1-SNAPSHOT.jar;%APP_HOME%\lib\email-app-0.0.1-SNAPSHOT.jar;%APP_HOME%\lib\common-lib-0.0.1-SNAPSHOT.jar;%APP_HOME%\lib\common-configuration-0.0.1-SNAPSHOT.jar;%APP_HOME%\lib\configuration-data-0.0.1-SNAPSHOT.jar;%APP_HOME%\lib\common-sharding-0.0.1-SNAPSHOT.jar;%APP_HOME%\lib\jersey-container-grizzly2-http-2.5.1.jar;%APP_HOME%\lib\jersey-spring3-2.5.1.jar;%APP_HOME%\lib\rating-rest-0.0.1-SNAPSHOT.jar;%APP_HOME%\lib\entitlement-rest-0.0.1-SNAPSHOT.jar;%APP_HOME%\lib\fulfilment-rest-0.0.1-SNAPSHOT.jar;%APP_HOME%\lib\cart-rest-0.0.1-SNAPSHOT.jar;%APP_HOME%\lib\billing-rest-0.0.1-SNAPSHOT.jar;%APP_HOME%\lib\billing-jobs-0.0.1-SNAPSHOT.jar;%APP_HOME%\lib\order-jobs-0.0.1-SNAPSHOT.jar;%APP_HOME%\lib\identity-app-0.0.1-SNAPSHOT.jar;%APP_HOME%\lib\order-rest-0.0.1-SNAPSHOT.jar;%APP_HOME%\lib\payment-rest-0.0.1-SNAPSHOT.jar;%APP_HOME%\lib\email-rest-0.0.1-SNAPSHOT.jar;%APP_HOME%\lib\commons-collections-3.2.1.jar;%APP_HOME%\lib\jcl-over-slf4j-1.7.6.jar;%APP_HOME%\lib\jackson-databind-2.2.3.jar;%APP_HOME%\lib\slf4j-api-1.7.6.jar;%APP_HOME%\lib\spymemcached-2.10.4.jar;%APP_HOME%\lib\spring-context-4.0.2.RELEASE.jar;%APP_HOME%\lib\logback-core-1.1.1.jar;%APP_HOME%\lib\javax.ws.rs-api-2.0.jar;%APP_HOME%\lib\logback-classic-1.1.1.jar;%APP_HOME%\lib\log4j-over-slf4j-1.7.6.jar;%APP_HOME%\lib\langur-core-0.0.1-SNAPSHOT.jar;%APP_HOME%\lib\jackson-core-2.2.3.jar;%APP_HOME%\lib\spring-beans-4.0.2.RELEASE.jar;%APP_HOME%\lib\jackson-jaxrs-json-provider-2.2.3.jar;%APP_HOME%\lib\spring-core-4.0.2.RELEASE.jar;%APP_HOME%\lib\jul-to-slf4j-1.7.6.jar;%APP_HOME%\lib\jackson-annotations-2.2.3.jar;%APP_HOME%\lib\postgresql-9.3-1100-jdbc41.jar;%APP_HOME%\lib\hibernate-entitymanager-4.1.7.Final.jar;%APP_HOME%\lib\log4j-1.2.17.jar;%APP_HOME%\lib\bonecp-spring-0.8.0-rc1.jar;%APP_HOME%\lib\hibernate-core-4.1.7.Final.jar;%APP_HOME%\lib\btm-3.0.0-SNAPSHOT.jar;%APP_HOME%\lib\spring-aop-4.0.2.RELEASE.jar;%APP_HOME%\lib\spring-tx-4.0.2.RELEASE.jar;%APP_HOME%\lib\spring-orm-4.0.2.RELEASE.jar;%APP_HOME%\lib\spring-jdbc-4.0.2.RELEASE.jar;%APP_HOME%\lib\hibernate-commons-annotations-4.0.1.Final.jar;%APP_HOME%\lib\javax.inject-2.2.0-b21.jar;%APP_HOME%\lib\grizzly-http-server-2.3.8.jar;%APP_HOME%\lib\jersey-common-2.5.1.jar;%APP_HOME%\lib\jersey-server-2.5.1.jar;%APP_HOME%\lib\jersey-container-servlet-core-2.5.1.jar;%APP_HOME%\lib\hk2-2.2.0-b21.jar;%APP_HOME%\lib\spring-bridge-2.2.0-b21.jar;%APP_HOME%\lib\spring-expression-4.0.2.RELEASE.jar;%APP_HOME%\lib\rating-spec-0.0.1-SNAPSHOT.jar;%APP_HOME%\lib\oom-core-0.0.1-SNAPSHOT.jar;%APP_HOME%\lib\commons-beanutils-1.8.3.jar;%APP_HOME%\lib\rating-core-0.0.1-SNAPSHOT.jar;%APP_HOME%\lib\javax.servlet-api-3.1.0.jar;%APP_HOME%\lib\rating-common-0.0.1-SNAPSHOT.jar;%APP_HOME%\lib\rating-clientproxy-0.0.1-SNAPSHOT.jar;%APP_HOME%\lib\oom-processor-0.0.1-SNAPSHOT.jar;%APP_HOME%\lib\entitlement-core-0.0.1-SNAPSHOT.jar;%APP_HOME%\lib\fulfilment-db-0.0.1-SNAPSHOT.jar;%APP_HOME%\lib\fulfilment-common-0.0.1-SNAPSHOT.jar;%APP_HOME%\lib\fulfilment-spec-0.0.1-SNAPSHOT.jar;%APP_HOME%\lib\commons-io-2.4.jar;%APP_HOME%\lib\fulfilment-core-0.0.1-SNAPSHOT.jar;%APP_HOME%\lib\fulfilment-clientproxy-0.0.1-SNAPSHOT.jar;%APP_HOME%\lib\cart-spec-0.0.1-SNAPSHOT.jar;%APP_HOME%\lib\cart-common-0.0.1-SNAPSHOT.jar;%APP_HOME%\lib\cart-core-0.0.1-SNAPSHOT.jar;%APP_HOME%\lib\cart-db-0.0.1-SNAPSHOT.jar;%APP_HOME%\lib\billing-spec-0.0.1-SNAPSHOT.jar;%APP_HOME%\lib\billing-core-0.0.1-SNAPSHOT.jar;%APP_HOME%\lib\quartz-2.2.1.jar;%APP_HOME%\lib\order-core-0.0.1-SNAPSHOT.jar;%APP_HOME%\lib\order-spec-0.0.1-SNAPSHOT.jar;%APP_HOME%\lib\identity-rest-0.0.1-SNAPSHOT.jar;%APP_HOME%\lib\order-db-0.0.1-SNAPSHOT.jar;%APP_HOME%\lib\payment-spec-0.0.1-SNAPSHOT.jar;%APP_HOME%\lib\payment-common-0.0.1-SNAPSHOT.jar;%APP_HOME%\lib\payment-core-0.0.1-SNAPSHOT.jar;%APP_HOME%\lib\payment-db-0.0.1-SNAPSHOT.jar;%APP_HOME%\lib\payment-clientproxy-0.0.1-SNAPSHOT.jar;%APP_HOME%\lib\email-clientproxy-0.0.1-SNAPSHOT.jar;%APP_HOME%\lib\email-core-0.0.1-SNAPSHOT.jar;%APP_HOME%\lib\email-spec-0.0.1-SNAPSHOT.jar;%APP_HOME%\lib\guava-15.0.jar;%APP_HOME%\lib\async-http-client-1.7.22.jar;%APP_HOME%\lib\swagger-annotations-1.3.2-J-SNAPSHOT.jar;%APP_HOME%\lib\jackson-jaxrs-base-2.2.3.jar;%APP_HOME%\lib\jackson-module-jaxb-annotations-2.2.3.jar;%APP_HOME%\lib\jboss-transaction-api_1.1_spec-1.0.0.Final.jar;%APP_HOME%\lib\dom4j-1.6.1.jar;%APP_HOME%\lib\hibernate-jpa-2.0-api-1.0.1.Final.jar;%APP_HOME%\lib\javassist-3.15.0-GA.jar;%APP_HOME%\lib\bonecp-0.8.0-rc1.jar;%APP_HOME%\lib\antlr-2.7.7.jar;%APP_HOME%\lib\aopalliance-1.0.jar;%APP_HOME%\lib\grizzly-http-2.3.8.jar;%APP_HOME%\lib\javax.annotation-api-1.2.jar;%APP_HOME%\lib\hk2-api-2.2.0-b21.jar;%APP_HOME%\lib\hk2-locator-2.2.0-b21.jar;%APP_HOME%\lib\osgi-resource-locator-1.0.1.jar;%APP_HOME%\lib\jersey-client-2.5.1.jar;%APP_HOME%\lib\validation-api-1.1.0.Final.jar;%APP_HOME%\lib\hk2-utils-2.2.0-b21.jar;%APP_HOME%\lib\config-types-2.2.0-b21.jar;%APP_HOME%\lib\core-2.2.0-b21.jar;%APP_HOME%\lib\hk2-config-2.2.0-b21.jar;%APP_HOME%\lib\hk2-runlevel-2.2.0-b21.jar;%APP_HOME%\lib\class-model-2.2.0-b21.jar;%APP_HOME%\lib\jersey-bean-validation-2.5.1.jar;%APP_HOME%\lib\catalog-spec-0.0.1-SNAPSHOT.jar;%APP_HOME%\lib\entitlement-spec-0.0.1-SNAPSHOT.jar;%APP_HOME%\lib\freemarker-2.3.20.jar;%APP_HOME%\lib\hickory-1.0.0.jar;%APP_HOME%\lib\entitlement-clientproxy-0.0.1-SNAPSHOT.jar;%APP_HOME%\lib\entitlement-db-0.0.1-SNAPSHOT.jar;%APP_HOME%\lib\persistence-api-1.0.2.jar;%APP_HOME%\lib\hibernate-validator-5.0.2.Final.jar;%APP_HOME%\lib\slf4j-simple-1.7.6.jar;%APP_HOME%\lib\fastjson-1.1.15.jar;%APP_HOME%\lib\json-path-0.9.1.jar;%APP_HOME%\lib\orika-core-1.1.4.jar;%APP_HOME%\lib\javax.el-2.2.4.jar;%APP_HOME%\lib\identity-spec-0.0.1-SNAPSHOT.jar;%APP_HOME%\lib\javax.el-api-2.2.4.jar;%APP_HOME%\lib\commons-lang-2.5.jar;%APP_HOME%\lib\ehcache-core-2.6.6.jar;%APP_HOME%\lib\billing-db-0.0.1-SNAPSHOT.jar;%APP_HOME%\lib\spring-context-support-4.0.2.RELEASE.jar;%APP_HOME%\lib\billing-clientproxy-0.0.1-SNAPSHOT.jar;%APP_HOME%\lib\c3p0-0.9.1.1.jar;%APP_HOME%\lib\aspectjrt-1.7.4.jar;%APP_HOME%\lib\aspectjweaver-1.7.4.jar;%APP_HOME%\lib\order-clientproxy-0.0.1-SNAPSHOT.jar;%APP_HOME%\lib\authorization-lib-0.0.1-SNAPSHOT.jar;%APP_HOME%\lib\identity-data-0.0.1-SNAPSHOT.jar;%APP_HOME%\lib\oauth-spec-0.0.1-SNAPSHOT.jar;%APP_HOME%\lib\identity-core-0.0.1-SNAPSHOT.jar;%APP_HOME%\lib\identity-common-0.0.1-SNAPSHOT.jar;%APP_HOME%\lib\braintree-java-2.22.1.jar;%APP_HOME%\lib\email-common-0.0.1-SNAPSHOT.jar;%APP_HOME%\lib\email-db-0.0.1-SNAPSHOT.jar;%APP_HOME%\lib\netty-3.6.6.Final.jar;%APP_HOME%\lib\grizzly-framework-2.3.8.jar;%APP_HOME%\lib\javax.inject-1.jar;%APP_HOME%\lib\asm-all-repackaged-2.2.0-b21.jar;%APP_HOME%\lib\cglib-2.2.0-b21.jar;%APP_HOME%\lib\tiger-types-1.4.jar;%APP_HOME%\lib\bean-validator-2.2.0-b21.jar;%APP_HOME%\lib\junit-4.3.1.jar;%APP_HOME%\lib\catalog-common-0.0.1-SNAPSHOT.jar;%APP_HOME%\lib\entitlement-common-0.0.1-SNAPSHOT.jar;%APP_HOME%\lib\json-smart-1.2.jar;%APP_HOME%\lib\javassist-3.12.0.GA.jar;%APP_HOME%\lib\paranamer-2.3.jar;%APP_HOME%\lib\order-mock-0.0.1-SNAPSHOT.jar;%APP_HOME%\lib\jta-1.1.jar;%APP_HOME%\lib\kryo-2.22.jar;%APP_HOME%\lib\spring-web-4.0.2.RELEASE.jar;%APP_HOME%\lib\classmate-1.0.0.jar;%APP_HOME%\lib\commons-logging-1.1.3.jar;%APP_HOME%\lib\jboss-logging-3.1.1.GA.jar

@rem Execute commerce-bundle
"%JAVA_EXE%" %DEFAULT_JVM_OPTS% %JAVA_OPTS% %COMMERCE_BUNDLE_OPTS%  -classpath "%CLASSPATH%" com.junbo.bootstrap.CommerceMain %CMD_LINE_ARGS%

:end
@rem End local scope for the variables with windows NT shell
if "%ERRORLEVEL%"=="0" goto mainEnd

:fail
rem Set variable COMMERCE_BUNDLE_EXIT_CONSOLE if you need the _script_ return code instead of
rem the _cmd.exe /c_ return code!
if  not "" == "%COMMERCE_BUNDLE_EXIT_CONSOLE%" exit 1
exit /b 1

:mainEnd
if "%OS%"=="Windows_NT" endlocal

:omega
