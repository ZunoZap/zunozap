@echo off
set /p jarbuild=Build: 
jar cvfm ZunoZap-0.0.%jarbuild%.jar MANIFEST.MF *.class *.png
jar cvfm ZunoZap-latest.jar MANIFEST.MF *.class *.png
pause
