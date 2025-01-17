#!/bin/sh
#
# Startup script for the Webswing
#
# Customised for YAP running in Docker - just call with no parameters for webswing to start and leave
# the docker container running
#
# Set environment.
export HOME=/yap/webswing
export OPTS="-h 0.0.0.0 -j $HOME/jetty.properties -c $HOME/webswing.config"
export JAVA_OPTS="-Xmx128M"
export LOG=$HOME/webswing.out
export PID_PATH_NAME=$HOME/webswing.pid

if [ -z `command -v $0` ]; then
    CURRENTDIR=`pwd`
    cd `dirname $0` > /dev/null
    SCRIPTPATH=`pwd`/
    cd $CURRENTDIR
else
    SCRIPTPATH=""
fi

if [ ! -f $HOME/webswing-server.war ]; then
    echo "Webswing executable not found in $HOME folder"
    exit 1
fi

if [ ! -f $JAVA_HOME/bin/java ]; then
    echo "Java installation not found in $JAVA_HOME folder"
    exit 1
fi
if [ -z `command -v xvfb-run` ]; then
    echo "Unable to locate xvfb-run command. Please install Xvfb before starting Webswing."
    exit 1
fi
if [ ! -z `command -v ldconfig` ]; then
    if [ `ldconfig -p | grep -i libXext | wc -l` -lt 1 ]; then
        echo "Missing dependent library libXext."
        exit 1
    fi
    if [ `ldconfig -p | grep -i libxi | wc -l` -lt 1 ]; then
        echo "Missing dependent library libXi."
        exit 1
    fi
    if [ `ldconfig -p | grep -i libxtst | wc -l` -lt 1 ]; then
        echo "Missing dependent library libXtst"
        exit 1
    fi
    if [ `ldconfig -p | grep -i libxrender | wc -l` -lt 1 ]; then
        echo "Missing dependent library libXrender."
        exit 1
    fi
fi

# See how we were called - customised for YAP running in Docker
case "$1" in
    run)
        # Run Webswing server- expects X Server to be running
        # dont put into the background otherwise docker will exit
        cd $HOME

        # Set up the YAP runtime options
        YAP_OPTS="-host 0.0.0.0 -port 8090"
        YAP_PUBLIC="/yap/wrk/yap_root_ca.cer"
        YAP_PRIVATE="/yap/wrk/yap_root_ca.key"

        if [ ! -z "${YAP_WEBSWING_OPTS}" ]; then
          # Replace them with those set in the env var
          YAP_OPTS="${YAP_WEBSWING_OPTS}"
        elif [ -f ${YAP_PRIVATE} ]; then
          # Private cert is available, use that
          YAP_OPTS="${YAP_OPTS} -certload ${YAP_PRIVATE}"
        elif [ -w /yap/wrk ]; then
          # wrk directory is writable, output public and private certs
          YAP_OPTS="${YAP_OPTS} -certpubdump  ${YAP_PUBLIC} -certfulldump  ${YAP_PRIVATE}"
        fi

        echo "Using YAP command line options: ${YAP_OPTS}"
        # Use ; for sed separators so we can use the directory slashes
        sed -i "s;YAP_OPTS;${YAP_OPTS};" webswing.config

        $JAVA_HOME/bin/java $JAVA_OPTS -jar webswing-server.war $OPTS 2>> $LOG >> $LOG
        ;;
    *)
        xvfb-run $SCRIPTPATH$0 run
esac

exit 0
