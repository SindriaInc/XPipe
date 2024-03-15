# JGitVer

jgitver consists of a set of library and plugins allowing to automatically compute project versions based on:

- git history

- git tags (annotated & lightweight)

- git branches

- configuration (predefined or explicit)


## Setup

0. Move into project root
1. Create maven dot folder: `mkdir -p .mvn`
2. create extensions.xml file: `touch .mvn/extensions.xml`
3. Copy and Paste the following content

## extensions.xml

```
<extensions xmlns="http://maven.apache.org/EXTENSIONS/1.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xsi:schemaLocation="http://maven.apache.org/EXTENSIONS/1.0.0 http://maven.apache.org/xsd/core-extensions-1.0.0.xsd">
  <extension>
    <groupId>fr.brouillard.oss</groupId>
    <artifactId>jgitver-maven-plugin</artifactId>
    <version>1.8.0</version>
  </extension>
</extensions>
```



4. create jgitver.config.xml file: `touch .mvn/jgitver.config.xml`
5. Copy and Paste the following content

## jgitver.config.xml

```
<configuration xmlns="http://jgitver.github.io/maven/configuration/1.1.0"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://jgitver.github.io/maven/configuration/1.1.0 https://jgitver.github.io/maven/configuration/jgitver-configuration-v1_1_0.xsd">
    <mavenLike>false</mavenLike>   <!-- deprecated, use 'strategy' instead -->
    <strategy>CONFIGURABLE</strategy>
    <policy>MAX</policy>    <!-- LookupPolicy to select the base tag/commit for the version computation -->
    <autoIncrementPatch>false</autoIncrementPatch>
    <useCommitDistance>false</useCommitDistance>
    <useDirty>false</useDirty>
    <useGitCommitId>false</useGitCommitId>
    <useSnapshot>false</useSnapshot> <!-- use -SNAPSHOT in CONFIGURABLE strategy -->
    <useDefaultBranchingPolicy>false</useDefaultBranchingPolicy>   <!-- uses jgitver#BranchingPolicy#DEFAULT_FALLBACK as fallback branch policy-->
</configuration>
```


6. edit current pom.xml file

## pom.xml

### project.version

```
<version>${jgitver.calculated_version}</version>
```

### project.plugins

```
<plugin>
        <artifactId>maven-antrun-plugin</artifactId>
        <executions>
          <execution>
            <phase>validate</phase>
            <goals>
                <goal>run</goal>
            </goals>
            <configuration>
                <tasks>
                    <!--suppress UnresolvedMavenProperty -->
                    <echo>used version: ${jgitver.used_version}</echo>
                    <!--suppress UnresolvedMavenProperty -->
                    <echo>version calculated: ${jgitver.calculated_version}</echo>
                    <!--suppress UnresolvedMavenProperty -->
                    <echo>dirty: ${jgitver.dirty}</echo>
                    <!--suppress UnresolvedMavenProperty -->
                    <echo>head_committer_name: ${jgitver.head_committer_name}</echo>
                    <!--suppress UnresolvedMavenProperty -->
                    <echo>head_commiter_email: ${jgitver.head_commiter_email}</echo>
                    <!--suppress UnresolvedMavenProperty -->
                    <echo>head_commit_datetime: ${jgitver.head_commit_datetime}</echo>
                    <!--suppress UnresolvedMavenProperty -->
                    <echo>git_sha1_full: ${jgitver.git_sha1_full}</echo>
                    <!--suppress UnresolvedMavenProperty -->
                    <echo>git_sha1_8: ${jgitver.git_sha1_8}</echo>
                    <!--suppress UnresolvedMavenProperty -->
                    <echo>branch_name: ${jgitver.branch_name}</echo>
                    <!--suppress UnresolvedMavenProperty -->
                    <echo>head_tags: ${jgitver.head_tags}</echo>
                    <!--suppress UnresolvedMavenProperty -->
                    <echo>head_annotated_tags: ${jgitver.head_annotated_tags}</echo>
                    <!--suppress UnresolvedMavenProperty -->
                    <echo>head_lightweight_tags: ${jgitver.head_lightweight_tags}</echo>
                    <!--suppress UnresolvedMavenProperty -->
                    <echo>base_tag: ${jgitver.base_tag}</echo>
                    <!--suppress UnresolvedMavenProperty -->
                    <echo>all_tags: ${jgitver.all_tags}</echo>
                    <!--suppress UnresolvedMavenProperty -->
                    <echo>all_annotated_tags: ${jgitver.all_annotated_tags}</echo>
                    <!--suppress UnresolvedMavenProperty -->
                    <echo>all_lightweight_tags: ${jgitver.all_lightweight_tags}</echo>
                    <!--suppress UnresolvedMavenProperty -->
                    <echo>all_version_tags: ${jgitver.all_version_tags}</echo>
                    <!--suppress UnresolvedMavenProperty -->
                    <echo>all_version_annotated_tags: ${jgitver.all_version_annotated_tags}</echo>
                    <!--suppress UnresolvedMavenProperty -->
                    <echo>all_version_lightweight_tags: ${jgitver.all_version_lightweight_tags}</echo>
                </tasks>
            </configuration>
          </execution>
        </executions>
      </plugin>
```

## Only for multi module projects (API)

### api/pom.xml

#### project.parent.version
```
<version>${project.version}</version>
```

### model/pom.xml
#### project.parent.version
```
<version>${project.version}</version>
```

### repository/pom.xml
#### project.parent.version
```
<version>${project.version}</version>
```

### service/pom.xml
#### project.parent.version
```
<version>${project.version}</version>
```

N.B. of course you can create maven dot folder and config files in the windows way :)

## Links

- [https://jgitver.github.io/](https://jgitver.github.io/)
- [https://github.com/jgitver/jgitver](https://github.com/jgitver/jgitver)
- [https://github.com/jgitver/jgitver-maven-plugin](https://github.com/jgitver/jgitver-maven-plugin)
