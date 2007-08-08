#!/bin/sh
# Usage: newuser name password

psql template1 <<EOF
CREATE ROLE $1 LOGIN PASSWORD '$2'
   VALID UNTIL 'infinity'
   CONNECTION LIMIT 50
   IN ROLE offstageusers;

CREATE DATABASE $1
  WITH ENCODING='UTF8'
       OWNER=$1
       TEMPLATE=offstagetemplate1;
EOF

./initdb.sh $1
