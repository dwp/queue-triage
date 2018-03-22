# queue-triage

An application to manage dead lettered messages across multiple brokers

## Building [![Build Status](https://travis-ci.org/dwpdigitaltech/queue-triage.svg?branch=master)](https://travis-ci.org/dwpdigitaltech/queue-triage)
* JDK 8
* Mongo 3.2+
* [Gradle](https://gradle.org/).  Please refer to the [Getting Started Guide](https://gradle.org/guides/#getting-started) for installation and usgae of Gradle.

### Setup
To configure the project for IntelliJ run:

```
./gradlew idea
```

To create the relevant Mongo Roles and Users execute the following (this assumes Mongo is running locally on port 27017 and an `admin` user exists with password `Passw0rd`.  These values can be overridden using environment variables, see [mongo-queue-triage-roles.sh](core/dao-mongo/src/main/resources/mongo-queue-triage-roles.sh) or [mongo-queue-triage-users.sh](core/dao-mongo/src/main/resources/mongo-queue-triage-users.sh)):
```bash
./gradlew createUsersAndRoles
```

### Testing
To test all the modules run the following commands:

```bash
./gradlew test
```
#### Testing - Configuration
The web component-test will run against a local firefox.  There are a number of options that can be configuration by adding passing in parameters to the build.

##### `browser`
Execute the selenium tests against a given browser
```bash
./gradlew test -Pbrowser=firefox|phantomjs
```
##### `remote_url`
Can be used to execute the selenium tests against a remote selenium instance
```bash
./gradlew test -Pselenium.remote_url=http://localhost:4444/wd/hub
```

### Starting
To run the `queue-triage-core-server` from the command line run:

```bash
./start.sh core
```

To run the `queue-triage-web-server` from the command line run:

```bash
./start.sh ldap
./start.sh web
```

### Troubleshooting
#### External Dependencies
Your organisation may not allow direct access to the Central Maven repository (https://repo1.maven.org/maven2), if this is the case you will need to create a second "local" 
configuration file (filename: `~/.gradle/init.gradle`) in the project's root directory and the following snippet to the `init.gradle` file:  
```
allprojects {
  repositories {
    maven {
	  url "https://repo.internal.com/maven2"
    }
  }
}
```
where <https://repo.internal.com/maven2> is your internal local repo location.

NOTE: This file **should not** be under version-control.


###Releasing
The axion-release plugin is used to apply semantic versioning to the project after successful builds. You can view more detailed 
documentation on their [website](http://axion-release-plugin.readthedocs.io/en/latest/index.html) but here is the TL;DR

To inspect what version your current codebase is running against:
```bash
./gradlew currentVersion
```

To release a new version of the committed and pushed code, then execute:
```bash
./gradlew release
```

If you'd like to test these options out, then you can run in dry-run mode:
```bash
./gradlew release -Prelease.dryRun
```

## Message Classification
Message Classification is really the core of the queue-triage application.

Once a messages has been Dead-Lettered by an Application, queue triage:
* De-queues the message
* Classifies the message using a set of Predicates
* Performs one or more Actions

For more information see [README.md](core/message-classification/README.md) in [core/message-classification](core/message-classification)