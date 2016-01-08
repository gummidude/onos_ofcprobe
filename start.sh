#/bin/bash
#cd ~/controller/opendaylight/distribution/opendaylight/target/distribution.opendaylight-osgipackage/opendaylight/
#screen -AmdS openD bash -c './run.sh'
#ssh -t onos@172.16.45.16 "sudo service onos restart"
bash -c `onos-install -f $OC1`
