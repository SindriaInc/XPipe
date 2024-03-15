# SSH

SSH (Secure SHell) è un protocollo che permette di stabilire una sessione remota cifrata tramite interfaccia a riga di comando con un altro host di una rete informatica. È il protocollo che ha sostituito l'analogo, ma insicuro, Telnet.


## Chiavi

Come standard Sindria andremo a generare 2 chiavi ssh, una ad uso esclusivo di GIT (senza passphrase) e l'altra "cloud" (con passphrase) generica per l'accesso a tutte le macchine del cloud Sindria.

Ogni chiave come best practice seguira' la nomenclatura: `<utente>@<server>` es. `prossi@example.sindria.org`

### Setup

Per gli utenti windows e' fortemente consigliato il setup del [WSL](wsl.md) prima di procedere.

- Spostarsi nella home: `cd ~`
- Creare la directory: `mkdir -p .ssh`
- Settare i permessi: `chmod 700 .ssh`
- Spostarsi dentro la directory: `cd .ssh`

### Chiave GIT

- Spostarsi dentro la directory: `cd ~/.ssh`
- Generiamo la nuova chiave: `ssh-keygen`

alla richiesta del prompt inserire il nome della chiave con la seguente sintassi: `<utente>@git.sindria.org`

es. `prossi@git.sindria.org`

alla richiesta della passphrase premere invio 2 volte per skippare.

- Aggiungere estensione pem: `cp prossi@git.sindria.org prossi@git.sindria.org.pem`
- Ottenere la chiave pubblica: `cat prossi@git.sindria.org.pub`
- Inserire la chiave pubblica nel profilo gitlab: [https://git.sindria.org/profile/keys](https://git.sindria.org/profile/keys)

### Chiave Cloud

- Spostarsi dentro la directory: `cd ~/.ssh`
- Generiamo la nuova chiave: `ssh-keygen`

alla richiesta del prompt inserire il nome della chiave con la seguente sintassi: `<utente>@cloud.sindria.org`

es. `prossi@cloud.sindria.org`

alla richiesta della passphrase la inseriamo 2 volte ricordandoci di annotarla nel nostro password manager personale.

- Aggiungere estensione pem: `cp prossi@cloud.sindria.org prossi@cloud.sindria.org.pem`
- Ottenere la chiave pubblica: `cat prossi@cloud.sindria.org.pub`

N.B. questa chiave pubblica va inviata al DevOps di Sindria per l'abilitazione ai server.

### Configurazione

- Spostarsi dentro la directory: `cd ~/.ssh`
- Creare il file: `touch config`
- Settare i permessi: `chmod 600 config`

Inserire nel file config il blocco sottostante ricordandoci di sostituire l'utente `prossi` con il nostro utente.


File config:

```
Host *
     PubkeyAcceptedKeyTypes +ssh-dss

# Sindria Inc. Bitbucket
host bitbucket.org
     User prossi
     Port 22
     StrictHostKeyChecking no
     UserKnownHostsFile=/dev/null
     PreferredAuthentications publickey
     IdentityFile ~/.ssh/prossi@bitbucket.org.pem

# Sindria Inc. GitHub
host github.com
     User prossi
     Port 22
     StrictHostKeyChecking no
     UserKnownHostsFile=/dev/null
     PreferredAuthentications publickey
     IdentityFile ~/.ssh/prossi@github.com.pem

# Sindria Git
host git.sindria.org
     User prossi
     Port 22
     StrictHostKeyChecking no
     UserKnownHostsFile=/dev/null
     PreferredAuthentications publickey
     IdentityFile ~/.ssh/prossi@git.sindria.org.pem

# Sindria Cloud
host #fqdn
     User prossi
     Port 5872
     StrictHostKeyChecking no
     UserKnownHostsFile=/dev/null
     PreferredAuthentications publickey
     IdentityFile ~/.ssh/prossi@cloud.sindria.org.pem
```


