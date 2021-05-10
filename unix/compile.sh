#! /bin/bash

if [ $(uname -s) == "Linux" ]; then
    os="linux"
else
    os="mac"
fi

cd ..

CLASS_PATH="./lib/sqlite/sqlite-jdbc-3.34.0.jar:./lib/calendarfx/*:./lib/googledrive/*:./lib/dropbox/*:./lib/gson/*:./lib/jarchivelib/*:./lib/controlsfx/*:./lib/junit/*:./lib/junit/console/*"


function get_files(){
  for file in "$1"/* ;do
    if [ -f "${file}" ] ;then
      local name=$(basename "$file")
      local extension="${name##*.}"
      if [ "$extension" == "java" ];then
        echo "$file" >> "$2"
      fi
    fi
    [ -d "${file}" ] && get_files "$file" "$2"
  done
}

function make_tests(){

  local SOURCE_FILE_LOCAL_PATH="compile/test/sources.txt"
  local SOURCE_FILE="$PWD/$SOURCE_FILE_LOCAL_PATH"

  > "$SOURCE_FILE"

  local MAIN_LOCATION="$PWD"
  rm -rf ./compile/test
  mkdir -p ./compile/test

  cd ./test
  get_files "$PWD" "$SOURCE_FILE"

  cd ..


  cd ./src/be/ac/ulb/infof307/g06/models

  get_files "$PWD" "$SOURCE_FILE"

  cd "$MAIN_LOCATION"


  javac -cp "$CLASS_PATH" --module-path lib/javafx/"$os" --add-modules javafx.controls,javafx.fxml,javafx.media @./compile/test/sources.txt -d ./compile/test

}

function compile_program(){

  local SOURCE_FILE_LOCAL_PATH="compile/build/sources.txt"
  local SOURCE_FILE="$PWD/$SOURCE_FILE_LOCAL_PATH"

  > "$SOURCE_FILE"

  rm -rf ./compile/build/be

  mkdir -p ./compile/build


  cd ./src
  get_files "$PWD" "$SOURCE_FILE"
  cd ..
  rsync -a -f"+ */" -f"- *" src/ compile/build/



  javac -cp "$CLASS_PATH" --module-path lib/javafx/"$os" --add-modules javafx.controls,javafx.fxml,javafx.media @./compile/build/sources.txt ./src/be/ac/ulb/infof307/g06/Main.java -d ./compile/build

  cp ./src/be/ac/ulb/infof307/g06/views/connectionViews/*.fxml ./compile/build/be/ac/ulb/infof307/g06/views/connectionViews/ 
  cp ./src/be/ac/ulb/infof307/g06/views/projectViews/*.fxml ./compile/build/be/ac/ulb/infof307/g06/views/projectViews/ 
  cp ./src/be/ac/ulb/infof307/g06/views/projectViews/popups/*.fxml ./compile/build/be/ac/ulb/infof307/g06/views/projectViews/popups/ 
  cp ./src/be/ac/ulb/infof307/g06/views/statisticsViews/*.fxml ./compile/build/be/ac/ulb/infof307/g06/views/statisticsViews/ 
  cp ./src/be/ac/ulb/infof307/g06/resources/icons/* ./compile/build/be/ac/ulb/infof307/g06/resources/icons/ 
  cp ./src/be/ac/ulb/infof307/g06/resources/stylesheets/*.css ./compile/build/be/ac/ulb/infof307/g06/resources/stylesheets/ 
  cp ./src/be/ac/ulb/infof307/g06/resources/videos/* ./compile/build/be/ac/ulb/infof307/g06/resources/videos/ 
  cp ./src/be/ac/ulb/infof307/g06/views/calendarViews/*.fxml ./compile/build/be/ac/ulb/infof307/g06/views/calendarViews/ 
  cp ./src/be/ac/ulb/infof307/g06/views/mainMenuViews/*.fxml ./compile/build/be/ac/ulb/infof307/g06/views/mainMenuViews/ 
  cp ./src/be/ac/ulb/infof307/g06/views/settingsViews/*.fxml ./compile/build/be/ac/ulb/infof307/g06/views/settingsViews/ 
  cp ./src/be/ac/ulb/infof307/g06/views/settingsViews/helpViews/*.fxml ./compile/build/be/ac/ulb/infof307/g06/views/settingsViews/helpViews/ 
  cp ./src/be/ac/ulb/infof307/g06/models/cloudModels/DropBox/credentials.json ./compile/build/be/ac/ulb/infof307/g06/models/cloudModels/DropBox/credentials.json 
  cp ./src/be/ac/ulb/infof307/g06/models/cloudModels/GoogleDrive/credentials.json ./compile/build/be/ac/ulb/infof307/g06/models/cloudModels/GoogleDrive/credentials.json 

}

if [ "$1" == "test" ]; then
  make_tests
else
  compile_program
fi



