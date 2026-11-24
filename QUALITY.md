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

### SonarQube sur IDE

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

- Une variable en camelCase est soulignée.
- Une méthode publique sans javadoc est signalée.
- Un `System.out.println` est signalé.

Avant d'ouvrir une Pull Request, il faut vérifier qu'il ne reste plus de problème SonarQube dans les fichiers modifiés.


## Quality guidelines (English version)

### Conventions

#### Reports

All deliverables written in French must use inclusive writing, following the middle dot convention (point médian). The reference used for inclusive writing is the following website: https://eninclusif.fr/

#### Code

Documentation, comments, variables, methods and classes must be written in English. Variables, methods and classes must follow these naming conventions:

- Variable: snake_case (`^[a-z][a-z0-9]*(_[a-z0-9]+)*$`)
- Method: camelCase (`^[a-z][a-zA-Z0-9]*$`)
- Class: PascalCase (`^[A-Z][a-zA-Z0-9]*$`)
  [Javadoc](https://www.oracle.com/technical-resources/articles/java/javadoc-tool.html) is mandatory for every class, interface and method. Each element must include a clear description of its role.

A comment explaining the method/class and the following tags are mandatory whenever they apply:

- `@param`
- `@return`
- `@author`
- `@version`
### Git

#### Workflow

Development is done on feature branches only. A finished feature is submitted through a Pull Request to the module branch or to `develop`, according to the workflow defined by the Git managers. After being reviewed and approved by another member of the group, it is merged into `develop`. The `main` branch only contains stable versions and is updated by the Git managers.

#### Branch naming convention

`module/feature-name`: kebab-case

#### Commit naming convention

- Fix: `Fix` + subject
- Addition: `Add` + subject
- Removal: `Remove` + subject
- Change: `Modify` + subject
  The subject must explain what changes, not just give the name of the modified file.

#### Pull Requests

Every PR must be reviewed by someone other than the author of the code.

A PR can only be merged if the code compiles, the related tests pass and the review by another person is approved.

### Logging

Logging is used to display messages while the program is running, in order to keep track of what it does and to find the origin of a potential problem: a client connecting, a message being sent, an error, etc.

A logger automatically adds the time, the class and the method that produced the message. It gives each message a level of importance, and includes the exceptions that were raised.

We use the standard Java logger (`System.Logger`). Each class that produces logs declares its own logger:

```java
private static final System.Logger logger = System.getLogger(MyClass.class.getName());
```

Levels used:

- `ERROR`: an error or an exception that prevents an action from completing
- `INFO`: a normal event, useful to follow the execution
  Writing rules:

- A caught exception is named `ex` and passed as a whole to the logger, rather than only its message:
```java
logger.log(ERROR, "Message could not be sent", ex);
```

### SonarQube for IDE

The SonarQube for IDE extension analyzes the code while it is being written and highlights issues directly in the editor.
It is configured to enforce our naming and documentation rules.
Everyone must install the extension and enable the rules manually on their own machine.

#### Rules to enable

| Rule | Title | Format |
|---|---|---|
| `java:S116` | Field names should comply with a naming convention | `^[a-z][a-z0-9]*(_[a-z0-9]+)*$` |
| `java:S117` | Local variable and method parameter names should comply with a naming convention | `^[a-z][a-z0-9]*(_[a-z0-9]+)*$` |
| `java:S3008` | Static non-final field names should comply with a naming convention | `^[a-z][a-z0-9]*(_[a-z0-9]+)*$` |
| `java:S1176` | Public types, methods and fields (API) should be documented with Javadoc | — |
| `java:S106` | Standard outputs should not be used directly to log anything | — |


#### If you work in IntelliJ:

1. Install the extension: Settings → Plugins → Marketplace, search for SonarQube for IDE, install it, then restart IntelliJ.
2. Open Settings → Tools → SonarQube for IDE → Rules.
3. For each rule in the table, search for its title in the search field, then:
  - check the box on the right of the rule to enable it,
  - click on the rule and, if a format (regex) is given, replace it in Options → format, at the bottom of the description.
4. Click Apply, then OK. If nothing changes in the editor, restart IntelliJ.
#### If you work in VS Code:

1. Install the extension: in the Extensions tab, search for SonarQube for IDE and install it.
2. Add the following content to `.vscode/settings.json`:
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

#### Checking that it works

- A variable in camelCase is underlined.
- A public method without Javadoc is reported.
- A `System.out.println` is reported.
  Before opening a Pull Request, make sure there are no SonarQube issues left in the modified files.
 