@echo off

%1 --module-path .\lib\javafx\windows\lib --add-modules javafx.controls,javafx.fxml -cp .\compile\build\;.\lib\dropbox\dropbox-core-sdk-3.1.5.jar;.\lib\dropbox\jackson-core-2.7.4.jar;.\lib\gson\gson-2.8.6.jar;.\lib\jarchivelib\jarchivelib-0.7.1-jar-with-dependencies.jar;.\lib\controlsfx\controlsfx-11.1.0.jar;.\lib\sqlite\sqlite-jdbc-3.34.0.jar -jar .\dist\iteration-2\g06-iteration-2.jar