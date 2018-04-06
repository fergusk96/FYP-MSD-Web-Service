@REM ----------------------------------------------------------------------------
@REM Copyright 2001-2004 The Apache Software Foundation.
@REM
@REM Licensed under the Apache License, Version 2.0 (the "License");
@REM you may not use this file except in compliance with the License.
@REM You may obtain a copy of the License at
@REM
@REM      http://www.apache.org/licenses/LICENSE-2.0
@REM
@REM Unless required by applicable law or agreed to in writing, software
@REM distributed under the License is distributed on an "AS IS" BASIS,
@REM WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
@REM See the License for the specific language governing permissions and
@REM limitations under the License.
@REM ----------------------------------------------------------------------------
@REM

@echo off

set ERROR_CODE=0

:init
@REM Decide how to startup depending on the version of windows

@REM -- Win98ME
if NOT "%OS%"=="Windows_NT" goto Win9xArg

@REM set local scope for the variables with windows NT shell
if "%OS%"=="Windows_NT" @setlocal

@REM -- 4NT shell
if "%eval[2+2]" == "4" goto 4NTArgs

@REM -- Regular WinNT shell
set CMD_LINE_ARGS=%*
goto WinNTGetScriptDir

@REM The 4NT Shell from jp software
:4NTArgs
set CMD_LINE_ARGS=%$
goto WinNTGetScriptDir

:Win9xArg
@REM Slurp the command line arguments.  This loop allows for an unlimited number
@REM of arguments (up to the command line limit, anyway).
set CMD_LINE_ARGS=
:Win9xApp
if %1a==a goto Win9xGetScriptDir
set CMD_LINE_ARGS=%CMD_LINE_ARGS% %1
shift
goto Win9xApp

:Win9xGetScriptDir
set SAVEDIR=%CD%
%0\
cd %0\..\.. 
set BASEDIR=%CD%
cd %SAVEDIR%
set SAVE_DIR=
goto repoSetup

:WinNTGetScriptDir
set BASEDIR=%~dp0\..

:repoSetup


if "%JAVACMD%"=="" set JAVACMD=java

if "%REPO%"=="" set REPO=%BASEDIR%\repo

