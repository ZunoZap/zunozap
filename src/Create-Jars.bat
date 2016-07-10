@echo off
set /p jarbuild=Build: 
REM jar cvfm ZunoZap-0.0.1-SNAPSHOT-0%jarbuild%.jar MANIFEST.MF ZunoZap*.class
jar cvfm ZunoZap-0.0.%jarbuild%.jar MANIFEST.MF *.class *.png
jar cvfm ZunoZap-latest.jar MANIFEST.MF *.class *.png
pause
