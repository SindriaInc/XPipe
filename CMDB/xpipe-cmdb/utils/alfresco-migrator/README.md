# Versioni di CMDBuild supportate

Come riportato nel file *pom.xml*, le versioni di CMDBuild supportate (quindi le librerie richieste) sono la 2.4.0, la 2.4.1 e la 2.4.2.

# Compilazione del progetto

```
mvn package
cd target
cp alfresco-migrator-bin $DESTINAZIONE
```

La directory *alfresco-migrator-bin* contiene:

* *config*, directory contenente un template di configurazione
* *lib*, directory contenente tutte le librerie necessarie

# Utilizzo del tool

Assumendo di trovarsi all'interno della directory *alfresco-migrator-bin* (ovunque sia stata copiata):

```
java -cp "lib/*" com.tecnoteca.cmdbuild.alfresco.migrator.Main --help
usage: java [options] com.tecnoteca.cmdbuild.alfresco.migrator.Main
 -f,--file <arg>             uses given file for properties
 -h,--help                   print this message
```

Attraverso l'utilizzo delle proprietà della VM di Java (*-D...*) è possibile ridefinire quanto specificato nel file di configurazione.

## Utilizzo del backend CMIS

Per utilizzare il backend CMIS è necessario creare delle funzioni SQL tramite lo script *needed_for_cmis.sql*.

## Esempi

### Copia standard

```
java -cp "lib/*" com.tecnoteca.cmdbuild.alfresco.migrator.Main --file $FILE
```

### Copia con ridefinizione di proprietà

Inclusione di classi:
```
java -cp "lib/*" -Dmigration.classes.source.include=foo,bar com.tecnoteca.cmdbuild.alfresco.migrator.Main --file $FILE
```

Esclusione di classi:
```
java -cp "lib/*" -Dmigration.classes.source.exclude=foo,bar com.tecnoteca.cmdbuild.alfresco.migrator.Main --file $FILE
```

Inclusione di id:
```
java -cp "lib/*" -Dmigration.ids.source.include=123,456 com.tecnoteca.cmdbuild.alfresco.migrator.Main --file $FILE
```

Esclusione di id:
```
java -cp "lib/*" -Dmigration.ids.source.exclude=123,456 com.tecnoteca.cmdbuild.alfresco.migrator.Main --file $FILE
```

Aggiornamento dei soli metadati, assumendo che i file siano già trasferiti in altro modo (ftp, smb, ...):
```
java -cp "lib/*" -Dmigration.updateonly=true com.tecnoteca.cmdbuild.alfresco.migrator.Main --file $FILE
```

Paginazione nella lettura delle card (es. 500 card alla volta) in caso di problemi con i web service:
```
java -cp "lib/*" -Dcmdbuild.soap.page.size=500 com.tecnoteca.cmdbuild.alfresco.migrator.Main --file $FILE
```

Mapping degli id da sorgente a destinazione quando gli id non corrispondono (da configurare anche la destinazione: cmdbuild.soap.destination.endpoint)
```
java -cp "lib/*" -Dmigration.idmapping=/tmp/test.csv com.tecnoteca.cmdbuild.alfresco.migrator.Main --file $FILE
``` il csv dovrà essere del tipo: pathsorgente/Id1234;pathdestinazione/Id5678 ad esempio Employee/CustomerEmployee/Id16652;Employee/InternalEmployee/Id17874
