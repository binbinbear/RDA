:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
::
:: BatchDCT - v0.1
::
:: Refer to README.txt for details.
::
:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::

@ECHO off
@ECHO BatchDCT - v0.1

SET log_mode=""
IF "%1"=="full" SET log_mode=full
IF "%1"=="FULL" SET log_mode=full
IF "%1"=="quick" SET log_mode=quick
IF "%1"=="QUICK" SET log_mode=quick
IF %log_mode%=="" ECHO "Usage: BatchDCT.bat [full|quick]"& EXIT /B

::--------------------------------------------------------------------------
:: Check tools 
::--------------------------------------------------------------------------
IF NOT EXIST .\tools\PsExec.exe GOTO missing_psexec
IF NOT EXIST .\tools\7za.exe GOTO missing_7za
GOTO set_config

:missing_psexec
ECHO You need to download PsExec.exe, and put it in the "tools" directory in this tool.
EXIT /B

:missing_7za
ECHO You need to download 7za.exe, and put it in the "tools" directory in this tool.
EXIT /B

:set_config
::--------------------------------------------------------------------------
:: This is the default setting. Change the actual setting in config.txt
::--------------------------------------------------------------------------
SET output_dir=	".\collected logs"
SET max_full_logs_to_keep = 7
SET max_quick_logs_to_keep = 7
SET parallel_mode=1
REM SET task_cmd=test.bat
SET task_cmd=RemoteCollect.bat

SET starttime=%TIME: =0%

::--------------------------------------------------------------------------
:: Read config
::--------------------------------------------------------------------------
setlocal enableextensions enabledelayedexpansion

ECHO Prepare environment...
FOR /F "eol=# delims=: tokens=1, 2" %%i IN (config.txt) DO (
	IF "%%i"=="MAX_FULL_LOGS_TO_KEEP" (
		SET max_full_logs_to_keep=%%j
	)
	
	IF "%%i"=="MAX_QUICK_LOGS_TO_KEEP" (
		SET max_quick_logs_to_keep=%%j
	)
	
	IF "%%i"=="OUTPUT_DIR" (
		SET output_dir=%%j
	)
	
	IF "%%i"=="PARALLEL_MODE" (
		SET parallel_mode=%%j
	)
)

ECHO   MAX_FULL_LOGS_TO_KEEP = %max_full_logs_to_keep%
ECHO   MAX_QUICK_LOGS_TO_KEEP = %max_quick_logs_to_keep%
ECHO   OUTPUT_DIR = %output_dir%
ECHO   PARALLEL_MODE = %parallel_mode%

::--------------------------------------------------------------------------
:: Maintain log size
::--------------------------------------------------------------------------
FOR /F %%i IN ('DIR /AD /B %output_dir%\*') DO (
	REM ECHO deleting %output_dir%\%%i
	RD /Q /S %output_dir%\%%i
)
:: ensure size of full logs
SET /A counter=0
FOR /F %%i IN ('DIR /A-D /B /O:-D %output_dir%\*_full.zip') DO (
	SET /A counter+=1
	IF NOT !counter! LSS %max_full_logs_to_keep% (
		REM ECHO   Delete old log: %%i
		DEL /Q %output_dir%\%%i
	)
)
:: ensure size of quick logs
SET /A counter=0
FOR /F %%i IN ('DIR /A-D /B /O:-D %output_dir%\*_quick.zip') DO (
	SET /A counter+=1
	IF NOT !counter! LSS %max_quick_logs_to_keep% (
		REM ECHO   Delete old log: %%i
		DEL /Q %output_dir%\%%i
	)
)

:: Create working directory
FOR /f "skip=1 tokens=1-6 delims= " %%a IN ('wmic path Win32_LocalTime Get Day^,Hour^,Minute^,Month^,Second^,Year /Format:table') do (
	IF NOT "%%~f"=="" (
		SET /a FormattedDate=10000 * %%f + 100 * %%d + %%a
		SET FormattedDate=!FormattedDate:~-4,2!_!FormattedDate:~-2,2!_!FormattedDate:~-6,2!
	)
)	
SET DATEST=%FormattedDate%-%TIME:~0,2%_%TIME:~3,2%_%TIME:~6,2%
SET DATEST=%DATEST: =0%
SET WORK_DIR=%output_dir%\%DATEST%