set CLASSPATH="%BASEDIR%"\etc;"%REPO%"\org\neo4j\driver\neo4j-java-driver\1.0.5\neo4j-java-driver-1.0.5.jar;"%REPO%"\com\fasterxml\jackson\core\jackson-core\2.5.1\jackson-core-2.5.1.jar;"%REPO%"\org\json\json\20180130\json-20180130.jar;"%REPO%"\com\googlecode\json-simple\json-simple\1.1.1\json-simple-1.1.1.jar;"%REPO%"\se\michaelthelin\spotify\spotify-web-api-java\2.0.0\spotify-web-api-java-2.0.0.jar;"%REPO%"\org\apache\httpcomponents\httpclient-cache\4.5.5\httpclient-cache-4.5.5.jar;"%REPO%"\org\apache\httpcomponents\httpclient\4.5.5\httpclient-4.5.5.jar;"%REPO%"\org\apache\httpcomponents\httpcore\4.4.9\httpcore-4.4.9.jar;"%REPO%"\commons-codec\commons-codec\1.10\commons-codec-1.10.jar;"%REPO%"\commons-logging\commons-logging\1.2\commons-logging-1.2.jar;"%REPO%"\com\neovisionaries\nv-i18n\1.22\nv-i18n-1.22.jar;"%REPO%"\com\fasterxml\jackson\core\jackson-databind\2.9.4\jackson-databind-2.9.4.jar;"%REPO%"\com\fasterxml\jackson\core\jackson-annotations\2.9.0\jackson-annotations-2.9.0.jar;"%REPO%"\com\sparkjava\spark-template-freemarker\2.7.1\spark-template-freemarker-2.7.1.jar;"%REPO%"\org\freemarker\freemarker\2.3.26-incubating\freemarker-2.3.26-incubating.jar;"%REPO%"\org\neo4j\test\neo4j-harness\3.0.4\neo4j-harness-3.0.4.jar;"%REPO%"\org\neo4j\neo4j\3.0.4\neo4j-3.0.4.jar;"%REPO%"\org\neo4j\neo4j-kernel\3.0.4\neo4j-kernel-3.0.4.jar;"%REPO%"\org\neo4j\neo4j-lucene-index\3.0.4\neo4j-lucene-index-3.0.4.jar;"%REPO%"\org\apache\lucene\lucene-analyzers-common\5.5.0\lucene-analyzers-common-5.5.0.jar;"%REPO%"\org\apache\lucene\lucene-core\5.5.0\lucene-core-5.5.0.jar;"%REPO%"\org\apache\lucene\lucene-queryparser\5.5.0\lucene-queryparser-5.5.0.jar;"%REPO%"\org\apache\lucene\lucene-codecs\5.5.0\lucene-codecs-5.5.0.jar;"%REPO%"\org\neo4j\neo4j-graph-algo\3.0.4\neo4j-graph-algo-3.0.4.jar;"%REPO%"\org\neo4j\neo4j-udc\3.0.4\neo4j-udc-3.0.4.jar;"%REPO%"\org\neo4j\neo4j-graph-matching\3.0.4\neo4j-graph-matching-3.0.4.jar;"%REPO%"\org\neo4j\neo4j-cypher\3.0.4\neo4j-cypher-3.0.4.jar;"%REPO%"\org\scala-lang\scala-library\2.11.8\scala-library-2.11.8.jar;"%REPO%"\org\scala-lang\scala-reflect\2.11.8\scala-reflect-2.11.8.jar;"%REPO%"\org\neo4j\neo4j-codegen\3.0.4\neo4j-codegen-3.0.4.jar;"%REPO%"\org\ow2\asm\asm\5.0.2\asm-5.0.2.jar;"%REPO%"\org\neo4j\neo4j-cypher-compiler-2.3\2.3.5\neo4j-cypher-compiler-2.3-2.3.5.jar;"%REPO%"\org\neo4j\neo4j-cypher-frontend-2.3\2.3.5\neo4j-cypher-frontend-2.3-2.3.5.jar;"%REPO%"\org\neo4j\neo4j-cypher-compiler-3.0\3.0.4\neo4j-cypher-compiler-3.0-3.0.4.jar;"%REPO%"\org\neo4j\neo4j-cypher-frontend-3.0\3.0.4\neo4j-cypher-frontend-3.0-3.0.4.jar;"%REPO%"\org\parboiled\parboiled-scala_2.11\1.1.7\parboiled-scala_2.11-1.1.7.jar;"%REPO%"\org\parboiled\parboiled-core\1.1.7\parboiled-core-1.1.7.jar;"%REPO%"\net\sf\opencsv\opencsv\2.3\opencsv-2.3.jar;"%REPO%"\com\googlecode\concurrentlinkedhashmap\concurrentlinkedhashmap-lru\1.4.2\concurrentlinkedhashmap-lru-1.4.2.jar;"%REPO%"\org\neo4j\neo4j-jmx\3.0.4\neo4j-jmx-3.0.4.jar;"%REPO%"\org\neo4j\neo4j-consistency-check\3.0.4\neo4j-consistency-check-3.0.4.jar;"%REPO%"\org\neo4j\app\neo4j-server\3.0.4\neo4j-server-3.0.4.jar;"%REPO%"\org\neo4j\neo4j-dbms\3.0.4\neo4j-dbms-3.0.4.jar;"%REPO%"\org\neo4j\neo4j-command-line\3.0.4\neo4j-command-line-3.0.4.jar;"%REPO%"\org\neo4j\server-api\3.0.4\server-api-3.0.4.jar;"%REPO%"\org\neo4j\3rdparty\javax\ws\rs\jsr311-api\1.1.2.r612\jsr311-api-1.1.2.r612.jar;"%REPO%"\org\neo4j\neo4j-bolt\3.0.4\neo4j-bolt-3.0.4.jar;"%REPO%"\io\netty\netty-all\4.0.28.Final\netty-all-4.0.28.Final.jar;"%REPO%"\org\neo4j\client\neo4j-browser\1.1.8\neo4j-browser-1.1.8.jar;"%REPO%"\org\neo4j\neo4j-shell\3.0.4\neo4j-shell-3.0.4.jar;"%REPO%"\jline\jline\2.12\jline-2.12.jar;"%REPO%"\com\sun\jersey\jersey-server\1.19\jersey-server-1.19.jar;"%REPO%"\com\sun\jersey\jersey-servlet\1.19\jersey-servlet-1.19.jar;"%REPO%"\commons-configuration\commons-configuration\1.10\commons-configuration-1.10.jar;"%REPO%"\commons-lang\commons-lang\2.6\commons-lang-2.6.jar;"%REPO%"\commons-digester\commons-digester\2.1\commons-digester-2.1.jar;"%REPO%"\commons-beanutils\commons-beanutils\1.8.3\commons-beanutils-1.8.3.jar;"%REPO%"\commons-io\commons-io\2.4\commons-io-2.4.jar;"%REPO%"\org\codehaus\jackson\jackson-jaxrs\1.9.13\jackson-jaxrs-1.9.13.jar;"%REPO%"\org\codehaus\jackson\jackson-core-asl\1.9.13\jackson-core-asl-1.9.13.jar;"%REPO%"\org\codehaus\jackson\jackson-mapper-asl\1.9.13\jackson-mapper-asl-1.9.13.jar;"%REPO%"\org\mozilla\rhino\1.7R4\rhino-1.7R4.jar;"%REPO%"\org\bouncycastle\bcprov-jdk15on\1.53\bcprov-jdk15on-1.53.jar;"%REPO%"\org\bouncycastle\bcpkix-jdk15on\1.53\bcpkix-jdk15on-1.53.jar;"%REPO%"\com\sun\jersey\contribs\jersey-multipart\1.19\jersey-multipart-1.19.jar;"%REPO%"\org\jvnet\mimepull\mimepull\1.9.3\mimepull-1.9.3.jar;"%REPO%"\org\neo4j\neo4j-kernel\3.0.4\neo4j-kernel-3.0.4-tests.jar;"%REPO%"\org\neo4j\neo4j-graphdb-api\3.0.4\neo4j-graphdb-api-3.0.4.jar;"%REPO%"\org\neo4j\neo4j-resource\3.0.4\neo4j-resource-3.0.4.jar;"%REPO%"\org\neo4j\neo4j-common\3.0.4\neo4j-common-3.0.4.jar;"%REPO%"\org\neo4j\neo4j-collections\3.0.4\neo4j-collections-3.0.4.jar;"%REPO%"\org\neo4j\neo4j-primitive-collections\3.0.4\neo4j-primitive-collections-3.0.4.jar;"%REPO%"\org\neo4j\neo4j-io\3.0.4\neo4j-io-3.0.4.jar;"%REPO%"\org\neo4j\neo4j-csv\3.0.4\neo4j-csv-3.0.4.jar;"%REPO%"\org\neo4j\neo4j-logging\3.0.4\neo4j-logging-3.0.4.jar;"%REPO%"\org\neo4j\neo4j-security\3.0.4\neo4j-security-3.0.4.jar;"%REPO%"\org\neo4j\neo4j-lucene-upgrade\3.0.4\neo4j-lucene-upgrade-3.0.4.jar;"%REPO%"\org\apache\lucene\lucene-backward-codecs\5.5.0\lucene-backward-codecs-5.5.0.jar;"%REPO%"\org\neo4j\neo4j-io\3.0.4\neo4j-io-3.0.4-tests.jar;"%REPO%"\org\neo4j\neo4j-unsafe\3.0.4\neo4j-unsafe-3.0.4.jar;"%REPO%"\org\apache\commons\commons-lang3\3.3.2\commons-lang3-3.3.2.jar;"%REPO%"\com\sun\jersey\jersey-client\1.19\jersey-client-1.19.jar;"%REPO%"\com\sun\jersey\jersey-core\1.19\jersey-core-1.19.jar;"%REPO%"\org\neo4j\app\neo4j-server\3.0.4\neo4j-server-3.0.4-tests.jar;"%REPO%"\com\sparkjava\spark-core\2.5\spark-core-2.5.jar;"%REPO%"\org\eclipse\jetty\jetty-server\9.3.6.v20151106\jetty-server-9.3.6.v20151106.jar;"%REPO%"\javax\servlet\javax.servlet-api\3.1.0\javax.servlet-api-3.1.0.jar;"%REPO%"\org\eclipse\jetty\jetty-http\9.3.6.v20151106\jetty-http-9.3.6.v20151106.jar;"%REPO%"\org\eclipse\jetty\jetty-util\9.3.6.v20151106\jetty-util-9.3.6.v20151106.jar;"%REPO%"\org\eclipse\jetty\jetty-io\9.3.6.v20151106\jetty-io-9.3.6.v20151106.jar;"%REPO%"\org\eclipse\jetty\jetty-webapp\9.3.6.v20151106\jetty-webapp-9.3.6.v20151106.jar;"%REPO%"\org\eclipse\jetty\jetty-xml\9.3.6.v20151106\jetty-xml-9.3.6.v20151106.jar;"%REPO%"\org\eclipse\jetty\jetty-servlet\9.3.6.v20151106\jetty-servlet-9.3.6.v20151106.jar;"%REPO%"\org\eclipse\jetty\jetty-security\9.3.6.v20151106\jetty-security-9.3.6.v20151106.jar;"%REPO%"\org\eclipse\jetty\websocket\websocket-server\9.3.6.v20151106\websocket-server-9.3.6.v20151106.jar;"%REPO%"\org\eclipse\jetty\websocket\websocket-common\9.3.6.v20151106\websocket-common-9.3.6.v20151106.jar;"%REPO%"\org\eclipse\jetty\websocket\websocket-client\9.3.6.v20151106\websocket-client-9.3.6.v20151106.jar;"%REPO%"\org\eclipse\jetty\websocket\websocket-servlet\9.3.6.v20151106\websocket-servlet-9.3.6.v20151106.jar;"%REPO%"\org\eclipse\jetty\websocket\websocket-api\9.3.6.v20151106\websocket-api-9.3.6.v20151106.jar;"%REPO%"\com\google\code\gson\gson\2.2.4\gson-2.2.4.jar;"%REPO%"\org\slf4j\slf4j-api\1.7.21\slf4j-api-1.7.21.jar;"%REPO%"\ch\qos\logback\logback-classic\1.2.3\logback-classic-1.2.3.jar;"%REPO%"\ch\qos\logback\logback-core\1.2.3\logback-core-1.2.3.jar;"%REPO%"\org\neo4j\example\neo4j-movies\3.0-SNAPSHOT\neo4j-movies-3.0-SNAPSHOT.jar
set EXTRA_JVM_ARGUMENTS=-Xmx512m
goto endInit

@REM Reaching here means variables are defined and arguments have been captured
:endInit

%JAVACMD% %JAVA_OPTS% %EXTRA_JVM_ARGUMENTS% -classpath %CLASSPATH_PREFIX%;%CLASSPATH% -Dapp.name="webapp" -Dapp.repo="%REPO%" -Dbasedir="%BASEDIR%" web_service.backend.Server %CMD_LINE_ARGS%
if ERRORLEVEL 1 goto error
goto end

:error
if "%OS%"=="Windows_NT" @endlocal
set ERROR_CODE=1

:end
@REM set local scope for the variables with windows NT shell
if "%OS%"=="Windows_NT" goto endNT

@REM For old DOS remove the set variables from ENV - we assume they were not set
@REM before we started - at least we don't leave any baggage around
set CMD_LINE_ARGS=
goto postExec

:endNT
@endlocal

:postExec

if "%FORCE_EXIT_ON_ERROR%" == "on" (
  if %ERROR_CODE% NEQ 0 exit %ERROR_CODE%
)

exit /B %ERROR_CODE%
