:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
::
:: BatchDCT - v0.1
::
:: [Purpose]
::   * Generate and collect DCT/SVI logs on multiple Horizon View
::     components, remotely, batchly, and in parallel.
::
::     View components:
::         View Connection Server (DCT log)
::         View Security Server (DCT log)
::         View Transfer Server (DCT log)
::         View Agent (DCT log, agent)
::         View Composer Server (SVI log)
::
:: [Features]
::   * Support parallel execution mode, and sequential execution mode
::   * Configurable and fully automated. So it can be used as scheduled task.
::   * Delete old logs, according to setting, to maintain log size.
::
:: [Requirements]
::   1. Download PsExec.exe from Microsoft, and put it in the "tools" directory.
::   2. All systems must be in the same or trusted domain.
::   3. Current user is domain profile and is system admin.
::   4. File sharing is opened on each server.
::   5. Windows7, Windows server 2008 R2
::   6. Horizon View 6. 
::
:: [Usage]
::   1. Edit configuration in "config.txt", including:
::       Target systems to collect logs from
::       Destination folder to store collected log archive
::       (optional) max logs to keep
::       (optional) parallel or not
::   2. Run command "BatchDCT.bat full" or "BatchDCT.bat quick".
::       The "full" mode: collects the entire DCT logs, which can be big and 
::         the process takes a long time to complete. Note that
::         the logging level depends on the current DCT logging level,
::         BatchDCT will not change the logging level configuration on target servers.
::       The "quick" mode: collects only most recent log files.
::      
::   For example, you can set:
::       MAX_FULL_LOGS_TO_KEEP = 2
::       MAX_QUICK_LOGS_TO_KEEP = 7
::   and create two scheduled tasks (e.g. in Windows Task Scheduler):
::       a). repeat every week, command: "BatchDCT.bat full"
::       b). repeat every day, command: "BatchDCT.bat quick"
::
::   If an issue happens and the logs are needed, upload at least one full log 
::   bundle (<date>_full.zip), together with all later quick log archives (<date>_quick.zip).
::
::
:: [Known issue and noted behavior]
::   1. On each target view component server to collect DCT from, the folder
::          <user_desktop>/vdm-sdct
::      will be cleared. This folder created by DCT tool, used as output.
::      This BatchDCT tool will delete that folder each time it runs.
::
:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
::
:: EUC Tech Enablement Team, nanw@vmware.com
::
:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
