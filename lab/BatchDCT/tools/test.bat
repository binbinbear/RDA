@echo off

echo Target system: %1, local dir: %2\%3, parallel mode: %4

ping localhost -n 3 > nul

del /Q %2\%3\WORKING_%1.txt

if %4==1 exit

exit /B
