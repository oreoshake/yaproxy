if exist "%USERPROFILE%\YAP\.YAP_JVM.properties" (
	set /p jvmopts=< "%USERPROFILE%\YAP\.YAP_JVM.properties"
) else (
	set jvmopts=-Xmx512m
)

java %jvmopts% -jar @yapJar@ %*
