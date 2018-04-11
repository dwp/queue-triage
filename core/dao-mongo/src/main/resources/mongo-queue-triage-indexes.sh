#!/usr/bin/env python

import mongoUtils

parser = mongoUtils.create_default_argument_parser()

options = parser.parse_args()

mongoUtils.execute_mongo_command(options, """db.getCollection("failedMessage").createIndex({"statusHistory.0.status": 1, "destination.brokerName": 1})""")