ECHO   Work dir: %WORK_DIR%
mkdir %WORK_DIR%

::--------------------------------------------------------------------------
:: Spawn a task for each server. Generate the DCT log archive and download it
::--------------------------------------------------------------------------
ECHO Spawning sub tasks...
SET /A total_tasks=0
FOR /F "eol=# tokens=1,2 delims=:" %%i IN (config.txt) DO (
	IF "%%j" EQU "" (
		ECHO   Server: %%i
		ECHO "" > %WORK_DIR%\WORKING_%%i.txt

		IF %parallel_mode%==1 (
			start .\tools\%task_cmd% %%i %output_dir% %DATEST% %parallel_mode% %log_mode%
			SET /A total_tasks+=1
		) ELSE (
			CALL .\tools\%task_cmd% %%i %output_dir% %DATEST% %parallel_mode% %log_mode%
		)
	)
)


IF %parallel_mode%==0 GOTO finish

::--------------------------------------------------------------------------
:: Wait until all tasks are done
::--------------------------------------------------------------------------
:wait
	ping 127.0.0.1 -n 20 > nul

	::IF NOT EXIST %WORK_DIR%\WORKING_*.txt GOTO finish
	SET count=0
	FOR %%a IN (%WORK_DIR%\WORKING_*.txt) DO SET /a count+=1
	IF %count%==0 GOTO finish

	SET /A completed_task=%total_tasks% - %count%
	ECHO %completed_task%/%total_tasks%...(%time%)
GOTO wait

:finish

::--------------------------------------------------------------------------
:: Archive
::--------------------------------------------------------------------------
cd %output_dir%
:: For full mode, the archives are already compressed. We use level 0 to just archive them.
:: for quick mode, the files are text log files. Archive them with high compression level.
SET compression_level=0
IF "%log_mode%"=="quick" SET compression_level=7
..\tools\7za.exe a -tzip -y -mx%compression_level% %DATEST%_%log_mode% .\%DATEST%\*
rd /S /Q %DATEST%
cd ..

::--------------------------------------------------------------------------


SET endtime=%TIME: =0%
:: convert starttime and endtime to centiseconds
SET /A starttime=(1%starttime:~0,2%-100)*360000 + (1%starttime:~3,2%-100)*6000 + (1%starttime:~6,2%-100)*100 + (1%starttime:~9,2%-100)
SET /A endtime=(1%endtime:~0,2%-100)*360000 + (1%endtime:~3,2%-100)*6000 + (1%endtime:~6,2%-100)*100 + (1%endtime:~9,2%-100)

:: calculating the duratyion
SET /A duration=%endtime%-%starttime%

:: we might have measured the time inbetween days
IF %endtime% LSS %starttime% SET SET /A duration=%starttime%-%endtime%

:: now break the centiseconds down to hors, minutes, seconds and the remaining centiseconds
SET /A durationh=%duration% / 360000
SET /A durationm=(%duration% - %durationh%*360000) / 6000
SET /A durations=(%duration% - %durationh%*360000 - %durationm%*6000) / 100
SET /A durationhs=(%duration% - %durationh%*360000 - %durationm%*6000 - %durations%*100)

:: some formatting
IF %durationh% LSS 10 SET durationh=0%durationh%
IF %durationm% LSS 10 SET durationm=0%durationm%
IF %durations% LSS 10 SET durations=0%durations%
IF %durationhs% LSS 10 SET durationhs=0%durationhs%

:: outputing

ECHO.
ECHO ---------------------------------------------
ECHO Logs: %WORK_DIR%
ECHO Duration: %durationh%:%durationm%:%durations%
ECHO Complete.
ECHO ---------------------------------------------
ECHO.
