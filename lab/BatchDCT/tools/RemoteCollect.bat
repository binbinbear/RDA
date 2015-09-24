@ECHO off

IF "%1" == "" ECHO No server specified.& EXIT /B 1

ECHO Target system: %1, local dir: %2\%3, parallel: %4, log mode: %5

IF EXIST "\\%1\c$\Program Files\VMware\VMware View\Server\DCT\vdm-support.vbs" GOTO start_collect
IF EXIST "\\%1\c$\Program Files\VMware\VMware View\Agent\DCT\vdm-support.vbs" GOTO start_collect
IF EXIST "\\%1\c$\Program Files (x86)\VMware\VMware View Composer\svi-support.wsf" GOTO start_collect
IF EXIST "\\%1\c$\Program Files\VMware\VMware View Composer\svi-support.wsf" GOTO start_collect

ECHO Target server not accessible, or DCT not found: %1
ECHO "Target server not accessible, or DCT not found" > %2\%3\ERROR_%1.txt
GOTO finish

:start_collect

set REMOTE_WORK_DIR=\\%1\c$\BatchDCT\%3

IF EXIST %REMOTE_WORK_DIR% (rd /S /Q %REMOTE_WORK_DIR%)

md %REMOTE_WORK_DIR%

copy .\tools\CollectLog.bat %REMOTE_WORK_DIR%\CollectLog.bat

ECHO.
ECHO ------------------------------------------------------------
ECHO Generating remote log bundles...
ECHO This may take several minutes or even longer. Please wait...
ECHO ------------------------------------------------------------
ECHO.

.\tools\psexec.exe \\%1 -h -accepteula -w "%SystemDrive%\BatchDCT\%3" cmd.exe /C "%SystemDrive%\BatchDCT\%3\CollectLog.bat %3 %5 > RemoteLog_%1.txt"

copy %REMOTE_WORK_DIR%\*.zip %2\%3.
copy %REMOTE_WORK_DIR%\*.txt %2\%3.
copy %REMOTE_WORK_DIR%\*.log %2\%3.
xcopy %REMOTE_WORK_DIR%\quick %2\%3\%1 /s /h /e /k /c /i

IF EXIST %REMOTE_WORK_DIR% (rd /S /Q %REMOTE_WORK_DIR%)
IF EXIST \\%1\c$\BatchDCT (rd /Q \\%1\c$\BatchDCT)

:finish

del /Q %2\%3\WORKING_%1.txt

IF %4==1 exit

exit /B
