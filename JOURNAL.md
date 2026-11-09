# JOURNAL - EFREIForge
## Noah LAVOCAT & Doryan MENDY - LSI 2 - ALSI54 (2026-27)

---

## 1. Démarche

**Organisation du travail**

Nous avons travaillé à deux sur un dépôt GitHub commun (`noah-lvc/EFREIForge`), avec des branches de fonctionnalité fusionnées par pull request au début (`feat/consulter-ticket`, `feat/assigner-ticket`), puis directement sur `main` avec des commits courts.

- **PRJ 1 (22/10)** : création du projet Maven, récupération de nos classes du TP4, intégration de l'entité `Ticket` et des énumérations `Priorite` / `Statut`, premières règles d'assignation.
- **Jalon 1 (29/10)** : menu console découpé en sections (Collaborateurs / Tickets), création, consultation, assignation et affichage des tickets par collaborateur depuis le menu.
- **PRJ 2 (02/11)** : réorganisation en packages (`modele`, `service`, `exception`, `ui`), cycle de vie des tickets, suppression.
- **04/11 → 09/11** : refactoring du cycle de vie, modification d'un ticket, correction de bugs, gestion des erreurs de saisie, script SQL et journal final.

**Éléments du TP4 réutilisés**

- La hiérarchie `Collaborateur` (abstraite) → `Programmeur` / `Testeur`, persistée en `SINGLE_TABLE` avec la colonne discriminante `metier`. Elle sert directement aux règles métier : on teste `instanceof Programmeur` pour l'assignation et `instanceof Testeur` pour la validation.
- L'entité `Adresse` (`@OneToOne` avec cascade `PERSIST`).
- Le `CollaborateurService` (ajout, recherche, augmentation, comptage) et son modèle de transaction : un `EntityManager` par opération, `begin` / `commit`, `rollback` en cas d'erreur.
- L'exception métier `CollaborateurDejaExistantException`.
- La configuration JPA / Hibernate / MariaDB (`persistence.xml`) et la journalisation SLF4J.

Le `TicketService` reprend exactement le même modèle que le `CollaborateurService`.

**Autocontrôle du jalon 1** (rappel)

| N° | Autocontrôle du jalon 1 | Résultat attendu | Constat |
| :--- | :--- | :--- | :--- |
| J1 | Créer deux tickets de priorités différentes, puis consulter les tickets. | Ils apparaissent « À faire », sans responsable. | OK |
| J2 | Assigner un ticket à un programmeur, puis afficher les tickets de ce programmeur. | Le ticket apparaît dans sa liste. | OK |
| J3 | Arrêter puis relancer l'application, consulter les tickets. | Tickets, statuts et assignations retrouvés à l'identique. | OK |

---

## 2. Deux décisions techniques

### Décision 1 - Répartir les règles entre l'entité `Ticket` et le `TicketService`

- **Dans l'entité `Ticket`** : les règles qui ne dépendent que du ticket lui-même. Le constructeur refuse un titre vide ou composé d'espaces et force le statut `A_FAIRE` sans responsable ; `setTitre` applique la même vérification ; `assignerCollaborateur` refuse un collaborateur absent, un non-programmeur et un ticket qui n'est plus « À faire » (`AssignationImpossibleException`).
- **Dans le `TicketService`** : les règles qui ont besoin de la base, comme la limite de 3 tickets « En cours » par programmeur (requête JPQL `count`), ainsi que le cycle de vie (`avancerTicket` / `reculerTicket`).

**Pourquoi :** un ticket ne peut jamais être construit dans un état invalide, quel que soit l'appelant. La règle des 3 tickets a besoin d'une requête, donc elle n'a pas sa place dans l'entité. Le cycle de vie est écrit avec un `switch` sur le statut : chaque statut n'autorise qu'une seule transition « avancer » (et seul `A_VALIDER` peut « reculer »). Les sauts d'étape et les retours depuis « Terminé » sont donc impossibles par construction.

### Décision 2 - Valider d'abord en mémoire, écrire ensuite (une opération refusée ne touche pas la base)

Chaque opération suit le même schéma : on charge le ticket (et le collaborateur), on applique la règle sur l'objet en mémoire, et **on n'appelle `mettreAJourTicket` (`merge` dans une transaction) que si aucune exception n'a été levée**. En cas d'erreur pendant la transaction, on fait un `rollback`. La suppression relit le ticket dans la transaction et vérifie son statut avant `remove`.

