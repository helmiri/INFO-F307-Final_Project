@echo off

cd ..

rmdir .\compile\build\be
mkdir compile
mkdir .\compile\build
mkdir .\compile\tests

cd .\src

dir /s/b *.java > ..\compile\build\sources.txt
cd ..
xcopy .\src .\compile\build /t/e

%1 -cp ".\lib\calendarfx\view-11.8.3.jar;.\lib\googledrive\*;.\lib\dropbox\dropbox-core-sdk-3.1.5.jar;.\lib\dropbox\jackson-core-2.7.4.jar;.\lib\gson\gson-2.8.6.jar;.\lib\jarchivelib\jarchivelib-0.7.1-jar-with-dependencies.jar;.\lib\controlsfx\controlsfx-11.1.0.jar" --module-path lib\javafx\windows\lib --add-modules javafx.controls,javafx.fxml @.\compile\build\sources.txt .\src\be\ac\ulb\infof307\g06\Main.java -d .\compile\build

copy .\src\be\ac\ulb\infof307\g06\views\*.fxml .\compile\build\be\ac\ulb\infof307\g06\views\
copy .\src\be\ac\ulb\infof307\g06\views\connectionViews\*.fxml .\compile\build\be\ac\ulb\infof307\g06\views\connectionViews\
copy .\src\be\ac\ulb\infof307\g06\views\projectViews\*.fxml .\compile\build\be\ac\ulb\infof307\g06\views\projectViews\
copy .\src\be\ac\ulb\infof307\g06\views\projectViews\*.css .\compile\build\be\ac\ulb\infof307\g06\views\projectViews\
copy .\src\be\ac\ulb\infof307\g06\views\projectViews\popups\*.fxml .\compile\build\be\ac\ulb\infof307\g06\views\projectViews\popups\
copy .\src\be\ac\ulb\infof307\g06\views\statisticsViews\*.fxml .\compile\build\be\ac\ulb\infof307\g06\views\statisticsViews\
copy .\src\be\ac\ulb\infof307\g06\icons\* .\compile\build\be\ac\ulb\infof307\g06\icons\
copy .\src\be\ac\ulb\infof307\g06\views\calendarViews\*.fxml .\compile\build\be\ac\ulb\infof307\g06\views\calendarViews\
copy .\src\be\ac\ulb\infof307\g06\views\mainMenuViews\*.fxml .\compile\build\be\ac\ulb\infof307\g06\views\mainMenuViews\
copy .\src\be\ac\ulb\infof307\g06\views\settingsViews\*.fxml .\compile\build\be\ac\ulb\infof307\g06\views\settingsViews\
copy .\src\be\ac\ulb\infof307\g06\models\cloudModels\DropBox\credentials.json .\compile\build\be\ac\ulb\infof307\g06\models\cloudModels\DropBox\credentials.json
copy .\src\be\ac\ulb\infof307\g06\models\cloudModels\GoogleDrive\credentials.json .\compile\build\be\ac\ulb\infof307\g06\models\cloudModels\GoogleDrive\credentials.json




