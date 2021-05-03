#! /bin/bash
cd ..

java --module-path lib/javafx/mac --add-modules javafx.controls,javafx.fxml -cp "./compile/build/:./lib/calendarfx/view-11.8.3.jar:./lib/googledrive/*:./lib/dropbox/dropbox-core-sdk-3.1.5.jar:./lib/dropbox/jackson-core-2.7.4.jar:./lib/gson/gson-2.8.6.jar:./lib/jarchivelib/jarchivelib-0.7.1-jar-with-dependencies.jar:./lib/controlsfx/controlsfx-11.1.0.jar:./lib/sqlite/sqlite-jdbc-3.34.0.jar" -jar ./dist/iteration-3/g06-iteration-3.jar