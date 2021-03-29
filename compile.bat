@echo off
rmdir .\compile\build\be
mkdir compile
mkdir .\compile\build
mkdir .\compile\tests

cd .\src

dir /s/b *.java > ..\compile\build\sources.txt
cd ..
xcopy .\src .\compile\build /t/e

%1 -cp .\lib\dropbox\dropbox-core-sdk-3.1.5.jar;.\lib\dropbox\jackson-core-2.7.4.jar;.\lib\gson\gson-2.8.6.jar;.\lib\jarchivelib\jarchivelib-0.7.1-jar-with-dependencies.jar;.\lib\controlsfx\controlsfx-11.1.0.jar --module-path lib\javafx\windows\lib --add-modules javafx.controls,javafx.fxml @.\compile\build\sources.txt .\src\be\ac\ulb\infof307\g06\Controllers\MainController.java -d .\compile\build

copy .\src\be\ac\ulb\infof307\g06\views\*.fxml .\compile\build\be\ac\ulb\infof307\g06\views\
copy .\src\be\ac\ulb\infof307\g06\views\ConnectionsViews\*.fxml .\compile\build\be\ac\ulb\infof307\g06\views\ConnectionsViews\
copy .\src\be\ac\ulb\infof307\g06\views\ProjectViews\*.fxml .\compile\build\be\ac\ulb\infof307\g06\views\ProjectViews\
copy .\src\be\ac\ulb\infof307\g06\views\StatisticsViews\*.fxml .\compile\build\be\ac\ulb\infof307\g06\views\StatisticsViews\
copy .\src\be\ac\ulb\infof307\g06\icons\* .\compile\build\be\ac\ulb\infof307\g06\icons\



