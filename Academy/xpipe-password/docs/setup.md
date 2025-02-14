# SpringBoot development environment setup


When adding a feature to XPipe, clone the `springboot27-starter`
repo at https://github.com/lucapitzoi/springboot27-starter to use
as a template project to implement a service.
`git clone git@github.com:lucapitzoi/springboot27-starter.git`

Move into the cloned repo, and follow the steps below to continue the setup

## Env setup

- Create a copy of the .env.local file, named .env `cp .env.local .env`
- Open **.env** and modify the split path at lines 6 and 48 according to your project path.
- Modify the Network Configuration to make sure the service is using free network addresses

Network addresses use the following standard:

since this is a /24 network, we only take the last byte into consideration to subdivide the network

- xx.xx.xx.1 - xx.xx.xx.99 are used by Core services
- xx.xx.xx.100 - xx.xx.xx.199 are used by non-Core services
- xx.xx.xx.200 and on are used by databases, the last digit of the last digit of the address 
of a given database should match the number of the address of its service.

Example: `172.16.10.6` : `172.16.10.206`

## IDE setup


### Build setup

- Open IntelliJ settings, expand the **Build, Execution, Deployment** tab
- Expand the **Build Tools** tab
- Select the **Maven** tab
- Select the override next to **Local repository**
- Modify the Local repository path to `/Users/User/Projects/XPipe/Category/xpipe-featurename/src/.m2/repository`
- Apply and save changes
- Select the three dots next to the debug icon at the top
- Select Edit...
- Select the + icon in the top right corner of the window, and choose BashSupportPro
- Set the configuration name as "Build"
- Under **Source**, modify the script file path to `/Users/User/Projects/XPipe/Category/xpipe-featurename/src/bin/build.sh`
- Apply and save changes


### JDK setup

- Select **File** from the title bar, select **Project Structure**
- On the Project tab, set the **SDK** as a **Java 17 SDK** (whichever one is available, preferably from Oracle)
- Set the language level as **17, Sealed types, always-strict floating-point semantics**
- Apply and save changes


### Docker container setup

- Go to the project's root directory `/Users/User/Projects/XPipe/Category/xpipe-featurename/`
- Create a copy of the docker-compose.local.yml file, named docker-compose.yml `cp docker-compose.local.yml docker-compose.yml`
- Open **docker-compose.yml** and make sure the ports used are not occupied.
- Set the network name at line 59 as `"vpc_xpipe"`
- Head back to the IDE, open a terminal window to work with the container
- Run `docker compose up -d` to build the containers for the service
- Run `docker ps` to check if all the containers have been created correctly (in case there were any mistakes,
run `docker compose down` to destroy the containers)
- To check if the containers are up, run `docker ps`
- Enter the service's container by running `docker exec -it <container name> bash`
- To run the application inside the container, run `java -jar target/<jar file name>`

\
\
\
todo:\
improve db-service address convention description