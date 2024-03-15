# Git

Git è un software di controllo versione distribuito utilizzabile da interfaccia a riga di comando, creato da Linus Torvalds nel 2005.

Git nello slang americano significa idiota proprio' perche' il suo sviluppatore lo ideo' come strumento semplice per versionare il sorgente del Kernel Linux che nei primi anni 2000 era diventato un progetto oramai assodato.

Oggi è il software di versionamento più diffuso grazie alla sua semplicita' di utilizzo e la sua efficacia.

In ottica DevOps e' considerato lo strumento di partenza su cui si basano i piu' moderni paradigmi del design del software e delle architetture, quali "infrastructure as a code" e "gitops" che affiancati alla metodologia Agile si dimostrano sempre piu' efficaci e affidabili.

Le sue peculiarita' di essere semplice lo rendono certamente utile e comodo e allo stesso tempo uno strumento letale e pericoloso.

Per far in modo che questo strumento ci sia sempre utile e che non ci venga mai contro e' necessario chiarire bene il suo principio di funzionamento e applicare alcune best practice che dopo la sua larga diffusione sono diventate standard e vengono identificate con il termine "Git Flow".

E' fortemente consigliato, sopratutto se siamo agli inizi, utilizzare Git a riga di comando proprio per la sua natura per acquisire padronanza con lo strumento e capire meglio tutte le sue funzionalita'.

## Setup

Git e' installato di default sulla maggior parte delle distribuzioni Gnu/Linux e in generale su tutti i sistemi operativi desktop piu' utilizzati.

In caso non lo avessimo possiamo installarlo utilizzando il nostro package manager. Per gli utenti windows e' fortemente congliato l'utilizzo del WSL nella sua versione 2 nel momento in cui scriviamo e' stata appena rilasciata.

### Configurazione

Se e' la prima volta che usiamo Git sono necessarie alcune configurazioni di base.
Possiamo verificare la nostra configurazione con il comando: `git config --list`

- Configurazione username: `git config --global user.name <username>`
- Configurazione email: `git config --global user.email <email>`
- Configurazione editor: `git config --global core.editor vim`

#### Opzionale

- Configurazione difftool: `git config --global diff.tool <meld/vimdiff>`
- Configurazione alert difftool: `git config --global difftool.prompt false`


N.B. Queste configurazioni si posso applicare anche per progetto eliminando il parametro `--global` 

#### Windows host

- Configurazione crlf: `git config --global core.autocrlf true`
- Configurazione warning auto-crlf: `git config --global core.safecrlf false`

N.B. Se utilizziamo Git sul WSL queste configurazioni vanno applicate sia su Git Bash per il nostro IDE che sulla nostra distro WSL.

#### Oh-My-ZSH

- Disabilitare auto-tracking git: `git config --global --add oh-my-zsh.hide-dirty 1`


## Basics

Di seguito elenchiamo i comandi base con la loro descrizione.

- Inizializzare una directory come repository: `git init`
- Visualizzare lo stato attuale: `git status`
- Aggiungere i file aggiunti e/o modificati in stage area: `git add .`
- Commit rapido: `git commit -m "Messaggio"`
- Commmit esteso (scelta consigliata): `git commit`
- Clonare un repository: `git clone git@git.sindria.org:<project-namespace>/<repo-name>.git`
- Eliminare un remote: `git remote rm <remote-alias>` es. `git remote rm origin`
- Aggiungere un remote: `git remote add origin git@git.sindria.org:<project-namespace>/<repo-name>.git`
- Spostarsi di branch: `git checkout <branch-name>` es. `git checkout sviluppo`
- Creare un branch: `git checkout -b <branch-name>` es. `git checkout -b feature-customers`
- Push dei commit: `git push origin <branch-name>` es. `git push origin feature-customers`
- Pull dei commit: `git pull origin <branch-name>` es. `git pull origin feature-customers`
- Pull dei tags: `git pull origin --tags`
- Visualizzare la differenza delle modifiche: `git diff`
- Visualizzare la log history: `git log` 
- Modificare il commit piu' recente: `git commit --amend`

### Flusso base

- Fetchare le modifiche: `git fetch --all`
- Pullare le modifiche: `git pull origin <branch-name>`
- Visualizzare lo stato: `git status`
- Aggiungere i file: `git add .`
- Visualizziamo lo stato: `git status`
- Commit delle modifiche: `git commit`
- Push dei commits: `git push origin <branch-name>`

N.B. Quando si aggiungono i file in stage con il comando `git add .` ricordarsi di essere sempre nella root del repository git.

### Creare un nuovo repository

```
mkdir -p ~/winhome/Projects
mkdir -p ~/winhome/Projects/Sindria
mkdir -p ~/winhome/Projects/Sindria/<project-namespace>
cd ~/winhome/Projects/Sindria/<project-namespace>
mkdir -p <repo-name>
cd <repo-name>
git init
touch readme.md
touch .gitignore
git add .
git commit -m "Init"
git remote add origin git@git.sindria.org:<project-namespace>/<repo-name>.git
git push -u origin master
```


