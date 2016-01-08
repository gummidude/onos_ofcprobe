#/bin/bash!
#BenchmarkingHosts:
IPS[0]="172.16.45.17"
#IPS[1]="192.168.42.22"
#IPS[2]="192.168.42.31"
#IPS[3]="192.168.42.32"

EXPECTED_ARGS=2
if [ $# -ne $EXPECTED_ARGS ]
then
	echo "Usage: 'launch.sh runCount Controllername'"
	exit -1
fi

#scripts starting the controller
if [[ $2 == *"flood"* ]]
then
	controllerstart=flood/start.sh
fi

if [[ $2 == *"nox"* ]]
then
	controllerstart=nox/start.sh
fi

if [[ $2 == *"beacon"* ]]
then
        controllerstart=beacon/start.sh
fi

if [[ $2 == *"pox"* ]]
then
        controllerstart=pox/start.sh
fi

if [[ $2 == *"ryu"* ]]
then
        controllerstart=ryu/start.sh
fi

if [[ $2 == *"openD"* ]]
then
        controllerstart=openD/start.sh
fi

if [[ $2 == *"onos"* ]]
then
	controllstart=onos/start.sh
fi

controller=$2
echo ">>Killing Controller"
./stop_controller.sh
sleep 3s

maxswitchnum=4
if [ ${#IPS[@]} -eq 2 ]
then
	maxswitchnum=50
fi
if [ ${#IPS[@]} -eq 4 ]
then
        maxswitchnum=25
fi

#runArray=$(seq 5 5 $maxswitchnum)
#runArray=("1" "${runArray[@]}")
#runArray=("${runArray[@]}" "${runArray[@]}")
runArray=4

for i in $(seq 1 1 $1)
do
	#runs 1,5-maxswitchnum
	for j in ${runArray[@]}
	do
		echo ">Run #$i, Switch #$j"
		echo ">>Starting Controller"
		$controllerstart
		
		echo ">>Kill all alive BenchingTools"
		for ip in ${IPS[@]}
		do
			ssh onos@${ip} killall -9 java
		done
		sleep 30s
	
		echo ">>Start BenchingTools"
		./tcpdumper.sh $i\.pcap
		for ip in ${IPS[@]}
		do
			ssh onos@${ip} "~/ssh_redirector.sh $j"
		done
		sleep 50s
		
		echo ">>Kill Controller"
		./stop_controller.sh
		./tcpstopper.sh
		sleep 5s
		
		echo ">>Moving Logfiles"
		target=$(printf "%03d" $j)
		for ip in ${IPS[@]}
		do
			ssh onos@${ip} mv "/home/onos/ofcprobe/MyLog.log" "/home/onos/ofcprobe/statistics/"
		done
	done
	
	echo ">Moving statistics to statistics_$i"
	./tcpmover.sh $controller
	for ip in ${IPS[@]}
	do
		ssh onos@${ip}  mv "/home/onos/ofcprobe/statistics/" "/home/onos/ofcprobe/statistics_$i/"
	done
	
done

echo "Moving Statistics_* into new Dir and taring"
for ip in ${IPS[@]}
do
	ssh onos@${ip}  mkdir "/home/onos/ofcprobe/$controller"
	ssh onos@${ip}  mv "/home/onos/ofcprobe/statistics_*" "/home/onos/ofcprobe/$controller/"
#	ssh openflow@${ip}  tar czf "/home/openflow/ofcprobe_sdnflex/$controller.tar.gz" "/home/openflow/ofcprobe_sdnflex/$controller/" &
done
