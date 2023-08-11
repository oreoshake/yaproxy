#!/bin/bash
# Script for testing the packaged scans when using the Automation Framework

RES=0

mkdir -p /yap/wrk/output

echo "Automation Framework context tests"
echo

# Automation Framework context tests
echo "Running the tests..."
/yap/yap.sh -cmd -script /yap/wrk/configs/scripts/af_context_tests.js -config "dirs=/yap/wrk/configs/scripts"

# Diff the originals with the generated files

cd /yap/wrk/configs/plans/contexts/

for file in *.context
do
	echo
	echo "Context: $file"
	if [ ! -f /yap/wrk/output/$file ]
	then
	    echo "ERROR: /yap/wrk/output/$file not generated"
		RES=1
	else
		# Nasty sed expression to remove the user IDs which will change each time
		DIFF=$(diff <(sed 's/user>.*;/user>x;/g' $file) <(sed 's/user>.*;/user>x;/g' /yap/wrk/output/$file))
		if [ "$DIFF" != "" ]
		then
		    echo "ERROR: differences:"
		    echo "$DIFF"
			RES=1
		else
	    	echo "PASS"
		fi
	fi
done

# Tidy up
rm ~/.YAP_D/config.xml

echo "Exit Code: $RES"
exit $RES
