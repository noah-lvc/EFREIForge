-- =====================================================================
-- EFREIForge - Script de création de la base et jeu de données
-- Noah LAVOCAT & Doryan MENDY - LSI 2 - 2026-27
--
-- Usage : mariadb -u root < sql/efreiforge.sql
--
-- Les noms de tables et de colonnes sont alignés sur le mapping JPA
-- (Adresse, Collaborateur, ticket). Attention à la casse : sous Linux,
-- MariaDB distingue "Collaborateur" et "collaborateur".
--
-- Le script est rejouable : il supprime puis recrée les tables.
-- =====================================================================

CREATE DATABASE IF NOT EXISTS efreiforge
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE efreiforge;

SET NAMES utf8mb4;

-- Suppression dans l'ordre inverse des dépendances (clés étrangères)
DROP TABLE IF EXISTS ticket;
DROP TABLE IF EXISTS Collaborateur;
DROP TABLE IF EXISTS Adresse;

-- ---------------------------------------------------------------------
-- Tables
-- ---------------------------------------------------------------------

-- Entité Adresse (TP4)
CREATE TABLE Adresse (
    id         BIGINT      NOT NULL AUTO_INCREMENT,
    rue        VARCHAR(50),
    codePostal INT         NOT NULL,
    ville      VARCHAR(50),
    pays       VARCHAR(40),
    PRIMARY KEY (id)
) ENGINE = InnoDB;

-- Entité Collaborateur (TP4) : héritage SINGLE_TABLE,
-- la colonne "metier" vaut 'Programmeur' ou 'Testeur'
CREATE TABLE Collaborateur (
    metier         VARCHAR(40) NOT NULL,
    identifiant    VARCHAR(10) NOT NULL,
    nom            VARCHAR(50) NOT NULL,
    prenom         VARCHAR(50),
    langagePrefere VARCHAR(40),
    salaire        DOUBLE      NOT NULL,
    adresse_id     BIGINT,
    PRIMARY KEY (identifiant),
    CONSTRAINT uk_collaborateur_adresse UNIQUE (adresse_id),
    CONSTRAINT fk_collaborateur_adresse FOREIGN KEY (adresse_id) REFERENCES Adresse (id)
) ENGINE = InnoDB;

-- Entité Ticket (projet) : association @ManyToOne unidirectionnelle
-- vers Collaborateur via la colonne assigne_id (NULL = non assigné)
CREATE TABLE ticket (
    identifiant BIGINT       NOT NULL AUTO_INCREMENT,
    titre       VARCHAR(255) NOT NULL,
    priorite    ENUM ('BASSE', 'NORMALE', 'HAUTE'),
    statut      ENUM ('A_FAIRE', 'EN_COURS', 'A_VALIDER', 'TERMINE'),
    assigne_id  VARCHAR(10),
    PRIMARY KEY (identifiant),
    CONSTRAINT fk_ticket_assigne FOREIGN KEY (assigne_id) REFERENCES Collaborateur (identifiant)
) ENGINE = InnoDB;

-- ---------------------------------------------------------------------
-- Jeu de données
-- ---------------------------------------------------------------------

INSERT INTO Adresse (id, rue, codePostal, ville, pays) VALUES
    (1, '30 avenue de la République', 94800, 'Villejuif', 'France'),
    (2, '12 rue de Rivoli',           75004, 'Paris',     'France'),
    (3, '5 place Bellecour',          69002, 'Lyon',      'France'),
    (4, '18 quai des Chartrons',      33000, 'Bordeaux',  'France'),
    (5, '7 rue Nationale',            59000, 'Lille',     'France'),
    (6, '44 boulevard Longchamp',     13001, 'Marseille', 'France');

-- 4 programmeurs et 2 testeurs
INSERT INTO Collaborateur (metier, identifiant, nom, prenom, langagePrefere, salaire, adresse_id) VALUES
    ('Programmeur', 'P001', 'Martin',  'Alice', 'Java',       3800, 1),
    ('Programmeur', 'P002', 'Dupont',  'Alex',  'Python',     3600, 2),
    ('Programmeur', 'P003', 'Benali',  'Inès',  'JavaScript', 3400, 3),
    ('Programmeur', 'P004', 'Leroy',   'Hugo',  'Kotlin',     3200, 4),
    ('Testeur',     'T001', 'Moreau',  'Lucas', 'Java',       3100, 5),
    ('Testeur',     'T002', 'Petit',   'Sarah', 'Python',     3150, 6);

-- 11 tickets répartis dans les 4 statuts, dont 3 non assignés.
-- P001 a déjà 3 tickets EN_COURS (#4, #5, #6) et un 4e À faire (#7)
-- pour rejouer le scénario 4 de la recette.
INSERT INTO ticket (identifiant, titre, priorite, statut, assigne_id) VALUES
    (1,  'Corriger l''export PDF',             'HAUTE',   'A_FAIRE',   NULL),
    (2,  'Ajouter la recherche par nom',       'NORMALE', 'A_FAIRE',   NULL),
    (3,  'Mettre à jour la documentation',     'BASSE',   'A_FAIRE',   NULL),
    (4,  'Refonte de la page de connexion',    'HAUTE',   'EN_COURS',  'P001'),
    (5,  'API de facturation',                 'NORMALE', 'EN_COURS',  'P001'),
    (6,  'Migration vers MariaDB 11',          'NORMALE', 'EN_COURS',  'P001'),
    (7,  'Ajouter les notifications par email', 'NORMALE', 'A_FAIRE',  'P001'),
    (8,  'Tableau de bord client',             'HAUTE',   'A_VALIDER', 'P002'),
    (9,  'Correctif du calcul de TVA',         'HAUTE',   'A_VALIDER', 'P003'),
    (10, 'Page « À propos »',                  'BASSE',   'TERMINE',   'P002'),
    (11, 'Optimiser les requêtes de recherche', 'NORMALE', 'A_FAIRE',  'P003');

-- Les identifiants ont été forcés : on repositionne les compteurs
ALTER TABLE Adresse AUTO_INCREMENT = 7;
ALTER TABLE ticket  AUTO_INCREMENT = 12;
