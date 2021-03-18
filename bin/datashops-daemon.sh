#!/bin/sh

# if no args specified, show usage
if [ $# -le 1 ]; then
  echo $usage
  exit 1
fi

command=$1

BIN_DIR=$(dirname $0)
BIN_DIR=$(
  cd "$BIN_DIR"
  pwd
)
DATASHOPS_HOME=$BIN_DIR/..

source ~/.bash_profile

export JAVA_HOME=$JAVA_HOME
export HOSTNAME=$(hostname)

export DATASHOPS_PID_DIR=$DATASHOPS_HOME/pid
export DATASHOPS_LOG_DIR=$DATASHOPS_HOME/logs
export DATASHOPS_CONF_DIR=$DATASHOPS_HOME/conf
export DATASHOPS_LIB_JARS=$DATASHOPS_HOME/lib/*

export STOP_TIMEOUT=5

if [ ! -d "$DATASHOPS_LOG_DIR" ]; then
  mkdir $DATASHOPS_LOG_DIR
fi

log=$DATASHOPS_LOG_DIR/DATASHOPS-$command-$HOSTNAME.out
pid=$DATASHOPS_PID_DIR/DATASHOPS-$command.pid

cd $DATASHOPS_HOME

if [ "$command" = "master" ]; then
  HEAP_INITIAL_SIZE=1g
  HEAP_MAX_SIZE=1g
  HEAP_NEW_GENERATION__SIZE=512m
  LOG_FILE="-Dlogging.config=classpath:logback-master.xml"
  CLASS=com.bigdata.datashops.server.master.MasterServer
elif [ "$command" = "worker" ]; then
  HEAP_INITIAL_SIZE=1g
  HEAP_MAX_SIZE=1g
  HEAP_NEW_GENERATION__SIZE=512m
  LOG_FILE="-Dlogging.config=classpath:logback-worker.xml"
  CLASS=com.bigdata.datashops.server.worker.WorkerServer
elif [ "$command" = "api" ]; then
  HEAP_INITIAL_SIZE=512m
  HEAP_MAX_SIZE=512m
  HEAP_NEW_GENERATION__SIZE=128m
  LOG_FILE="-Dlogging.config=classpath:logback.xml"
  CLASS=com.bigdata.datashops.api.ApiApplication
fi

export DATASHOPS_OPTS="-server -Xms$HEAP_INITIAL_SIZE -Xmx$HEAP_MAX_SIZE -Xmn$HEAP_NEW_GENERATION__SIZE -XX:MetaspaceSize=128m -XX:MaxMetaspaceSize=128m  -Xss512k -XX:+UseParNewGC -XX:+UseConcMarkSweepGC -XX:+CMSParallelRemarkEnabled -XX:LargePageSizeInBytes=128m -XX:+UseCMSInitiatingOccupancyOnly -XX:CMSInitiatingOccupancyFraction=70 -XX:+PrintGCDetails -Xloggc:gc.log -XX:+HeapDumpOnOutOfMemoryError  -XX:HeapDumpPath=dump.hprof"

exec_command="$LOG_FILE $DATASHOPS_OPTS -classpath $DATASHOPS_CONF_DIR:$DATASHOPS_LIB_JARS $CLASS"

echo "nohup $JAVA_HOME/bin/java $exec_command > $log 2>&1 &"
nohup $JAVA_HOME/bin/java $exec_command >$log 2>&1 &
echo $! >$pid
