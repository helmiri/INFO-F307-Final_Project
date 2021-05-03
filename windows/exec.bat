@echo off

set classpath=.\lib\sqlite\*;.\lib\calendarfx\*;.\lib\googledrive\*;.\lib\dropbox\*;.\lib\gson\*;.\lib\jarchivelib\*;.\lib\controlsfx\*;.\lib\junit\*;.\lib\junit\console\*

if "%2" == "test" (
    %1  -cp "%classpath%;.\compile\test" org.junit.platform.console.ConsoleLauncher --scan-class-path --details verbose --details-theme ascii --reports-dir .\compile\test --exclude-engine junit-vintage
) else if "%2" == "jar" (
    %1 --module-path .\lib\javafx\windows\lib --add-modules javafx.controls,javafx.fxml -jar .\dist\%3\g06-%3.jar
) else (
    %1 --module-path .\lib\javafx\windows\lib --add-modules javafx.controls,javafx.fxml -cp "%classpath%;.\compile\build" be.ac.ulb.infof307.g06.Main
)
