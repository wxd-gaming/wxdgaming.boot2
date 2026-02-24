#!/bin/bash


MEM=700m
WK=`pwd`
JAR=`pwd`/bootstrap.jar
PID=`ps -ef | grep ${JAR} | grep -v grep | awk '{print $2}'`

mkdir -p target

exists()
{
	PID=`ps -ef | grep ${JAR} | grep -v grep | awk '{print $2}'`
		if [ ${#PID} -ne 0 ]; then
			return 1
		fi
	return 0
}

start()
{
	echo "startting"
	exists
	if [ $? -ne 0 ]; then
		echo $"failed $prog: " /bin/false
		exit 1
	fi

	JAVA_PARAM="-Dw.k=${WK} -Xms${MEM} -Xmx${MEM} -Xss512k -Dfile.encoding=UTF-8 -Dsun.jnu.encoding=UTF-8 -Dlogback.configurationFile=./logback.xml -XX:CICompilerCount=4 -XX:-OmitStackTraceInFastThrow -XX:MaxDirectMemorySize=128m -XX:MaxGCPauseMillis=100 -Djdk.attach.allowAttachSelf=true -XX:+UseZGC -XX:+ZGenerational -XX:-ZUncommit -Xlog:gc*:target/gc.log:time,level,tags -XX:ConcGCThreads=2 -XX:+UseDynamicNumberOfGCThreads -server -jar"

	nohup /usr/local/openjdk-21/bin/java ${JAVA_PARAM} ${JAR} > nohup.out 2>&1 &

	echo $"sucess, java process start $prog: " /bin/true
	return 0
}

stop()
{
	echo "stopping"
	exists
	if [ $? -ne 0 ]; then
		kill -15 ${PID}
	fi

  for i in $(seq 1 1000); do
    exists
    if [ $? -ne 0 ]; then
      echo "java process is exists, ${i} try again after 1 sec..."
      sleep 2
    else
      break
    fi
  done

	exists
	if [ $? -ne 0 ]; then
		echo $"failed $prog: " /bin/false
		exit 1
	fi
  echo $"sucess $prog: " /bin/true
	return 0
}

restart()
{
	stop
	if [ $? -eq 0 ]; then
		start
	fi
}

###########################################
if [ $# -ne 1 ]; then
	echo "$0 start|restart|stop"
	exit
fi

case "$1" in
"start") start
exit
;;
"restart") restart
exit
;;
"stop") stop
exit
;;
*) echo "$0 start|restart|stop"
exit
;;
esac
