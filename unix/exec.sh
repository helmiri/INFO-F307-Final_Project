#! /bin/bash
cd ..
if [ $(uname -s) == "Linux" ]; then
    os="linux"
    gtkVersion="-Djdk.gtk.version=2"
else
    os="mac"
    gtkVersion=""
fi

CLASS_PATH="./lib/sqlite/*:./lib/calendarfx/*:./lib/googledrive/*:./lib/dropbox/*:./lib/gson/*:./lib/jarchivelib/*:./lib/controlsfx/*:./lib/junit/*:./lib/junit/console/*"
if [ "$1" == "test" ]; then
  java -cp "$CLASS_PATH:./compile/test" org.junit.platform.console.ConsoleLauncher --scan-class-path --details verbose --details-theme ascii --reports-dir ./compile/test --exclude-engine junit-vintage
elif [ "$1" == "jar" ]; then
  java --module-path lib/javafx/"$os" --add-modules javafx.controls,javafx.fxml,javafx.media $gtkVersion -jar ./dist/"$2"/g06-"$2".jar
else
  java --module-path lib/javafx/"$os" --add-modules javafx.controls,javafx.fxml,javafx.media $gtkVersion -cp "$CLASS_PATH:./compile/build" be.ac.ulb.infof307.g06.Main
fi

