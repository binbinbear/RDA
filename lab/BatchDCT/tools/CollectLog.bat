::------------------------------------------------------------------------
:: This BAT script runs on View components
:: KB: http://kb.vmware.com/selfservice/microsites/search.do?language=en_US&cmd=displayKC&externalId=1017939
::------------------------------------------------------------------------

@ECHO OFF

REM set /P INPUT=Delete all old log bundles and start DCT? (y/N): %=%
REM IF "%INPUT%"=="y" goto yes
REM IF "%INPUT%"=="Y" goto yes
REM EXIT
:yes

IF "%1" == "" (
	echo No ID specified.
	exit
)
IF "%2" == "" (
	echo No log mode specified.
	exit
)

set WORK_DIR="C:\BatchDCT\%1"

ECHO WORK_DIR=%WORK_DIR%
cd %WORK_DIR%

IF "%2"=="full" GOTO log_mode_full

::------------------------------------------------------------------------
:: Try collecting DCT logs, quick parts (e.g. latest access logs)
::------------------------------------------------------------------------
:log_mode_quick

SET log_dir=C:\ProgramData\VMware\VDM\logs
SET sub_dir=.\quick\.
md %sub_dir%

copy /Y %log_dir%\wsnm_starts.txt %sub_dir%
:st_1
FOR /F %%i IN ('DIR /A-D /B /O:-D %log_dir%\debug-*.txt') DO (
	copy /Y %log_dir%\%%i %sub_dir%
	GOTO st_2
)
:st_2
FOR /F %%i IN ('DIR /A-D /B /O:-D %log_dir%\log-*.txt') DO (
	copy /Y %log_dir%\%%i %sub_dir%
	GOTO st_3
)
:st_3
FOR /F %%i IN ('DIR /A-D /B /O:-D "%log_dir%\PCoIP Secure Gateway\SecurityGateway_*.log"') DO (
	copy /Y "%log_dir%\PCoIP Secure Gateway\%%i" %sub_dir%
	GOTO st_4
)
:st_4
FOR /F %%i IN ('DIR /A-D /B /O:-D "%log_dir%\*"') DO (
	copy /Y "%log_dir%\%%i" %sub_dir%
	GOTO st_5
)
:st_5
FOR /F %%i IN ('DIR /A-D /B /O:-D "%log_dir%\PCoIP Secure Gateway\*"') DO (
	copy /Y "%log_dir%\PCoIP Secure Gateway\%%i" %sub_dir%
	GOTO st_6
)
:st_6
FOR /F %%i IN ('DIR /A-D /B /O:-D "%log_dir%\Blast Secure Gateway\*"') DO (
	copy /Y "%log_dir%\Blast Secure Gateway\%%i" %sub_dir%
	GOTO st_7
)
:st_7
SET log_dir=C:\Program Files\VMware\VMware View\Server\broker\logs
FOR /F %%i IN ('DIR /A-D /B /O:-D "%log_dir%\catalina.*.log"') DO (
	copy /Y "%log_dir%\%%i" %sub_dir%
	GOTO st_10
)
:st_10
FOR /F %%i IN ('DIR /A-D /B /O:-D "%log_dir%\host-manager.*.log"') DO (
	copy /Y "%log_dir%\%%i" %sub_dir%
	GOTO st_11
)
:st_11
FOR /F %%i IN ('DIR /A-D /B /O:-D "%log_dir%\localhost.*.log"') DO (
	copy /Y "%log_dir%\%%i" %sub_dir%
	GOTO st_12
)
:st_12
FOR /F %%i IN ('DIR /A-D /B /O:-D "%log_dir%\*"') DO (
	copy /Y "%log_dir%\%%i" %sub_dir%
	GOTO st_19
)
:st_19
GOTO log_mode_common


:log_mode_full
::------------------------------------------------------------------------
:: Try collecting DCT log if possible (broker, security server)
::------------------------------------------------------------------------

set DCT_OUT=%USERPROFILE%\Desktop\vdm-sdct
set DCT_BIN="C:\Program Files\VMware\VMware View\Server\DCT\vdm-support.vbs"
IF EXIST %DCT_BIN% GOTO START_DCT
set DCT_BIN="C:\Program Files\VMware\VMware View\Agent\DCT\vdm-support.vbs"
IF EXIST %DCT_BIN% GOTO START_DCT

ECHO No DCT installation found (vdm-support.vbs). Skip collecting DCT log.
GOTO END_DCT

:START_DCT
IF EXIST %DCT_OUT% (rd /S /Q %DCT_OUT%)

(ECHO N& ECHO N& ECHO N) | C:\Windows\System32\cscript.exe //nologo %DCT_BIN%
MOVE /Y %DCT_OUT%\*.zip %WORK_DIR%\.

IF EXIST %DCT_OUT% rd /S /Q %DCT_OUT%
:END_DCT


:log_mode_common

::------------------------------------------------------------------------
:: Try collecting SVI log if possible (View composer)
::------------------------------------------------------------------------

set SVI_OUT=%USERPROFILE%\Desktop
set SVI_BIN="C:\Program Files (x86)\VMware\VMware View Composer\svi-support.wsf"
IF EXIST %SVI_BIN% GOTO START_SVI
set SVI_BIN="C:\Program Files\VMware\VMware View Composer\svi-support.wsf"
IF EXIST %SVI_BIN% GOTO START_SVI

echo No SVI installation found (svi-support.wsf). Skip collecting SVI log.
GOTO END_SVI

:START_SVI
C:\Windows\System32\cscript.exe //nologo %SVI_BIN% /zip
MOVE /Y %SVI_OUT%\viewcomposer-svi-support-*.zip %WORK_DIR%\.
REN %WORK_DIR%\viewcomposer-svi-support-*.zip %computername%-viewcomposer-svi-support-*.zip
:END_SVI


::------------------------------------------------------------------------
:: Try collecting View Persona Management logs
::------------------------------------------------------------------------

::xp
set TARGET_FILE="C:\All Users\Application Data\VMware\VDM\logs\VMWVvp.txt"
IF EXIST %TARGET_FILE% COPY /Y %TARGET_FILE% %WORK_DIR%\%computername%-VMWVvp_xp.txt

::Windows 7
set TARGET_FILE="C:\Program Data\VMware\VDM\logs\VMWVvp.txt"
IF EXIST %TARGET_FILE% copy /Y %TARGET_FILE% %WORK_DIR%\%computername%-VMWVvp.txt

echo Complete.

