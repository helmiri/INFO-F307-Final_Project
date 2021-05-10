# I(Should)PlanAll : Projet de génie logiciel et gestion de projet (INFO-F-307)

Ce projet est le développement d'une application pour la gestion de projets sous Java.

# Utilisation

Ce projet a été réalisé sous Java 15.0.2

Dépendances:

- JavaFX 16
- DropBox API v2
- GoogleDrive API v3
- GSon 2.8.6
- Jarchive 0.7.1
- JUnit 5.4.2
- SQLite JDBC 3.34.0
- ControlsFX 11.1.0
- CalendarFX 11

## Compilation

Windows:

- Ouvrez une fenêtre cmd ou PowerShell dans le dossier ou le script compile.bat réside (dans le dossier windows) et exécutez la commande suivante :

`.\compile.bat PATH_TO_JDK_JAVAC.EXE`

où PATH_TO_JDK_JAVAC.EXE est le chemin vers javac.exe

MacOS et Linux:

-Ouvrez un terminal dans le dossier ou le script compile.sh réside (dans le dossier unix) et exécutez la commande suivante :

`./compile.sh`

## Démarrage

Windows:

- Ouvrez une fenêtre cmd ou PowerShell dans le dossier ou le script compile.bat réside et exécutez la commande suivante :

`.\exec.bat PATH_TO_JDK_JAVA.EXE`

où PATH_TO_JDK_JAVA.EXE vers java.exe

MacOS et Linux:

- Ouvrez un terminal dans le dossier ou le script exec.sh (dans le dossier unix) réside et exécutez la commande suivante :

`./exec.sh`

Fichier .jar:

Windows:

- Ouvrez une fenêtre cmd ou PowerShell dans le dossier ou le script exec.bat réside et exécutez la commande suivante :

`.\exec.bat PATH_TO_JDK_JAVA.EXE jar iteration-x`

où x est le numéro de l'itération

MacOS et Linux:
 
- Ouvrez une fenêtre cmd ou PowerShell dans le dossier ou le script exec.sh (dans le dossier unix) réside et exécutez la commande suivante :

`./exec.sh jar iteration-x`

où x est le numéro de l'itération

# Configuration :

Pour ce projet nous avons dû implémenter une application de gestion de projets. Après s'être connecté, l'utilisateur a
accès à un menu pour créer et éditer des projets et des sous projets qui contiennent un système de tâches, étiquettes,
date de fin, etc. L'utilisateur peut décider de collaborer avec d'autres personnes sur un projet, ainsi qu'en exporter
ou importer. Un système de "cloud" a aussi dû être intégré. En plus des fonctionnalités déjà présentes, l'utilisateur
peut aussi avoir accès à des statistiques sur ses projets qu'il a la possibilité d'exporter. Pour ce faire, nous avons
dû diviser notre projet en packages tout en respectant le MVC (model view controller).

# Tests

## Compilation

Windows:

- Ouvrez une fenêtre cmd ou PowerShell dans le dossier ou le script compile.bat réside et exécutez la commande suivante

`.\windows\compile.bat PATH_TO_JDK_JAVAC.EXE test`

où PATH_TO_JDK_JAVAC.EXE est le chemin vers javac.exe

MacOS et Linux:

- Ouvrez terminal dans le dossier ou le script compile.sh (dans le dossier unix) réside et exécutez la commande suivante : 

`./compile.sh test`

## Démarrage

Windows:

- Ouvrez une fenêtre cmd ou PowerShell dans le dossier ou le script compile.bat réside et exécutez la commande suivante

`.\windows\exec.bat PATH_TO_JDK_JAVA.EXE test`

où PATH_TO_JDK_JAVA.EXE vers java.exe

MacOS et Linux:

- Ouvrez terminal dans le dossier ou le script exec.sh réside (dans le dossier unix) et exécutez la commande suivante : 

`./exec.sh test`

# Misc

## Développement
Durant l'itération 1, on a implémenté la connexion et l'enregistrement d'un utilisateur pour notre application. Ceci rassemble la base de données pour les utilisateurs et le premier jet de l'interface graphique. On a travaillé parallèlement sur tout ce qui comptait pour la gestion de projets, c'est-à-dire la première version du visuel des projets et des statistiques ainsi que la base de données pour ceux-ci. Une fois l'interface basique et la base de données liées, on a fait une refonte totale du visuel. Cependant durant cette itération, on a à peine pu faire les statistiques suite à une mauvaise estimation du temps.

