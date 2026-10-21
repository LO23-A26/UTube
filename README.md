# UTube
LO23 - A26 Application de partage de vidéos


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



