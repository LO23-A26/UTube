# UTube
LO23 - A26 Application de partage de vidéos

Chemin pour trouver les classes implémentées par data: ``src/main/java/common/dataclasses``

## Lancer le serveur et l'application

Prérequis : JDK 21 et Maven.

1. Compiler le projet (client et serveur sont dans le même module) :
   ```
   mvn compile
   ```
2. Démarrer le serveur dans un terminal, attendre le message `Listening on 0.0.0.0/0.0.0.0:9000` :
   ```
   mvn javafx:run@server
   ```
3. Démarrer le client dans un deuxième terminal :
   ```
   mvn javafx:run
   ```
4. Sur l'écran de connexion, renseigner l'IP du serveur (`127.0.0.1` en local) et le port `9000`.

Le serveur doit être lancé avant que le client tente de se connecter.