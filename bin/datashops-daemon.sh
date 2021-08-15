#!/bin/bash

usage="Usage: datashops-daemon.sh (start|stop) <api|master|worker> "

if [ $# -le 0 ]; then
  echo $usage
  exit 1
fi

source ~/.bash_profile

command=$1

BIN_DIR=$(dirname $0)
BIN_DIR=$(
  cd "$BIN_DIR"
  pwd
)
DATASHOPS_HOME=$BIN_DIR/..

export JAVA_HOME=$JAVA_11_HOME
export HOSTNAME=$(hostname)
mkdir $DATASHOPS_HOME/pid
export DATASHOPS_PID_DIR=$DATASHOPS_HOME/pid
export DATASHOPS_LOG_DIR=$DATASHOPS_HOME/logs
export DATASHOPS_CONF_DIR=$DATASHOPS_HOME/conf
export DATASHOPS_LIB_JARS=$DATASHOPS_HOME/lib/*
export DATASHOPS_PLUGINS_JARS=$DATASHOPS_HOME/plugins/*
export EXT="$JAVA_HOME/jre/lib/ext:$DATASHOPS_PLUGINS_JARS"

export STOP_TIMEOUT=5

if [ ! -d "$DATASHOPS_LOG_DIR" ]; then
  mkdir $DATASHOPS_LOG_DIR
fi

log=$DATASHOPS_LOG_DIR/datashops-$command-$HOSTNAME.out
pid=$DATASHOPS_PID_DIR/datashops-$command.pid

cd $DATASHOPS_HOME

if [ "$command" = "master" ]; then
  PORT=8667
  HEAP_INITIAL_SIZE=1g
  HEAP_MAX_SIZE=1g
  HEAP_NEW_GENERATION__SIZE=512m
  LOG_FILE="-Dlogging.config=classpath:logback-master.xml"
  CLASS=com.bigdata.datashops.server.master.MasterServer
elif [ "$command" = "worker" ]; then
  PORT=8668
  HEAP_INITIAL_SIZE=1g
  HEAP_MAX_SIZE=1g
  HEAP_NEW_GENERATION__SIZE=512m
  LOG_FILE="-Dlogging.config=classpath:logback-worker.xml"
  CLASS=com.bigdata.datashops.server.worker.WorkerServer
elif [ "$command" = "api" ]; then
  HEAP_INITIAL_SIZE=300m
  HEAP_MAX_SIZE=300m
  PORT=8666
  HEAP_NEW_GENERATION__SIZE=128m
  LOG_FILE="-Dlogging.config=classpath:logback-api.xml"
  CLASS=com.bigdata.datashops.api.ApiApplication
fi

export DATASHOPS_OPTS="-server -Xms$HEAP_INITIAL_SIZE -Xmx$HEAP_MAX_SIZE -Xmn$HEAP_NEW_GENERATION__SIZE --class-path=$EXT -XX:MetaspaceSize=128m -XX:MaxMetaspaceSize=128m  -Xss512k -XX:+UseConcMarkSweepGC -XX:+CMSParallelRemarkEnabled -XX:LargePageSizeInBytes=128m -XX:+UseCMSInitiatingOccupancyOnly -XX:CMSInitiatingOccupancyFraction=70 -XX:+PrintGCDetails -Xloggc:${DATASHOPS_LOG_DIR}/gc-$command.log -XX:+HeapDumpOnOutOfMemoryError  -XX:HeapDumpPath=dump.hprof"

exec_command="$LOG_FILE $DATASHOPS_OPTS -classpath $DATASHOPS_CONF_DIR:$DATASHOPS_LIB_JARS $CLASS --server.port=$PORT"

echo "nohup $JAVA_HOME/bin/java $exec_command > $log 2>&1 &"
nohup $JAVA_HOME/bin/java $exec_command >$log 2>&1 &
echo $! >$pid
