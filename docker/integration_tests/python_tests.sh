#!/bin/bash
# Script for testing python library dependencies

RES=0

echo "Check aws cli - should output version"
aws --version
RES=$?

echo "Check yap-cli - should output help"
yap-cli --help
if [ "$RES" -eq 0 ]
then
  RES=$?
fi

exit $RES
