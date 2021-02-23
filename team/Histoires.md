# Histoires
Informations récapitulatives concernant les différentes histoires.

#### Quelques précisions
Un point correspond à une heure de travail par binôme (approximatif).  Par itération il faut accomplir X points.

----------------------


## Pondération

| Priorité/3 | N° | Description | Difficulté/3 | Risque/3 | Heures/? | Points |
| ------ | ------ | ------ | ------ | ------ | ------ | ------ |
| 1 | [1](#Histoire-1) | Histoire 1 |  | 2 | 30 | / |
| 2 | 2 | Histoire 2 |  | 2 | 33 | / |
|   | 3 | Histoire 3 |  | 3 | 15 |  |
|   | 4 | Histoire 4 |  | 1 | 35 |  |
|   | 5 | Histoire 5 |  | 1 | 27 |  |
|   | 6 | Histoire 6 |  | 1 | 30 |  |
|   | 7 | Histoire 7 |  | 3 | 18 |  |
|   | 8 | Histoire 8 |  | 3 | 10 |  |
|   | 9 | Histoire 9 |  | 1 | 10 |  |
| 3 | 10 | Histoire 10 |  | 2 | 25 | / |
|   | 11 | Histoire 11 |  | 3 | 4 |  |
|   | 12 | Histoire 12 |  | 2 | 15 |  |
|   | 13 | Histoire 13 |  | 1 | 20 |  |


----------------------


## Tableau Risque-priorité

| &#8595; Priorité / Risque &#8594; | 1 | 2 | 3 |
| ------ | ------ | ------ | ------ |
| 1 |  | 1 |  |
| 2 | 4-5-6-9 | 2 | 3-7-8 |
| 3 | 13 | 10-12 | 11 |


----------------------

## Description


### Histoire 1
**Instructions originales:**
- Présence de conditions d'utilisateurs, qui doivent être acceptés pour pouvoir accéder à l'application. 3h 
- Connexion à l'interface (mot de passe, nom d'utilisateur, nom de famille,prénom, adresse email). 25h
- Validitée des données des l'utilisateurs. 2h

:question: **Question:**       
- est-ce qu'un seul utilisateur peut être connecté à tout moment? Réponse : Un utilisateur/client.


### Histoire 2
**Instructions originales:**
- Pouvoir créer un projet (étiquettes, nom, description, date). 12h
- Création de sous projets. 2h 
- Ajout de tâches à un projet. 7h
- L'utilisateur doit pouvoir passer en mode édition pour un projet. 10h
- Possibilité de supprimer un projet. 2h

:question: **Question:**  
- Qu'est-ce qu'une tâche concrètement? Réponse : Une simple chose à faire, sans date ni heure, ni échéance.


### Histoire 3
**Instructions originales:**
-  Spécifier une date de début et/ou de fin aux tâches du projet. 4h
-  Choisisr une date via une interface calendrier. 3h
-  Spécifier la date de début et le temps attribué pour le projet. 4h 
-  Priorité des tâches en fonction du temps. 4h

:question: **Question:**  
- Peut-on avoir plusieurs tâches en même temps, si oui comment l'afficher?
- Doit-on avoir fini une tâche avant de pouvoir se lancer dans la 2e?
- Doit-on prendre en compte la complexité des tâches pour la priorité?


### Histoire 4
**Instructions originales:**
- Créer un calendrier (graphiquement) afin de visualiser les tâches et les projets. 20h
- Le calendrier permet la sélection d'un ou plusieurs projets qui peuvent être associés chacun à une couleur du choix de l'utilisateur. 4h
- Tâches reliées aux projets par leur nom et leur couleur. 2h
- Faire en sorte de voir le début et la fin d'une tâche. 5h

**Tâches en plus:** 
- Penser à tout type d'utilisateur (handicape, daltonisme,..) 1h
- Créer potentiellement 3 types d'affichage (par jour,par semaine et par mois). 3h

:question: **Question:** 
- Quel type de calendrier( une fenêtre entière ou bien juste dans un coin comme sous windows)?


