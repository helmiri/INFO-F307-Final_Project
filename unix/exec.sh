#! /bin/bash
cd ..

CLASS_PATH="./lib/sqlite/*:./lib/calendarfx/*:./lib/googledrive/*:./lib/dropbox/*:./lib/gson/*:./lib/jarchivelib/*:./lib/controlsfx/*:./lib/junit/*:./lib/junit/console/*"
if [ "$2" == "test" ]; then
  java -cp "$CLASS_PATH:./compile/test" org.junit.platform.console.ConsoleLauncher --scan-class-path --details verbose --details-theme ascii --reports-dir ./compile/test --exclude-engine junit-vintage
elif [ "$2" == "jar" ]; then
  java --module-path lib/javafx/"$1" --add-modules javafx.controls,javafx.fxml -jar ./dist/"$2"/g06-"$2".jar
else
  java --module-path lib/javafx/"$1" --add-modules javafx.controls,javafx.fxml -cp "$CLASS_PATH:./compile/build" be.ac.ulb.infof307.g06.Main
fi
