# I(Should)PlanAll : Projet de génie logiciel et gestion de projet (INFO-F-307)

Ce projet est le développement d'une application pour la gestion de projets sous Java.

# Utilisation

Ce projet a été réalisé sous Java 15.0.2

Dépendances:

- JavaFX 16
- DropBox API v2
- GSon 2.8.6
- Jarchive 0.7.1
- JUnit 5.4.2
- SQLite JDBC 3.34.0
- ControlsFX 11.1.0
- CalendarFX 11

## Compilation

Windows:

- Ouvrez une fenêtre cmd ou PowerShell dans le dossier ou le script compile.bat réside et exécutez la commande suivante

`.\compile.bat PATH_TO_JDK_JAVAC.EXE`

où PATH_TO_JDK_JAVAC.EXE est le chemin vers javac.exe

## Démarrage

Windows:

- Ouvrez une fenêtre cmd ou PowerShell dans le dossier ou le script compile.bat réside et exécutez la commande suivante

`.\exec.bat PATH_TO_JDK_JAVAC.EXE`

où PATH_TO_JDK_JAVA.EXE vers java.exe

Fichier .jar:

- Ouvrez une fenêtre cmd ou PowerShell dans le dossier ou le script exec_jar.bat réside et exécutez la commande suivante

`.\compile.bat PATH_TO_JDK_JAVA.EXE`

# Configuration :

Pour ce projet nous avons dû implémenter une application de gestion de projets. Après s'être connecté, l'utilisateur a
accès à un menu pour créer et éditer des projets et des sous projets qui contiennent un système de tâches, étiquettes,
date de fin, etc. L'utilisateur peut décider de collaborer avec d'autres personnes sur un projet, ainsi qu'en exporter
ou importer. Un système de "cloud" a aussi dû être intégré. En plus des fonctionnalités déjà présentes, l'utilisateur
peut aussi avoir accès à des statistiques sur ses projets qu'il a la possibilité d'exporter. Pour ce faire, nous avons
dû diviser notre projet en packages tout en respectant le MVC (model view controller).

# Tests

Pour le moment vu que nous utilisons intellIJ les tests s'effectuent simplement avec un run.

# Misc

## Développement
Durant l'itération 1, on a implémenté la connexion et l'enregistrement d'un utilisateur pour notre application. Ceci rassemble la base de données pour les utilisateurs et le premier jet de l'interface graphique. On a travaillé parallèlement sur tout ce qui comptait pour la gestion de projets, c'est-à-dire la première version du visuel des projets et des statistiques ainsi que la base de données pour ceux-ci. Une fois l'interface basique et la base de données liées, on a fait une refonte totale du visuel. Cependant durant cette itération, on a à peine pu faire les statistiques suite à une mauvaise estimation du temps.

Pour l'itération 2, on a commencé par faire un gros "refactor" de notre code. On a changé la mise en forme de notre projet afin qu'il respecte le Model-View-Controller (MVC). On a par conséquent séparé notre code en plusieurs packages et "divisé" le code pour séparer les vues des contrôleurs. Par la suite, étant donné que nous devions rajouter de nouvelles fonctionnalités (gestion de collaborateurs pour un projet, tags colorés, service de cloud). On a commencé par mettre à jour le code des bases de données afin qu'elles contiennent les nouvelles informations à gérer. Puis nous avons travaillé en parallèle sur la mise à jour des interfaces des projets et des statistiques ainsi que sur la création du cloud. Pour l'interface des projets, un nouveau menu à part a été créé afin de gérer/créer les tags colorés, pour ensuite les intégrer dans le menu des projets, ainsi que de nouvelles fonctionnalités afin de gérer les collaborateurs ont été rajoutées dans l'interface. L'interface des statistiques a été modifiée en parallèle en rajoutant l'exportation, cependant l'apparence n'est sûrement pas définitive. L'importation et l'exportation des projets dans le menu ont été rajoutées plus tard ce qui a pu débloquer l'équipe du cloud et leur a permis de faire l'interface pour le cloud.

Pour l'itération 3, on a perfectionné la structure MVC du projet en ajoutant des "Listener" afin de diminuer le couplage dans les différents fichiers. Ensuite, on a cloturé le design de l'interface des statistiques dans le but que le visuel soit plus compréhensible pour l'utilisateur. De ce fait, on a maintenant une vue générale et une vue individuelle pour une analyse plus fine des statistiques. Pendant ce temps, on a également travaillé sur le calendrier sur base de la librairie "CalendarFX". Enfin, on a ajouté une fonctionnalité afin de télécharger les projets via "GoogleDrive" pour pouvoir satisfaire un plus grand public d'utilisateurs.

## Screenshot
![signup](https://user-images.githubusercontent.com/33431271/112816753-03460200-9082-11eb-92de-fd46c727796f.PNG)
![login](https://user-images.githubusercontent.com/33431271/112816761-050fc580-9082-11eb-88ad-2a758668ddbd.PNG)
![projets](https://user-images.githubusercontent.com/33431271/116112919-537fa680-a6b8-11eb-90cb-7ac83e89539a.PNG)
![settings](https://user-images.githubusercontent.com/33431271/116112561-f552c380-a6b7-11eb-830e-90c832cd3114.PNG)
![cloud](https://user-images.githubusercontent.com/33431271/116112587-fb48a480-a6b7-11eb-955f-aec80cc15d7f.PNG)
![overall](https://user-images.githubusercontent.com/33431271/116116367-5def6f80-a6bb-11eb-9ce8-3a549d6d766c.PNG)

![projectstats](https://user-images.githubusercontent.com/33431271/116116335-562fcb00-a6bb-11eb-85bf-ab53ffdf73e0.PNG)


## License
