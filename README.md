# queue-triage

An application to manage dead lettered messages across multiple brokers

## Building [![Build Status](https://travis-ci.org/dwpdigitaltech/queue-triage.svg?branch=master)](https://travis-ci.org/dwpdigitaltech/queue-triage)
queue-triage uses [Buck](https://buckbuild.com/) as a build tool.  Please refer to the [Getting Started Guide]() for installation and usage of Buck.

To configure the project for IntelliJ run:

```
buck project
```

To build and test all the modules run the following commands:

```bash
buck fetch //...
buck test //...
```

To run the `queue-triage-core-server` from the command line run:

```bash
buck run start-core
```