**Pourquoi :** cela garantit la règle 9 (« une opération refusée ne modifie jamais les données ») sans logique de compensation, et les messages d'erreur viennent directement des exceptions métier (`IllegalArgumentException`, `IllegalStateException`, `AssignationImpossibleException`), affichées proprement par le `Menu`.

Nous avons aussi choisi `@Enumerated(EnumType.STRING)` pour la priorité et le statut : la base reste lisible (`EN_COURS` plutôt que `1`) et l'ordre des valeurs de l'énumération peut changer sans corrompre les données.

---

## 3. État du projet

### Ce qui fonctionne

- Collaborateurs (TP4 conservé) : création d'un programmeur ou d'un testeur avec adresse, liste, recherche par nom ou identifiant, comptage, augmentation de salaire.
- Tickets : création (titre obligatoire, statut « À faire », sans responsable), consultation de tous les tickets ou par statut, assignation à un programmeur uniquement et seulement à l'état « À faire », affichage des tickets d'un collaborateur.
- Cycle de vie : À faire → En cours (ticket assigné obligatoire, 3 tickets « En cours » maximum par programmeur) → À valider → Terminé (par un testeur désigné), et renvoi À valider → En cours (par un testeur).
- Modification du titre ou de la priorité (refusée sur un ticket « Terminé »).
- Suppression autorisée uniquement pour un ticket « À faire ».
- Persistance JPA des tickets et de leurs assignations ; tout est retrouvé après redémarrage.
- Script SQL rejouable avec un jeu de données (6 collaborateurs, 11 tickets).

### Ce qui reste incomplet / limites connues

- **Liste des « tickets à répartir »** (tickets non assignés triés par priorité décroissante puis par numéro) : **non implémentée** dans le menu. On peut s'en approcher avec « Afficher par statut → A_FAIRE », mais sans le tri ni le filtre « non assigné ».
- **Saisie du code postal** lors de la création d'un collaborateur : la saisie utilise `nextInt()`. Taper du texte à cet endroit lève une `InputMismatchException`, qui n'est pas interceptée, et l'application s'arrête. Toutes les autres saisies numériques (identifiants de tickets, salaire, pourcentage) sont protégées.
- La règle « Terminé non modifiable » est vérifiée dans le `Menu` et pas dans l'entité `Ticket` : un autre appelant pourrait la contourner.
- L'identifiant du ticket s'appelle `identifiant` (type `long`) et non `id` comme dans le squelette du sujet, pour rester cohérent avec `Collaborateur`. La colonne en base est donc `identifiant`.
- Le menu principal n'affiche pas de message en cas de choix invalide (il se réaffiche simplement).
- Les approfondissements facultatifs (tableau de bord, réassignation en masse) n'ont pas été réalisés.

### Difficultés significatives

- **Cycle de vie** : la première version mélangeait les transitions et les contrôles dans le menu. Nous l'avons refactorée (`avancerTicket` / `reculerTicket` dans le service) pour centraliser les règles.
- **Entités détachées** : chaque méthode du service ouvre et ferme son propre `EntityManager`. Le ticket modifié dans le menu est donc détaché, et nous le resynchronisons avec `merge` au lieu de compter sur la synchronisation automatique.
- **Nom de la base** : nous avons dû aligner le nom de la base (`efreiforge`) entre notre TP4, le `persistence.xml` et le script SQL.

### Tableau de recette

Jeu de données utilisé : celui du script `sql/efreiforge.sql`.

