#!/usr/bin/env python

import mongoUtils

parser = mongoUtils.create_default_argument_parser()
parser.add_argument('--appUser', help='username for the queue-triage application', default='failedMessageUser')
parser.add_argument('--appPassword', nargs='?', help='password for the application user', action=mongoUtils.PasswordAction, default='Passw0rd')

options = parser.parse_args()

mongoUtils.execute_mongo_command(options, """\
db.dropUser("%s")
db.createUser({
    user: "%s",
    pwd: "%s",
    roles: [
        "failedMessageReadWrite"
    ]
})""" % (options.appUser, options.appUser, options.appPassword))
