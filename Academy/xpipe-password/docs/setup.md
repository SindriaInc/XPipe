# SpringBoot development environment setup


When adding a feature to XPipe, clone the `springboot27-starter`
repo at https://github.com/lucapitzoi/springboot27-starter to use
as a template project to implement a solution.

Move into the cloned repo, and follow the steps below to continue the setup

## Config setup

- Create a copy of the `.env.local` file, name it `.env`.
- Open `.env` and modify the split path at lines 6 and 48 according to your project path.
- Modify the Network Configuration to make sure the service is using free network addresses
- Create a copy of the `docker-compose.local.yml` file, name it `docker-compose.yml`
- Open `docker-compose.yml` and make sure the ports used are not occupied.
- Set the network name at line 59 as `"vpc_xpipe"`

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
- Set the language level as 17, Sealed types, always-strict floating-point semantics
- Apply and save changes


### Docker container setup

- h

## Git

- a