| N° | Scénario | Résultat attendu | Constaté |
| :--- | :--- | :--- | :--- |
| 1 | Créer un ticket sans titre (vide ou espaces), puis un ticket valide. | Le premier est refusé ; le second est créé « À faire », sans responsable. | **OK** - « Création annulée : Le titre du ticket est obligatoire. », puis le ticket valide apparaît en `A_FAIRE` / « Non assigné ». |
| 2 | Assigner un ticket à un programmeur, puis un autre ticket à un testeur. | Le premier est accepté ; le second est refusé. | **OK** - #1 → P002 accepté ; #2 → T001 : « Assignation refusée : Seul un programmeur peut être assigné ». |
| 3 | Démarrer un ticket non assigné, puis un ticket assigné. | Refus pour le premier ; le second passe « En cours ». | **OK** - #3 : « Le ticket doit être assigné avant d'être démarré. » ; #11 (P003) passe `EN_COURS`. |
| 4 | Faire passer « En cours » un 4e ticket d'un programmeur qui en a déjà 3. | Refus ; le ticket reste « À faire ». | **OK** - #7 (P001, qui a déjà #4, #5 et #6 en cours) : refus, le ticket reste `A_FAIRE`. |
| 5 | Tenter de passer un ticket directement de « À faire » ou « En cours » à « Terminé ». | Refus, statut inchangé. | **OK** - L'action « Avancer » ne propose que l'étape suivante ; aucun saut n'est possible. |
| 6 | Soumettre un ticket à validation, tenter de le valider avec un programmeur, puis avec un testeur ; renvoyer un autre ticket « À valider » en cours. | Refus avec le programmeur ; « Terminé » avec le testeur ; le ticket renvoyé repasse « En cours ». | **OK** - #8 avec P001 : « Seul un Testeur peut valider un ticket. » ; avec T001 → `TERMINE` ; #9 renvoyé par T002 → `EN_COURS`. |
| 7 | Sur un ticket « Terminé » : modifier le titre, le réassigner. Supprimer un ticket « En cours », puis un ticket « À faire ». | Modification, réassignation et première suppression refusées ; la seconde suppression est acceptée. | **OK** - #10 : modification et assignation refusées ; suppression de #4 refusée ; suppression de #2 acceptée. |
| 8 | Arrêter puis relancer l'application ; utiliser un ticket ou un collaborateur inexistant ; saisir du texte quand un nombre est attendu. | Tickets, statuts et assignations retrouvés ; message clair, l'application ne plante pas. | **Partiel** - Données retrouvées après redémarrage ; ticket ou collaborateur inexistant → message clair ; texte à la place d'un identifiant de ticket → message clair. **KO** sur le code postal à la création d'un collaborateur (voir limites). |

---

## 4. README - Lancer et essayer l'application

### Prérequis

- **JDK 27** (le `pom.xml` compile avec `maven.compiler.release = 27`).
- **Maven 3.9+** (ou IntelliJ IDEA, qui intègre Maven).
- **MariaDB** sur `localhost:3306`, utilisateur `root` **sans mot de passe** (sinon, adapter `jakarta.persistence.jdbc.user` / `password` dans `src/main/resources/META-INF/persistence.xml`).

### Préparer la base

Depuis la racine du projet :

```bash
mariadb -u root < sql/efreiforge.sql
# ou : mysql -u root < sql/efreiforge.sql
```

Le script crée la base `efreiforge` si elle n'existe pas, (re)crée les tables `Adresse`, `Collaborateur` et `ticket`, puis insère le jeu de données :

- **Programmeurs** : `P001` Alice Martin, `P002` Alex Dupont, `P003` Inès Benali, `P004` Hugo Leroy.
- **Testeurs** : `T001` Lucas Moreau, `T002` Sarah Petit.
- **Tickets** : #1, #2, #3 « À faire » non assignés ; #4, #5, #6 « En cours » (P001) ; #7 « À faire » (P001) ; #8, #9 « À valider » ; #10 « Terminé » ; #11 « À faire » (P003).

⚠️ Le script est rejouable : il **supprime** les tables existantes avant de les recréer.

### Démarrer l'application

```bash
mvn compile exec:java -Dexec.mainClass=fr.efrei.efreiforge.ui.Main
```

Ou, dans IntelliJ : ouvrir le projet Maven et lancer `fr.efrei.efreiforge.ui.Main`.

### Quelques manipulations pour vérifier

1. `2` Tickets → `3` Voir → `1` Tous : les 11 tickets du jeu de données s'affichent.
2. `2` → `2` Gestion → `2` Avancer → ticket `7`. Refus : P001 a déjà 3 tickets en cours.
3. `2` → `2` → `2` Avancer → ticket `8` → collaborateur `P001`. Refus : seul un testeur peut valider. Recommencer avec `T001` : le ticket passe `TERMINE`.
4. `2` → `2` → `3` Reculer → ticket `9` → `T002` : le ticket repasse `EN_COURS`.
5. `2` → `2` → `4` Supprimer → `4` (refusé, en cours), puis `2` (accepté).
6. Quitter (`0`), relancer, et réafficher les tickets : les statuts et assignations sont identiques.

---

## 5. Contribution de chacun

- **Noah LAVOCAT** : création du projet Maven et du `pom.xml`, entité `Ticket` et énumérations, `TicketService` (enregistrement, consultation), menu console et ses sections (création, listage, assignation, affichage par collaborateur), journal du jalon 1, refactoring du cycle de vie, modification d'un ticket, gestion des erreurs de saisie, corrections finales.
- **Doryan MENDY** : récupération des classes et de la persistance du TP4, règles d'assignation des tickets, affichage des tickets par collaborateur, configuration de la base `efreiforge`, première version du cycle de vie, suppression des tickets, script SQL et journal final.
