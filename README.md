# I(Should)PlanAll : Projet de génie logiciel et gestion de projet (INFO-F-307)

TO DO: Description du projet

# Utilisation

Les librairies utilisées pour ce projet sont: controlsfx, jarchivelib, gson, sqlite, javafx, 

## Compilation

Windows:

`$PATH_TO_JDK$\javac.exe --module-path lib/javafx --add-modules javafx.controls,javafx.fxml .\src\be\ac\ulb\infof307\g06\Main.java .\src\be\ac\ulb\infof307\g06\database\*.java .\src\be\ac\ulb\infof307\g06\JavaUI\sample\*.java -d .\out\production\2021-groupe-6\`

`copy .\src\be\ac\ulb\infof307\g06\JavaUI\sample\*.fxml .\out\production\2021-groupe-6\be\ac\ulb\infof307\g06\JavaUI\sample\ && Xcopy /E /I .\src\be\ac\ulb\infof307\g06\JavaUI\sample\icons\ .\out\production\2021-groupe-6\be\ac\ulb\infof307\g06\JavaUI\sample\`

## Démarrage

Windows:

``$PATH_TO_JDK$\java.exe --module-path lib/javafx --add-modules javafx.controls,javafx.fxml -classpath ".\out\production\2021-groupe-6;.\lib\junit\*.jar;.\lib\controlsfx\*.jar;.\lib\javafx\*.jar;.\lib\sqlite\sqlite-jdbc-3.34.0.jar" be.ac.ulb.infof307.g06.Main``

Fichier .jar:

``$PATH_TO_JDK$\java.exe --module-path lib/javafx --add-modules javafx.controls,javafx.fxml -classpath ".\out\production\2021-groupe-6;.\lib\junit\*.jar;.\lib\controlsfx\*.jar;.\lib\javafx\*.jar;.\lib\sqlite\sqlite-jdbc-3.34.0.jar" -jar .\dist\g06-iteration-1.jar``

# Configuration :

TO DO: Informations sur la configuration du programme

# Tests

TO DO: Informations sur la façon d'executer les tests

# Misc

## Développement

## Screenshot

## License
