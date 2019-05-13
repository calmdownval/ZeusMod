del /q build\*
for /d %%x in (build\*) do @rd /s /q "%%x"

javac -d build -Xlint:deprecation -classpath spigot.jar src/fit/seems/mc/zeusmod/ZeusModPlugin.java src/fit/seems/mc/zeusmod/ItemInHandListener.java src/fit/seems/mc/zeusmod/SpeedPreset.java src/fit/seems/mc/zeusmod/ResetCommandExecutor.java src/fit/seems/mc/zeusmod/SpeedCommandExecutor.java
copy "src\config.yml" "build\config.yml"
copy "src\plugin.yml" "build\plugin.yml"

cd build
jar -cvf ZeusMod.jar config.yml plugin.yml fit

copy "ZeusMod.jar" "..\ZeusMod.jar"
PAUSE
