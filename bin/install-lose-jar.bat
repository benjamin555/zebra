@echo off
echo.
echo install custom jar
echo.
rem pause
rem echo.

set MAVEN_OPTS=%MAVEN_OPTS% -Xms256m -Xmx512m -XX:PermSize=128m -XX:MaxPermSize=256m

call mvn install:install-file -Dfile=../lib/JNative.jar -DgroupId=org.xvolks -DartifactId=jnative -Dversion=1.6 -Dpackaging=jar

pause