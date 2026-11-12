# Frigo

Application Java Spring Boot pour la gestion d’un réfrigérateur intelligent.

## Description

Ce projet permet de gérer :
- les compartiments,
- les aliments en stock,
- les dictionnaires d’aliments,
- les utilisateurs,
- les courses et les alertes de péremption.

## Prérequis

Avant de lancer le projet, il faut avoir installé :
- Java 21
- Git
- MariaDB 10.6 ou version compatible
- Maven ou utiliser le wrapper fourni avec le projet (`mvnw`)

## Configuration de la base de données

Le projet utilise MariaDB avec la configuration suivante dans `src/main/resources/application.properties` :
- URL : `jdbc:mariadb://localhost:3307/frigo_intelligent`
- utilisateur : `root`
- mot de passe : vide

Pour que l’application fonctionne correctement, il faut :
1. installer MariaDB,
2. démarrer le service MariaDB,
3. créer une base de données nommée `frigo_intelligent`,
4. vérifier que le port `3307` est bien utilisé,
5. vérifier que le nom d’utilisateur et le mot de passe sont corrects pour votre environnement local.

La version exacte de MariaDB n’a pas besoin d’être identique partout, mais elle doit être compatible avec le driver JDBC du projet.

## Démarrer le projet

Depuis la racine du projet :

Windows PowerShell :

- .\mvnw.cmd clean install
- .\mvnw.cmd spring-boot:run

Ou avec Maven :

- mvn clean install
- mvn spring-boot:run

Le projet démarre ensuite sur le port :

- http://localhost:8082

## Important pour un clone

Pour qu’une personne qui clone le projet puisse le lancer sans problème, elle doit avoir :
- Java 21 installé,
- MariaDB installé et démarré,
- une base de données `frigo_intelligent` créée,
- le bon accès à la base dans `application.properties`.

Le projet utilise `spring.jpa.hibernate.ddl-auto=validate`, donc la base doit déjà exister avec les tables nécessaires.

## Structure du projet

- `src/main/java` : code source Java
- `src/main/resources` : fichiers de configuration et ressources
- `src/test/java` : tests
- `pom.xml` : configuration Maven

## Git

Pour envoyer le projet vers GitHub :

- git init
- git add .
- git commit -m "Initial commit"
- git branch -M main
- git remote add origin <url_du_repo_github>
- git push -u origin main

Pour cloner le projet ailleurs :

- git clone <url_du_repo_github>

## Auteur

Projet réalisé dans le cadre du cours / du développement du projet Frigo.
