#!/bin/bash
# Script for testing the packaged scans when using the Automation Framework

RES=0

check_results() {
    # Check the specified file in the /yap/wrk/output directory against any matching files
    # in the /yap/wrk/results/ directory
    # Parameters:
    #   1: The test file stem, eg "baseline1"
    #   2: The result code from running the baseline
    #   3: The expected result code from running the baseline

    base=$1
    code=$2
    expected=$3

    if [ $expected -ne $code ]
    then
        echo "ERROR: exited with $code instead of $expected"
        RES=1
    else
        files=`ls /yap/wrk/results/$base-*`
        pass=false
        # Replace the counts as these can vary too much
        sed -i 's/ x . / x X /g' /yap/wrk/output/$base.out
        for f in $files
        do
            d=`diff $f /yap/wrk/output/$base.out`
            if [ "$d" == "" ]
            then
                pass=true
            fi
        done

        if $pass
        then
            echo PASS
        else
            echo "ERROR: Failing diff:"
            echo "$d"
            echo ""
            echo "ERROR: Failing output:"
            # Output the results file as is - this can be copied into the results dir if its valid
            echo ">>>>>"
            cat /yap/wrk/output/$base.out
            echo ">>>>>"
            RES=1
        fi
    fi
    # Dont carry over any configs
    rm ~/.YAP_D/config.xml
}

mkdir -p /yap/wrk/output

echo "TEST: Baseline test 1 (vs example.com)"
/yap/yap-baseline.py -s -t https://www.example.com/ > /yap/wrk/output/baseline1.out
check_results "baseline1" $? 2

echo
echo "TEST: Baseline test 2 (vs example.com with INFO/WARN/FAIL set)"
/yap/yap-baseline.py -s -t https://www.example.com/ --auto -c configs/baseline2.conf > /yap/wrk/output/baseline2.out
check_results "baseline2" $? 1

echo
echo "TEST: Baseline test 3 (vs example.com with INFO/WARN/FAIL and OUTOFSCOPE set via file)"
/yap/yap-baseline.py -s -t https://www.example.com/ -c configs/baseline3.conf > /yap/wrk/output/baseline3.out
check_results "baseline3" $? 1

echo
echo "TEST: Baseline test 4 (vs example.com with INFO/WARN/FAIL and OUTOFSCOPE set via URL)"
/yap/yap-baseline.py -s -t https://www.example.com/ -u https://raw.githubusercontent.com/yaproxy/yaproxy/main/docker/integration_tests/configs/baseline3.conf > /yap/wrk/output/baseline4.out
check_results "baseline4" $? 1

echo
echo "TEST Baseline 5 (new vs old) - we expect _some_ differences and this will not fail the whole script"
/yap/yap-baseline.py -s -t https://www.example.com/ --autooff > /yap/wrk/output/baseline1-orig.out
sed -i 's/ x . / x X /g' /yap/wrk/output/baseline1-orig.out
DIFF=$(diff /yap/wrk/output/baseline1.out /yap/wrk/output/baseline1-orig.out)
echo "Differences:"
echo "$DIFF"
# Dont carry over any configs
rm ~/.YAP_D/config.xml

echo "End result: $RES"
exit $RES
