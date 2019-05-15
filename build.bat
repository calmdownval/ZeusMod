del /q build\*
for /d %%x in (build\*) do @rd /s /q "%%x"

javac -d build -Xlint:deprecation -classpath spigot.jar src/fit/seems/mc/zeusmod/ZeusModPlugin.java src/fit/seems/mc/zeusmod/ItemInHandListener.java src/fit/seems/mc/zeusmod/PlayerPreset.java src/fit/seems/mc/zeusmod/ResetCommandExecutor.java src/fit/seems/mc/zeusmod/SpeedCommandExecutor.java src/fit/seems/mc/zeusmod/HarmListener.java src/fit/seems/mc/zeusmod/InteractListener.java
jar cvf ZeusMod.jar -C build . -C src config.yml -C src plugin.yml

PAUSE
