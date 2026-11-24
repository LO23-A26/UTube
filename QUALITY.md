# LO23 - A26 : Application de partage de vidéos

## Référence qualité

### Conventions

#### Rapport

L'ensemble des rendus en français seront rédigés en écriture inclusive, la convention utilisée sera le point médian. La référence inclusivité utilisée pour la rédaction sera le site suivant : https://eninclusif.fr/

#### Code

La documentation, les commentaires, les variables, les méthodes et les classes devront être en anglais. Les variables, méthodes et classes seront soumises aux conventions suivantes :

- Variable : snake_case (`^[a-z][a-z0-9]*(_[a-z0-9]+)*$`)
- Méthode : camelCase (`^[a-z][a-zA-Z0-9]*$`)
- Classe : PascalCase (`^[A-Z][a-zA-Z0-9]*$`)
  La [javadoc](https://simonandre.developpez.com/tutoriels/java/presentation-javadoc/) sera imposée pour la rédaction de chaque classe, interface et méthode. Chaque élément doit comporter une description claire de son rôle.

Un commentaire expliquant la méthode/classe et les tags suivants seront obligatoires lorsque cela s'applique :

- `@param`
- `@return`
- `@author`
- `@version`

### Git

#### Workflow

Le développement est effectué uniquement sur des branches feature. Une fonctionnalité terminée fait l'objet d'une Pull Request vers la branche du module ou vers `develop` selon le workflow défini par les responsables Git. Après revue par un autre membre du groupe et validation, elle est intégrée à `develop`. La branche main contient uniquement les versions stables et son intégration est réalisée par les responsables Git.

#### Convention de nommage des branches

`module/feature-name` : kebab-case

#### Convention de nommage des commits

- Correction : `Fix` + sujet
- Ajout : `Add` + sujet
- Suppression : `Remove` + sujet
- Modification : `Modify` + sujet
  Le sujet doit expliquer ce qui change, pas juste donner le nom du fichier modifié.

#### Pull Requests

Toutes les PR doivent être relues par une personne autre que l'auteur·rice du code.

Une PR ne peut être fusionnée que si le code compile, les tests associés passent et la revue par une autre personne est validée.

### Logging

On utilise le logging pour afficher des messages pendant l'exécution du programme, pour garder une trace de ce qu'il fait et retrouver l'origine d'un éventuel problème : connexion d'un client, envoi d'un message, erreur, etc.

Un logger ajoute automatiquement l'heure, la classe et la méthode à l'origine du message. Il associe à chaque message un niveau d'importance, ainsi que les exceptions levées.

On utilise le logger standard de Java (`System.Logger`). Chaque classe qui produit des logs déclare son propre logger :

```java
private static final System.Logger logger = System.getLogger(MyClass.class.getName());
```

Niveaux utilisés :

- `ERROR` : une erreur ou une exception qui empêche une action d'aboutir
- `INFO` : un événement normal, utile pour suivre l'exécution

Règles d'écriture :

- Une exception attrapée est nommée `ex` et transmise en entier au logger, plutôt que son seul message :
```java
logger.log(ERROR, "Message could not be sent", ex);
```

### SonarQube for IDE

L'extension SonarQube for IDE analyse le code pendant qu'on l'écrit et souligne les problèmes directement dans l'éditeur. 
Elle est configurée pour faire respecter nos règles de nommage et de documentation.
Chacun doit installer l'extension et activer les règles manuellement en local.

#### Règles à activer

| Règle | Titre | Format |
|---|---|---|
| `java:S116` | Field names should comply with a naming convention | `^[a-z][a-z0-9]*(_[a-z0-9]+)*$` |
| `java:S117` | Local variable and method parameter names should comply with a naming convention | `^[a-z][a-z0-9]*(_[a-z0-9]+)*$` |
| `java:S3008` | Static non-final field names should comply with a naming convention | `^[a-z][a-z0-9]*(_[a-z0-9]+)*$` |
| `java:S1176` | Public types, methods and fields (API) should be documented with Javadoc | — |
| `java:S106` | Standard outputs should not be used directly to log anything | — |

Les règles de nommage des méthodes (`S100`), des classes (`S101`) et des constantes (`S115`) sont déjà correctes par défaut et ne doivent pas être modifiées.

#### Si vous travaillez dans IntelliJ :

1. Installer l'extension : Settings → Plugins → Marketplace, chercher SonarQube for IDE, l'installer puis redémarrer IntelliJ.
2. Ouvrir Settings → Tools → SonarQube for IDE → Rules.
3. Pour chaque règle du tableau, chercher son titre dans le champ de recherche, puis :
  - cocher la case à droite de la règle pour l'activer
  - cliquer sur la règle et, si un format (regex) est indiqué, le remplacer dans Options → format, en bas de la description.
4. Cliquer sur Apply, puis OK. Si rien ne change dans l'éditeur, redémarrer IntelliJ.

#### Si vous travaillez dans VS Code :

1. Installer l'extension : dans l'onglet Extensions, chercher SonarQube for IDE et l'installer.
2. Ajouter le contenu suivant dans `.vscode/settings.json` :

```json
{
    "sonarlint.rules": {
        "java:S116":  { "level": "on", "parameters": { "format": "^[a-z][a-z0-9]*(_[a-z0-9]+)*$" } },
        "java:S117":  { "level": "on", "parameters": { "format": "^[a-z][a-z0-9]*(_[a-z0-9]+)*$" } },
        "java:S3008": { "level": "on", "parameters": { "format": "^[a-z][a-z0-9]*(_[a-z0-9]+)*$" } },
        "java:S1176": { "level": "on" },
        "java:S106":  { "level": "on" }
    }
}
```

#### Vérifier que ça fonctionne

- Une variable en camelCase (par exemple `videoId`) est soulignée.
- Une méthode publique sans javadoc est signalée.
- Un `System.out.println` est signalé.

Avant d'ouvrir une Pull Request, il faut vérifier qu'il ne reste plus de problème SonarQube dans les fichiers modifiés.
 