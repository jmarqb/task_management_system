#!/bin/bash

#IF DATABASE DOES NOT EXIST CREATE
if ! psql -U postgres -c "SELECT 1 FROM pg_database WHERE datname = 'authenticationDB'" | grep -q 1; then
  psql -U postgres -c "CREATE DATABASE authenticationDB;"
fi

if ! psql -U postgres -c "SELECT 1 FROM pg_database WHERE datname = 'projectDB'" | grep -q 1; then
  psql -U postgres -c "CREATE DATABASE projectDB;"
fi