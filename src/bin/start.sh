cd `dirname $0`
CLASSPATH=./
for jar in `ls lib/*.jar`
do
      CLASSPATH="$CLASSPATH:""./$jar"
done

if [ ! -d "./log" ]; then  
mkdir "./log"  
fi  

#java  -Xms2048M -Xmx4096M -XX:PermSize=64M -XX:MaxPermSize=256M -cp $CLASSPATH com.weyao.main.SrvTimerTaskMain $1 >> log/log.txt 2>&1 &
java  -Xms2048M -Xmx4096M -XX:PermSize=64M -XX:MaxPermSize=256M -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/tmp/heapdump.hprof -XX:OnOutOfMemoryError="sh /services/timer/srv-timmer-0.0.1/jvm_cleanup.sh" -cp $CLASSPATH com.weyao.main.SrvTimerTaskMain $1 >> log/log.txt 2>&1 &
