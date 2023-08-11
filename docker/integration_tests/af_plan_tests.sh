#!/bin/bash
# Script for testing Automation Framework features

RES=0

mkdir -p /yap/wrk/output

echo "Automation Framework integration tests"
echo

cd /yap/wrk/configs/plans/

export JIGSAW_USER="guest"
export JIGSAW_PWORD="guest"

# Install dev add-on
/yap/yap.sh -cmd -addoninstall dev

summary="\nSummary:\n"

for file in *.yaml
do
	echo
	echo "Plan: $file"

    /yap/yap.sh -cmd -autorun /yap/wrk/configs/plans/$file -dev
    RET=$?

	if [ "$RET" != 0 ]
	then
	    echo "ERROR"
		summary="${summary}  Plan: $file\tERROR\n"
		RES=1
	else
    	echo "PASS"
		summary="${summary}  Plan: $file\tPASS\n"
	fi
    sleep 2
    # Tidy up
    rm ~/.YAP_D/config.xml
done

echo -e $summary
echo "Exit Code: $RES"
exit $RES