### Histoire 5
**Instructions originales:** 
- Accès à un système de versions. 
- Suivi de l'évolution d'un projet:
        - "add" l'ajout de fichiers et dossiers d'un projet.
        - "remove" la suppression de fichiers et dossiers d'un projet.
        - "branch" le branchement d'un projet.
        - "commit" valider les modifications apportées à une certaine branche avec un message
          décrivant les changements
        - "revert" annuler une ou plusieurs validations précédentes, pour rétablir une version
          précédente.
        - "merge" fusionner deux branches, les modifications de l'une sont portées sur l'autre.
        - "diff" présente la différence entre la version actuelle et un commit précédent spécifié.
        
- Un commit sera lié à l"utilisateur l'ayant fait.
- Création d'un menu pour gérer ces fonctionnalités.

:question: **Question:** 
- /
 
 
### Histoire 6
**Instructions originales:**
- Permettre la possibilité d'ajouter d'autres collaborateurs à un projet (s'ils l'acceptent).
- Les autres collaborateurs ont le droit de le modifier et de le supprimer.
- Les sous projets du projet partagé doivent aussi être partagés.
- Il doit être possible d'assigner des tâches à un ou plusieurs des utilisateurs collaborateurs (chaque utilisateur peut modifier les attributions).
- Les tâches assignées doivent être bien visibles pour les utilisateurs.

**Tâches en plus:** 
- Lier les rappels avec les collaborateurs.

:question: **Question:** 
- /

### Histoire 7
**Instructions originales:**
- Exporter un fichier compressé(.tar.gz).
- Peut importer un projet ayant été exporté pour apparaitre comme projet dans l'application (pas forcément utilisable en dehors de l'application). 3h

:question: **Question:** 
- /


### Histoire 8
**Instructions originales:**
- Visualisation de statistiques importantes pour l"utilisateur(nombre de personnes concernées, tâches restantes, durée estimée du projet et durée réelle du projet).
- Il peut aussi visualiser ces métriques pour un projet choisi ou bien pour l'ensemble de ses projets.
- Possibilité d'exporter les statistiques en ".csv", ".json".

:question: **Question:** 
- /


### Histoire 9
**Instructions originales:**
- Chaque utilisateur dispose d'une quantité d'espace disque définie par l'administrateur.
- L'espace peut être étendu grâce à l'aide de services de stockage cloud.
- Possibilité d'exporter ses projets vers le service web désiré ainsi qu'importer des fichiers de celui-ci.
- L'exportation/importation peut s'effectuée jusqu'à plusieurs fichiers à la fois.
- Le système doit signaler si un fichier déjà présent dans le système est en train d'être téléchargé par l'utilisateur.
- Intégration faites en utilisant les API dédiées fournies par les services mêmes.

:question: **Question:** 
- /


### Histoire 10
**Instructions originales:**
- Intégration d'un système de rappels pour les projets et les tâches avec une date d'échéance.(via l'application ou par l'intégration d'une application calendrier externe).
- Si les rappels se font via l'application, ceux ci doivent être présentés à l'utilisateur lors du démarrage du logiciel, avec la possibilité de soit validé les rappels, soit de les reporter à une date ultérieure.
- Cependant si les rappels se font via l'intégration d'une application externe, ceux ci sont déleguée à celle-ci.

:question: **Question:** 
- Sous quelle forme doit apparaître le rappel lors du démarrage de l'application?


### Histoire 11
**Instructions originales:**
- Accès à une section d'aide 'informations et explications sur les fonctionnalités du programme).
- Possiblement l'intégration d'un tutoriel qui pourrait être démarré à partir de cette section.

:question: **Question:** 
- /


### Histoire 12
**Instructions originales:**
- Les données sauvegardées ne doivent pas être accesibles par des personnes tierces(confidentialité).
- Protection par mot de passe pour les fichiers ou les exportation de projet.
- Ne peut ouvrir ou importer un fichier/un projet protégé que si le mod de passe entré est correcte.

:question: **Question:** 
- /


### Histoire 13
**Instructions originales:**
- Garantit que les données stockées dans l'application ne puissent pas être alétérées de façon fortuite,illicite ou malveillante.
- Garantit l'absence de modifications non autorisées.

:question: **Question:** 
- Pouvons-nous utiliser une database basée sur du cloud (MongoDB Atlas).