### Pushare un repository esistente

```
cd ~/winhome/Projects/Sindria/<project-namespace>/<repo-name>
git remote rm origin
git remote add origin git@git.sindria.org:<project-namespace>/<repo-name>.git
git push -u origin --all
git push -u origin --tags
```


## Flow

Con il termine flow si intende quella procedura standard per gestire il flusso di sviluppo con il sistema di versionamento git.
Tipicamente questa prassi e' indicata per il lavoro in team o prt progetti relativamente grossi.

Come standard DevOps si e' scelto di utilizzare le procedure comuni del gitlab flow con delle convenzioni di naming conventions dei branch contenitori.
I branch contenitori standard definiti sono i seguenti:

*  master - Questo e' il branch di default bloccato da considerare sempre rilasciabile e dunque quello di produzione.
*  collaudo - Questo e' il branch custom bloccato da considerare rilasciabile per gli ambienti di collaudo.
*  sviluppo - Questo e' il branch custom libero da considerare come tavolo di lavoro per tutto il flusso di sviluppo e le implementazioni delle varie feature.

N.B. E' ammessa la variante staging per il branch di collaudo e develop per il branch di sviluppo per i team internazionali e/o a discrezione del TL/PM.


Per rendere piu' semplice lo sviluppo in team si da per assunto che ogni feature (funzionalita') venga sviluppata nel cosidetto "feature branch".

Questo dovra' avere come nomenclatura standard `feature-<nome-feature>`. Il merge di quest'ultimo poi dovra seguire la merge request che dovra' essere approvata dal tech lead del progetto o comunque da un altro sviluppatore con skill equivalenti.
Il branch di destinazione di tutte le merge request dovra' sempre essere il branch contenitore standard denominato "sviluppo".

E' ammessa la viariante `fix-<fix-name>` per i branch relativi ai bug fix.

N.B. Si ricorda che i "feature branch" dovranno sempre essere creati partendo dal branch denominato "sviluppo".


![flow](assets/media/flow.jpg)

### Commits

La scelta consigliata e' sempre creare il commit esteso. `git commit`

La best practice vuole che i commits abbiamo la prima riga sempre una descrizione generica della feature o bux fix dove stiamo lavorando seguita da due a capo con un elenco puntato dei tutto quello che e' stato aggiunto, modificato e refactorizzato.

Esempio:

```
Feature customers authoring

- Added base models with data migrations
- Added crud structure mvc with common methods
- Edited export customers - now export directy as csv format.
- Deleted procedural helpers for controller logic
- Refactor helpers method as OOP
```


## Tree

Come standard DevOps la convenzione dell'alberature dei vari repository dovra' sempre rispettare lo standard seguente:

### Project tree

.
 * [readme.md](assets/template/readme.md)
 * [src](./assets)
   * [Dockerfile](assets/template/src/Dockerfile)
   * [.dockerignore](assets/template/src/.dockerignore)
 * [.gitignore](assets/template/.gitignore)
 * [docker-compose.local.yml](assets/template/docker-compose.local.yml)
 * [docker-compose.production.yml](assets/template/docker-compose.production.yml)
 * [.env.local](assets/template/.env.local)
 * [.env.production](assets/template/.env.production)
 * [.gitlab-ci.yml](assets/template/.gitlab-ci.yml)
 * [build.sh](assets/template/build.sh)


## Release

Il rilascio deve sempre avvenire dopo il superamento della fase di collaudo. 
Questo sistema e' estremamente elastico poiche' permette il collaudo tramite metodologie legacy waterfall basate su test UAT sia collaudi totalmente automatizzati basati su Unit Tests e/o End-to-End.

Di norma comunque il rilascio partira' dal branch master che nella maggior parte dei casi sara' seguito dal tag annotato della release. `git tag -a <tag>`

## Tips and Tricks

- Modificare l'author del commit precedente senza modificare il mesaggio: `git commit --amend --author="prossi <paolo.rossi@sindria.org>" --no-edit`
- Visualizzare la log history completa: `git log --oneline --decorate --all --graph`
- Visualizzare lo stato di tutti i file: `git status -vu`
- Visualizzare il diff solo dei file staged: `git diff --staged`
- Visualizzare il diff dell'ultimo commit: `git diff HEAD^`
- Visualizzare il diff con un commit qualsiasi: `git diff <commit_id>`
- Visualizzare il diff con un tool esterno: `git difftool HEAD`
- Tornare a un commit precedente in modo permanente: `git reset --hard <commit-hash>`

N.B. ATTENZIONE! QUEST'UTLTIMO DISTRUGGERA' TUTTE LE MODIFICHE LOCALI. NON ESEGUIRLO SE HAI MODIFICHE LOCALI NON COMMITTATE DA PRESERVARE. 


## Links

- [Pagina WiKipedia](https://en.wikipedia.org/wiki/Git)
- [Slide Corso](https://www.slideshare.net/jomikr/quick-introduction-to-git)
- [Documentazione](https://git-scm.com/doc)
- [Conferenza di approfondimento](https://www.youtube.com/watch?v=duqBHik7nRo)