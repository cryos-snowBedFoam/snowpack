rem Get and save the current path
rem UNIX: curPath=`pwd`
rem UNIX: echo Current path is:
rem UNIX: echo $curPath
rem not solved yet for DOS

rem %1 = Path of snowpack executable file
cd %1

rem Execution of the SNOWPACK model
echo SNOWPACK model is being executed...

rem UNIX: snowpack $curPath/SETUP/CONSTANTS_User.INI > ModelLogFile
snowpack ..\SN_GUI\SETUP\CONSTANTS_User.INI > ModelLogFile

echo Execution of SNOWPACK model finished.
