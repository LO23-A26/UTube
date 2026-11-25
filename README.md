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

# Instructions git
## Créer une feature branch
```bash
git switch develop-<nom du module>
git pull 
git git switch -c  <nom de module>/<feature branch>
```
## Mettre a jour une feature branch 

```bash
git switch develop-<nom du module>
git pull  # mettre a jour la branche develop
git switch <nom de module>/<feature branch>
git rebase develop-<nom du module> # rebase la branche sur la dernière version de votre develop de groupe
```

## Préparer une PR 

Mettre a jour votre feature branch avant de la publier. N'hesitez pas a demander a votre resp git pour de l'aide. 
```bash
git push --set-upstream origin <feature branch>
```

## Règles

Noms de commit explicites (You have one job)
>pas de "Fixed some bugs" de +10 000 lignes


## Config

Vous **devez** ajouter ces lignes dans votre fichier `.git/config` (a la racine du repo) ou par la ligne de commande 

```toml
[user]
        name = <Prénom> <Nom> - groupe <nom de groupe>
[pull]
        rebase = true
```

ou en ligne de commande : 
```bash 
git config user.name "<Prénom> <Nom> - groupe <nom de groupe>"
git config pull.rebase true
```

# proposition d'outils

VsCodium ou IntellyJ pour
- visualiser les branches 
- resoudre les conflits de rebase 



Apprendre git comme un jeu
https://ohmygit.org/



