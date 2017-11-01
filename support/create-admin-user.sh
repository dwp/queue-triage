#!/bin/bash

MONGO_DB_ADDRESS=${MONGO_DB_ADDRESS:-"localhost:27017/queue-triage"}

echo "Creating admin user in $MONGO_DB_ADDRESS"
mongo <<EOF
use admin
db.createUser({user:'admin',pwd:'Passw0rd',roles:['userAdminAnyDatabase']})
EOF