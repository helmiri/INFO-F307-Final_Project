#! /bin/bash



cd ..

SOURCE_FILE_LOCAL_PATH="compile/build/sources.txt"
SOURCE_FILE="$PWD/$SOURCE_FILE_LOCAL_PATH"

> "$SOURCE_FILE"

function get_files(){
  for file in "$1"/* ;do
    if [ -f "${file}" ] ;then
      local name=$(basename "$file")
      local extension="${name##*.}"
      if [ "$extension" == "java" ];then
        echo "$file" >> "$SOURCE_FILE"
      fi
    fi
    [ -d "${file}" ] && get_files "$file"
  done
}

rm -rf ./compile/build/be

mkdir -p compile
mkdir -p ./compile/build
mkdir -p ./compile/tests

cd ./src
get_files "$PWD"
cd ..
rsync -a -f"+ */" -f"- *" src/ compile/build/


javac -cp "./lib/calendarfx/view-11.8.3.jar:./lib/googledrive/*:./lib/dropbox/dropbox-core-sdk-3.1.5.jar:./lib/dropbox/jackson-core-2.7.4.jar:./lib/gson/gson-2.8.6.jar:./lib/jarchivelib/jarchivelib-0.7.1-jar-with-dependencies.jar:./lib/controlsfx/controlsfx-11.1.0.jar:" --module-path lib/javafx/linux --add-modules javafx.controls,javafx.fxml @./compile/build/sources.txt ./src/be/ac/ulb/infof307/g06/Main.java -d ./compile/build

cp ./src/be/ac/ulb/infof307/g06/views/connectionViews/*.fxml ./compile/build/be/ac/ulb/infof307/g06/views/connectionViews/
cp ./src/be/ac/ulb/infof307/g06/views/projectViews/*.fxml ./compile/build/be/ac/ulb/infof307/g06/views/projectViews/
cp ./src/be/ac/ulb/infof307/g06/resources/stylesheets/*.css ./compile/build/be/ac/ulb/infof307/g06/resources/stylesheets/
cp ./src/be/ac/ulb/infof307/g06/views/projectViews/popups/*.fxml ./compile/build/be/ac/ulb/infof307/g06/views/projectViews/popups/
cp ./src/be/ac/ulb/infof307/g06/views/statisticsViews/*.fxml ./compile/build/be/ac/ulb/infof307/g06/views/statisticsViews/
cp ./src/be/ac/ulb/infof307/g06/resources/icons/*.png ./compile/build/be/ac/ulb/infof307/g06/resources/icons/
cp ./src/be/ac/ulb/infof307/g06/views/calendarViews/*.fxml ./compile/build/be/ac/ulb/infof307/g06/views/calendarViews/
cp ./src/be/ac/ulb/infof307/g06/views/mainMenuViews/*.fxml ./compile/build/be/ac/ulb/infof307/g06/views/mainMenuViews/
cp ./src/be/ac/ulb/infof307/g06/views/settingsViews/*.fxml ./compile/build/be/ac/ulb/infof307/g06/views/settingsViews/
cp ./src/be/ac/ulb/infof307/g06/models/cloudModels/DropBox/credentials.json ./compile/build/be/ac/ulb/infof307/g06/models/cloudModels/DropBox/credentials.json
cp ./src/be/ac/ulb/infof307/g06/models/cloudModels/GoogleDrive/credentials.json ./compile/build/be/ac/ulb/infof307/g06/models/cloudModels/GoogleDrive/credentials.json




