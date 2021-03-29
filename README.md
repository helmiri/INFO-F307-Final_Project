# I(Should)PlanAll : Projet de génie logiciel et gestion de projet (INFO-F-307)

Ce projet est le dévellopement d'une application pour la gestion de projets sous Java. 

# Utilisation

Ce projet a été réalisé sous Java 15.0.2 avec comme librairies exterieures utilisées: controlsfx, jarchivelib, gson, sqlite, javafx.

## Compilation

Windows:

`$PATH_TO_JDK$\javac.exe --module-path lib/javafx --add-modules javafx.controls,javafx.fxml .\src\be\ac\ulb\infof307\g06\Main.java .\src\be\ac\ulb\infof307\g06\database\*.java .\src\be\ac\ulb\infof307\g06\JavaUI\sample\*.java -d .\out\production\2021-groupe-6\`

`copy .\src\be\ac\ulb\infof307\g06\JavaUI\sample\*.fxml .\out\production\2021-groupe-6\be\ac\ulb\infof307\g06\JavaUI\sample\ && Xcopy /E /I .\src\be\ac\ulb\infof307\g06\JavaUI\sample\icons\ .\out\production\2021-groupe-6\be\ac\ulb\infof307\g06\JavaUI\sample\`

## Démarrage

Windows:

``$PATH_TO_JDK$\java.exe --module-path lib/javafx --add-modules javafx.controls,javafx.fxml -classpath ".\out\production\2021-groupe-6;.\lib\junit\*.jar;.\lib\controlsfx\*.jar;.\lib\javafx\*.jar;.\lib\sqlite\sqlite-jdbc-3.34.0.jar" be.ac.ulb.infof307.g06.Main``

Fichier .jar:

``$PATH_TO_JDK$\java.exe --module-path lib/javafx --add-modules javafx.controls,javafx.fxml -classpath ".\out\production\2021-groupe-6;.\lib\junit\*.jar;.\li![login](https://user-images.githubusercontent.com/33431271/112816355-992d5d00-9081-11eb-8d0d-b99bbde11276.PNG)
b\controlsfx\*.jar;.\lib\javafx\*.jar;.\lib\sqlite\sqlite-jdbc-3.34.0.jar" -jar .\dist\g06-iteration-1.jar``

# Configuration :

Pour ce projet nous avons du implémenter un système de création d'utilisateur avec la connection qui va avec où chaque utilisateurs auraient accès à un menu pour créer et éditer des projets et des sous projets qui contiendraient un système de tâches,étiquettes,date de fin,... L'utilisateur aura le droit aussi de collaborer avec d'autres personnes sur un projet et pourra aussi export/import des projets.Un système de cloud a aussi dû être intégré. Et pour finir sur les choses à implémenter, il a aussi accès à des statistiques sur ses projets qu'il peut export. 
Pour ce faire nous avons dû diviser notre projet en packages tout en respectant le MVC(model view controller).

# Tests

TO DO: Informations sur la façon d'executer les tests

# Misc

## Développement

## Screenshot

## License
