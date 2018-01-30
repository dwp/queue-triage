# queue-triage

An application to manage dead lettered messages across multiple brokers

## Building [![Build Status](https://travis-ci.org/dwpdigitaltech/queue-triage.svg?branch=master)](https://travis-ci.org/dwpdigitaltech/queue-triage)
* JDK 8
* Mongo 3.2+
* [Buck](https://buckbuild.com/).  Please refer to the [Getting Started Guide]() for installation and usage of Buck.


To configure the project for IntelliJ run:

```
./project
```

To create the relevant Mongo Roles and Users execute the following (this assumes Mongo is running locally on port 27017 and an `admin` user exists with password `Passw0rd`.  These values can be overridden using environment variables, see [mongo-queue-triage-roles.sh](core/dao-mongo/src/main/resources/mongo-queue-triage-roles.sh) or [mongo-queue-triage-users.sh](core/dao-mongo/src/main/resources/mongo-queue-triage-users.sh)):
```bash
buck build //core/dao-mongo:create-users-and-roles
```

To build and test all the modules run the following commands:

```bash
buck test --all --exclude component-test
buck test --all --include component-test
```

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
Your organisation may not allow direct access to the Central Maven repository (https://repo1.maven.org/maven2), if this is the case you will need to create a second "local" configuration file (filename: `.buckconfig.local`) in the project's root directory and add a `[maven_repositories]` section.  NOTE: This file **should not** be under version-control.

For example, to configure Buck to use the local repo <https://repo.internal.com/maven2> add the following section to `.buckconfig.local`:
```
[maven_repositories]
  internal = https://repo.internal.com/maven2
```