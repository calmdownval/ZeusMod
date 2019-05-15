#!/bin/bash
# builds the java package

# make sure we have a clear build directory
echo "> preparing build directory"
if [ -d build/ ]
then
	rm -rf build/*
else
	mkdir build
fi

# build the source
echo "> building sources"
IFS=$'\n' files=(`find src -name "*.java"`)
javac -Xlint -d build -cp spigot.jar "${files[@]}"

# create the package
echo "> creating package"
jar cvf ZeusMod.jar -C build . -C src config.yml -C src plugin.yml
