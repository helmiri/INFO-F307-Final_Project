# Examen
[EN] Here are your questions for the INFO-F-307 exam.

To answer them, discuss with each other (on the platform of your choice) in order to provide the best answer possible.

For each question, answer in a clear, concise and complete manner, using the method that seems most appropriate to you, namely:
  - by replying in this file
  - by annotating / modifying your files
  	- for example, if a question concerns a wrong code, you can suggest a correction
  - by adding other files (PDF, ...)
  - by putting a link to a video of you answering us orally
  - ...

Whichever way you use it, if you don't answer a question directly in this file, clearly describe after that question where the answer can be found (for example, "see the Controller.java file").

Before 11am, once you think you've finished answering our questions, commit and push your changes on the git.

[FR] Voici vos questions pour l'examen d'INFO-F-307.

Pour y répondre, concertez-vous (via le moyen de votre choix) pour répondre au mieux à chaque question.

Pour chaque question, répondez de manière claire, concise et complète, en utilisant la méthode qui vous paraît la plus appropriée, à savoir:
  - en répondant dans ce fichier
  - en annotant/modifiant vos fichiers
    - par exemple, si une question porte sur un mauvais code, vous pouvez proposer une correction
  - en ajoutant d'autres fichiers (PDF, ...)
  - en mettant un lien vers une vidéo de vous nous répondant oralement
  - ...

Qu'importe la manière utilisée, si vous ne répondez pas directement à une question dans ce fichier, décrivez clairement après cette question où la réponse peut être trouvée (par exemple, "voir le fichier Controller.java").

Avant 11h, une fois que vous pensez avoir fini de répondre à nos questions, commitez et pushez vos modifications sur le git.

## Question 1
How did you use the git branching system ? How would you use it now that you are more familiar with the methodology XP ?

Lors de la première itération, nous avons simplement créé deux branches pour séparer la partie de l'interface (GUI) et la base de données (database) afin d'éviter les conflits.
Par la suite, nous avons utilisé des sous-branches pour gérer les demandes spéciales/difficiles du client (cloud-integration), ainsi que le *refactor* (Refactor-wip).
Ensuite, de manière générale, pour la gestion des autres histoires nous avons subdivisé les branches par itération (master-it2, master-it3, etc.) qui ont été fusionnées par la suite dans la branche *master*.

Pour appliquer plus fidèlement la méthodologie XP, nous aurions dû créer une branche par binôme et par histoire et/ou tâche particulière. Cela aurait permis de créer une arborescence plus adéquate à la répartition des tâches.

## Question 2
Why is the method Controller>>loadView not used in all the controllers to load the views ? Discuss and justify. How would you change exactly the code to improve it ?

Le but initial de cette méthode était d'éviter une duplication du code. Cependant dès lors que le controlleur devait gérer plusieurs scènes, la méthode était inutilisable car la méthode cache les détails de chargement (besoin d'accès au *loader*).

Afin d'améliorer la qualité du code, dans *StatsController* par exemple, nous aurions pu ajouter un "fxid" au *AnchorPane* dans le fichier fxml  et l'ajouter en attribut  à *StatsViewController* pour avoir accès à la scène à partir de la vue, ainsi passer le *stage* à la vue depuis le controlleur.
(**Voir MainMenuController et MenuViewController** pour un exemple d'implémentation.) Ainsi, nous pouvons utiliser la méthode *loadView* dans *StatController* (lignes 80-83).

## Question 3
src/be/ac/ulb/infof307/g06/views/settingsViews/ProfileViewController.java:61 : Why did you put tests in the view and then in the controller ? What is your policy on the division/sharing of responsability here ?

Notre idée initiale était de réinitialiser les *textFields* dans la vue lorsque les nouveaux attributs sont valides, par conséquant, la vue a besoin de connaître le résultat de l'opération effectuée par le controlleur.

Étant donnée la structure MVC de notre code, la vue n'est pas responsable des tests sur les données qu'elle récupère. Seul le controlleur doit les gérer et vérifier dans cette situation. 
Une amélioration possible est de créer des méthodes dans la vue qui seront appelées par le controlleur selon les résultats des tests.

## Question 4
src/be/ac/ulb/infof307/g06/exceptions/DatabaseException.java : Why is it a bad idea to play with AlertWindow in this class ? Discuss and justify. Moreover, is AlertWindow in the right package ?

Le problème d'*AlertWindow* dans cette classe est la gestion d'affichage (étant donné qu'*AlertWindow* est un pop-up), dans une classe de gestion d'exception. 
*DatabaseException* n'est pas responsable de l'affichage du message d'alerte.

*AlertWindow* gère internellement l'affichage des messages d'erreur au moyen d'*Alert* qui est un objet prédéfinit de JavaFX. Par conséquent, *AlertWindow* se comporte plutôt comme un controlleur qui gère une vue et donc cette classe devrait être placée dans le *package "controllers"* et
renommée à *AlertController* pour rester cohérent.

## Question 5
The tests and launcher of the jar require to use a specific script to launch everything. Does it fulfill the delivery asked by the client ? What can you do to give what the client asked ?

Lors des différentes réunions avec le client, nous n'avons pas eu de remarques particulières sur la simplicité du lancement de l'application. Ainsi, nous avons décidé de détailler les différentes commandes dans le *README*.
Mais après réflexion, l'idéal aurait été d'offrir une éxécution simple, sans connaissances en informatique, ou utilisation d'un terminal en prérequis.
Il est possible de séparer nos fichers de compilation en *compileTest*, *runTest* et *compileJar*, *runJar* ainsi que *compileMain*, *runMain*, pour permettre d'exécuter le programme avec un simple double click sur les fichiers.