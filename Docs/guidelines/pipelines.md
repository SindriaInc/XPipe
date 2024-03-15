# Pipelines

A DevOps pipeline is a set of practices that the development (Dev) and operations (Ops) teams implement to build, test, and deploy software faster and easier. One of the primary purposes of a pipeline is to keep the software development process organized and focused.

The term “pipeline” might be a bit misleading, though. An assembly line in a car factory might be a more appropriate analogy since software development is a continuous cycle.

Before the manufacturer releases the car to the public, it must pass through numerous assembly stages, tests, and quality checks. Workers have to build the chassis, add the motor, wheels, doors, electronics, and a finishing paint job to make it appealing to customers.

DevOps pipelines work similarly.

Before releasing an app or a new feature to users, you first have to write the code. Then, make sure that it does not lead to any fatal errors that might cause the app to crash. Avoiding such a scenario involves running various tests to fish out any bugs, typos, or mistakes. Finally, once everything is working as intended, you can release the code to users.

From this simplified explanation, you can conclude that a DevOps pipeline consists of the build, test, and deploy stages.

## Procedura standard

Si ricorda che la pipeline segue gli standard devops e si da per scontanto che si segua il flusso git. Per maggiori informazioni si rimanda alla guida [git](git.md).

Una volta approvate le merge request e mergiato il codice su master si puo' procedere alla creazione della release sotto forma di tag annotato. Questa procedura puo' essere fatta sia manualmente con l'apposito comando `git tag -a <release>` oppure tramite interfaccia web di gitlab.

Per semplificare faremo riferimento alla procedura web, di seguito alcune screenshot.







# Links

- [https://docs.gitlab.com/ee/ci/pipelines/](https://docs.gitlab.com/ee/ci/pipelines/)