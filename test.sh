#!/bin/bash
echo "Compiling..."

# Find all .java files under TuringMachines and compile them to the 'out' directory
find TuringMachines -name "*.java" -print0 | xargs -0 javac -encoding UTF-8 -d out

if [ $? -ne 0 ]; then
    echo "Compilation failed."
    exit $?
fi

echo "Running."
echo
java -cp out TuringMachines.Test
