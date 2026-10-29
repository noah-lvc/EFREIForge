# Rendu intermédiaire - EFREIForge Java
## Noah LAVOCAT & Doryan Mendy

## 1. Démarche

**Organisation du travail :**
Pour ce rendu intermediaire, nous avons d'abord fusionné et nettoyé le code de notre TP4 (classes `Collaborateur`, `Programmeur`, `Testeur` et la configuration JPA). Ensuite, nous avons intégré les nouvelles classes `Ticket` et `TicketService` et ses énumérations (`Priorite`, `Statut`).

**Éléments réutilisés :**
*   La persistance des données avec JPA et la base de données MariaDB.
*   L'héritage des collaborateurs pour différencier les programmeurs des testeurs.
*   La gestion des menus en console via le `Scanner` (en séparant bien l'affichage des règles métiers).

Nous nous sommes concentrés sur le bon fonctionnement de la création de tickets, de l'assignation, et de la sauvegarde en base de données avant d'aller plus loin.

## 2. Tableau autocontrôle du jalon 1

Voici le tableau de validation de nos fonctionnalités actuelles pour ce jalon :

| N° | Autocontrôle du jalon 1 | Résultat attendu | Constat |
| :--- | :--- | :--- | :--- |
| **J1** | Créer deux tickets de priorités différentes, puis consulter les tickets. | Ils apparaissent « À faire », sans responsable. | **OK** - Les tickets sont bien créés avec le statut par défaut et aucun assigné. |
| **J2** | Assigner un ticket à un programmeur, puis afficher les tickets de ce programmeur. | Le ticket apparaît dans sa liste. | **OK** - L'assignation fonctionne et les règles métiers (uniquement un programmeur) sont respectées. |
| **J3** | Arrêter puis relancer l'application, consulter les tickets. | Tickets, statuts et assignations retrouvés à l'identique. | **OK** - Les données sont bien stockées dans la base de données et réapparaissent après avoir relancé l'application. |
