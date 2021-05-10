@echo off

set classpath=".\lib\sqlite\*;.\lib\calendarfx\*;.\lib\googledrive\*;.\lib\dropbox\*;.\lib\gson\*;.\lib\jarchivelib\*;.\lib\controlsfx\*;.\lib\junit\*;.\lib\junit\console\*"

if "%2" == "test" (
    goto COMPILE_TEST
) else if "%1" == "clean" (
    goto CLEAN
)

:COMPILE_BUILD
@REM Clean target directory

call :rem_dir build

mkdir .\compile\build

@REM Find .java files

dir /s/b .\src\*.java > .\compile\build\sources.txt

@REM Setup directory structure

xcopy .\src .\compile\build /t/e

@REM Compile 

%1 -cp %classpath% --module-path lib\javafx\windows\lib --add-modules javafx.controls,javafx.fxml,javafx.media @.\compile\build\sources.txt .\src\be\ac\ulb\infof307\g06\Main.java -d .\compile\build

@REM Copy resources (icons, fxml, json)

copy .\src\be\ac\ulb\infof307\g06\views\connectionViews\*.fxml .\compile\build\be\ac\ulb\infof307\g06\views\connectionViews\ > Nul
copy .\src\be\ac\ulb\infof307\g06\views\projectViews\*.fxml .\compile\build\be\ac\ulb\infof307\g06\views\projectViews\ > Nul
copy .\src\be\ac\ulb\infof307\g06\views\projectViews\popups\*.fxml .\compile\build\be\ac\ulb\infof307\g06\views\projectViews\popups\ > Nul
copy .\src\be\ac\ulb\infof307\g06\views\statisticsViews\*.fxml .\compile\build\be\ac\ulb\infof307\g06\views\statisticsViews\ > Nul
copy .\src\be\ac\ulb\infof307\g06\resources\icons\* .\compile\build\be\ac\ulb\infof307\g06\resources\icons\ > Nul
copy .\src\be\ac\ulb\infof307\g06\resources\stylesheets\*.css .\compile\build\be\ac\ulb\infof307\g06\resources\stylesheets\ > Nul
copy .\src\be\ac\ulb\infof307\g06\resources\videos\* .\compile\build\be\ac\ulb\infof307\g06\resources\videos\ > Nul
copy .\src\be\ac\ulb\infof307\g06\views\calendarViews\*.fxml .\compile\build\be\ac\ulb\infof307\g06\views\calendarViews\ > Nul
copy .\src\be\ac\ulb\infof307\g06\views\mainMenuViews\*.fxml .\compile\build\be\ac\ulb\infof307\g06\views\mainMenuViews\ > Nul
copy .\src\be\ac\ulb\infof307\g06\views\settingsViews\*.fxml .\compile\build\be\ac\ulb\infof307\g06\views\settingsViews\ > Nul
copy .\src\be\ac\ulb\infof307\g06\views\settingsViews\helpViews\*.fxml .\compile\build\be\ac\ulb\infof307\g06\views\settingsViews\helpViews\ > Nul
copy .\src\be\ac\ulb\infof307\g06\models\cloudModels\DropBox\credentials.json .\compile\build\be\ac\ulb\infof307\g06\models\cloudModels\DropBox\credentials.json > Nul
copy .\src\be\ac\ulb\infof307\g06\models\cloudModels\GoogleDrive\credentials.json .\compile\build\be\ac\ulb\infof307\g06\models\cloudModels\GoogleDrive\credentials.json > Nul

exit /b 0

@REM Tests compilation

:COMPILE_TEST

@REM Clean target directory
call :rem_dir test

mkdir .\compile\test

@REM Find .java files
dir /s/b .\test\*.java > .\compile\test\sources.txt
dir /s/b .\src\be\ac\ulb\infof307\g06\models\*.java >> .\compile\test\sources.txt

@REM Compile 
%1 -cp %classpath% --module-path lib\javafx\windows\lib --add-modules javafx.controls,javafx.fxml @.\compile\test\sources.txt -d .\compile\test

exit /b 0

:CLEAN

call :rem_dir test
call :rem_dir build
if exist .\compile rmdir /q .\compile

exit /b %ERRORLEVEL%

:rem_dir
    if exist .\compile (
        if exist .\compile\%~1 (
            del /q/s .\compile\%~1 > NUL
            rmdir /q/s .\compile\%~1 > NUL
        )
    )