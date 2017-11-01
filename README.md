# queue-triage

An application to manage dead lettered messages across multiple brokers

## Building [![Build Status](https://travis-ci.org/dwpdigitaltech/queue-triage.svg?branch=master)](https://travis-ci.org/dwpdigitaltech/queue-triage)
queue-triage uses [Buck](https://buckbuild.com/) as a build tool.  Please refer to the [Getting Started Guide]() for installation and usage of Buck.

To configure the project for IntelliJ run:

```
./project
```

The `queue-triage-core-server` module uses Mongo to persist messages.  To create the relevant Mongo Roles and Users execute the following command (this assumes mongo is running locally on port 27017 and an `admin` user exists with password `Passw0rd`.  These values can be overridden using environment variables, see [mongo-queue-triage-roles.sh](core/dao-mongo/src/main/resources/mongo-queue-triage-roles.sh) or [mongo-queue-triage-users.sh](core/dao-mongo/src/main/resources/mongo-queue-triage-users.sh)):
```bash
buck build //core/dao-mongo:create-users-and-roles
```

To build and test all the modules run the following commands:

```bash
buck test //...
```

To run the `queue-triage-core-server` from the command line run:

```bash
buck run start-core
```

To run the `queue-triage-web-server` from the command line run:

```bash
buck run start-web
```
