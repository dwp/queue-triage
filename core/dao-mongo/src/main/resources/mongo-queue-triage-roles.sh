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
    { role: "failedMessageReadOnly", db: "queue-triage" }
  ]
})""" % options.appDb )
