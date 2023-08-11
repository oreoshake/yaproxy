#!/bin/bash
# Script for testing installing add-ons
# Attempts to install all YAP add-ons and should then exit
# Will fail if this takes longer than the specified number of minutes

timeout 10m /yap/yap.sh -addoninstallall -cmd

exit $?