Pour l'itération 2, on a commencé par faire un gros "refactor" de notre code. On a changé la mise en forme de notre projet afin qu'il respecte le Model-View-Controller (MVC). On a par conséquent séparé notre code en plusieurs packages et "divisé" le code pour séparer les vues des contrôleurs. Par la suite, étant donné que nous devions rajouter de nouvelles fonctionnalités (gestion de collaborateurs pour un projet, tags colorés, service de cloud). On a commencé par mettre à jour le code des bases de données afin qu'elles contiennent les nouvelles informations à gérer. Puis nous avons travaillé en parallèle sur la mise à jour des interfaces des projets et des statistiques ainsi que sur la création du cloud. Pour l'interface des projets, un nouveau menu à part a été créé afin de gérer/créer les tags colorés, pour ensuite les intégrer dans le menu des projets, ainsi que de nouvelles fonctionnalités afin de gérer les collaborateurs ont été rajoutées dans l'interface. L'interface des statistiques a été modifiée en parallèle en rajoutant l'exportation, cependant l'apparence n'est sûrement pas définitive. L'importation et l'exportation des projets dans le menu ont été rajoutées plus tard ce qui a pu débloquer l'équipe du cloud et leur a permis de faire l'interface pour le cloud.

Pour l'itération 3, on a perfectionné la structure MVC du projet en ajoutant des "Listener" afin de diminuer le couplage dans les différents fichiers. Ensuite, on a cloturé le design de l'interface des statistiques dans le but que le visuel soit plus compréhensible pour l'utilisateur. De ce fait, on a maintenant une vue générale et une vue individuelle pour une analyse plus fine des statistiques. Pendant ce temps, on a également travaillé sur le calendrier sur base de la librairie "CalendarFX". Enfin, on a ajouté une fonctionnalité afin de télécharger les projets via "GoogleDrive" pour pouvoir satisfaire un plus grand public d'utilisateurs.

Lors de l'itération 4 nous avons dû intégrer à notre projet l'intégrité des données, un système de sécurité ainsi que des aides dans l'application. Nous avons commencé par régler les différentes "issues" qui nous avaient été pointées du doigt pour ensuite mieux attaqué les différentes choses à rajouter ou modifier.L'intégrité et la sécurité des données ont été faites en parallèle des aides. Pour les aides nous sommes partis sur des videos tutorielles qui sont plus simples que des captures d'écran. Nous avons aussi du rajouter la vue par mois dans le calendrier des projets.


## Screenshot
![logIn](https://user-images.githubusercontent.com/33431271/117679889-f5bb8600-b1b0-11eb-9a12-6d14594c1a76.PNG)
![image](https://user-images.githubusercontent.com/33431271/117679976-0835bf80-b1b1-11eb-8271-502c3415768a.png)
![ProjectManagement](https://user-images.githubusercontent.com/33431271/117680003-0e2ba080-b1b1-11eb-9c34-d79f86cc7c0f.PNG)
![weekCalendar](https://user-images.githubusercontent.com/33431271/117680029-1552ae80-b1b1-11eb-91c7-b74b38115418.PNG)
![monthCalendar](https://user-images.githubusercontent.com/33431271/117680048-18e63580-b1b1-11eb-9b1b-f96da5170da1.PNG)
![stats2](https://user-images.githubusercontent.com/33431271/117680062-1c79bc80-b1b1-11eb-8d4f-0786d73a9c1e.PNG)
![profil](https://user-images.githubusercontent.com/33431271/117680101-27cce800-b1b1-11eb-99c0-5ef70c449674.PNG)
![tags](https://user-images.githubusercontent.com/33431271/117680085-256a8e00-b1b1-11eb-8817-a255790e4d56.PNG)
![storage](https://user-images.githubusercontent.com/33431271/117680072-1f74ad00-b1b1-11eb-8a95-bc9e8a6fb08c.PNG)
![help](https://user-images.githubusercontent.com/33431271/117680080-23083400-b1b1-11eb-8c34-8bb1e6e39dc9.PNG)


## License
