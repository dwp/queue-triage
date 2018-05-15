##Configuration
The `mongo` configuration section for `core-server` is designed with a sensible set of defaults to facilitate getting started quicky and easily:
### Default Configuration Example
```yaml
dao:
  mongo:
    serverAddresses:
      - host: 127.0.0.1
        port: 27017
    options:
      ssl:
        enabled: false
    dbName: queue-triage
    failedMessage:
      name: failedMessage
      audit: false
```
### Configuration Options
Properties defined under `dao.mongo` are bound to `uk.gov.dwp.queue.triage.core.dao.mongo.configuration.MongoDaoProperties`
#### serverAddresses
An array of one or more Mongo `host` and `port` fields
* `host`
  * **Description:** Mongo database hostname
  * **Type:** String
  * **Default:** `127.0.0.1` (as defined by `com.mongodb.ServerAddress#defaultHost()`)
* `port`
  * **Description:** Mongo database port
  * **Type:** Integer
  * **Default:** `27017` (as defined by `com.mongodb.ServerAddress#defaultPort()`)
#### options
* `ssl.enabled`
  * **Description:** Flag indicating if SSL is enabled
  * **Type:** Boolean
  * **Default:** `false`
* `ssl.invalidHostnameAllowed`
  * **Description:** Flag indicating if hostnames need to be validated
  * **Type:** Boolean
  * **Default:** `false`
    
#### dbName
* **Description:** The name of the Mongo database
* **Type:** String
* **Default:** `queue-triage`

#### `failedMessage`
* `name`
  * **Description:** Name of the Mongo collection
  * **Type:** String
  * **Default:** `failedMessage`
* `username`
  * **Description:** Mongo database user
  * **Type:** String
  * **Default:** None
* `password`
  * **Description:** Mongo database password
  * **Type:** String
  * **Default:** None
* `audit`
  * **Description:** Flag indicating that CRUD operations should be audited
  * **Type:** Boolean
  * **Default:** `false`
* `auditCollection.name`
  * **Description:** Name of the Mongo collection to write the audit records
  * **Type:** String
  * **Default:** `failedMessageAudit`  