
# I(Should)PlanAll : Projet de génie logiciel et gestion de projet (INFO-F-307)

Ce projet est le développement d'une application pour la gestion de projets sous Java. 

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

Pour ce projet nous avons du implémenter un système de création d'utilisateur avec la connexion qui va avec où chaque utilisateurs auraient accès à un menu pour créer et éditer des projets et des sous projets qui contiendraient un système de tâches,étiquettes,date de fin,... L'utilisateur aura le droit aussi de collaborer avec d'autres personnes sur un projet et pourra aussi export/import des projets.Un système de cloud a aussi dû être intégré. Et pour finir sur les choses à implémenter, il a aussi accès à des statistiques sur ses projets qu'il peut export. 
Pour ce faire nous avons dû diviser notre projet en packages tout en respectant le MVC(model view controller).

# Tests

Pour le moment vu que nous utilisons intellIJ les tests s'effectuent simplement avec un run. 

# Misc

## Développement
Durant l'itération 1 on a implémenté la connexion et l'enregistrement d'un utilisateur pour notre application, ce qui rassemble la database pour les utilisateurs et le premier jet de l'interface graphique. On a travaillé parallèlement sur tout ce qui comptait pour la gestion de projets, c'est-à-dire la première version de l'interface graphique pour les projets et les statistiques ainsi que la database pour les projets. On a ensuite tout lié, on a donc fait en sorte que l'interface soit fonctionnel avec la database.Juste après ça on a fait une refonte total de nos interfaces. Cependant durant cette itération on a à peine pu faire les statistiques suite à une mauvaise estimation du temps.

Pour l'itération 2 on a commencé par faire un gros "refactor" de notre code. On a changé la mise en forme de notre projet afin qu'il respecte le Model-View-Controller, on a par conséquent séparé notre code en plusieurs package et "divisé" le code pour séparer les vus des controllers. Par la suite, étant donné que nous devions rajouter de nouvelles fonctionnalités(gestion de collaborateurs pour un projet, tags colorés, service de cloud), on a commencé par mettre à jour le code des databases afin qu'elles contiennent les nouvelles informations à gérer. Puis nous avons travaillé en parallèle sur la mise à jour des interfaces des projets et des statistiques ainsi que sur la création du cloud. Pour l'interface des projets, un nouveau menu à part a été créé afin de gérer/créer les tags colorés pour ensuite les intégrer dans le menu des projets et de nouvelles fonctionnalités afin de gérer les collaborateurs ont été rajoutées dans l'interface. L'interface des statistiques a été modifiée en parallèle en rajoutant l'export, cependant l'apparence n'est surement pas défénitive. L'import et export des projets dans le menu des projets a été rajouté plus tard ce qui a pu débloquer l'équipe du cloud et leur a permi de faire l'interface pour le cloud.

## Screenshot
![signup](https://user-images.githubusercontent.com/33431271/112816753-03460200-9082-11eb-92de-fd46c727796f.PNG)
![login](https://user-images.githubusercontent.com/33431271/112816761-050fc580-9082-11eb-88ad-2a758668ddbd.PNG)
![PojectMenu](https://user-images.githubusercontent.com/33431271/112816747-00e3a800-9082-11eb-8c7d-c42aa456f146.PNG)
![ColorTags](https://user-images.githubusercontent.com/33431271/112816742-ff19e480-9081-11eb-8459-6f5111df2183.PNG)
![statistics](https://user-images.githubusercontent.com/33431271/112816728-fb865d80-9081-11eb-90f7-d6db66110d52.PNG)

## License
