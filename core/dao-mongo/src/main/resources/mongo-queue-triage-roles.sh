#!/usr/bin/env python

import mongoUtils

parser = mongoUtils.create_default_argument_parser()

options = parser.parse_args()

mongoUtils.execute_mongo_command(options, """\
db.dropRole("failedMessageReadOnly")
db.createRole({
  role: "failedMessageReadOnly",
    privileges: [
      { resource: { db: "%s", collection: "failedMessage" }, actions: [ "find" ] }
    ],
    roles: []
})""" % options.appDb )

mongoUtils.execute_mongo_command(options, """\
db.dropRole("failedMessageReadWrite")
db.createRole({
  role: "failedMessageReadWrite",
  privileges: [
    { resource: { db: "%s", collection: "failedMessage" }, actions: [ "update", "insert", "remove" ] }
  ],
  roles: [
    { role: "failedMessageReadOnly", db: "%s" }
  ]
})""" % (options.appDb, options.appDb) )

mongoUtils.execute_mongo_command(options, """\
db.dropRole("failedMessageAuditReadOnly")
db.createRole({
  role: "failedMessageAuditReadOnly",
    privileges: [
      { resource: { db: "%s", collection: "failedMessageAudit" }, actions: [ "find" ] }
    ],
    roles: []
})""" % options.appDb )

mongoUtils.execute_mongo_command(options, """\
db.dropRole("failedMessageAuditReadWrite")
db.createRole({
  role: "failedMessageAuditReadWrite",
  privileges: [
    { resource: { db: "%s", collection: "failedMessageAudit" }, actions: [ "update", "insert", "remove" ] }
  ],
  roles: [
    { role: "failedMessageAuditReadOnly", db: "%s" }
  ]
})""" % (options.appDb, options.appDb) )

mongoUtils.execute_mongo_command(options, """\
db.dropRole("queueTriageAppRole")
db.createRole({
  role: "queueTriageAppRole",
  privileges: [],
  roles: [
    { role: "failedMessageReadWrite", db: "%s" },
    { role: "failedMessageAuditReadWrite", db: "%s" }
  ]
})""" % (options.appDb, options.appDb) )
