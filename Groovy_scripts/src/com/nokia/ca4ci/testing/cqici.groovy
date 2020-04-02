package com.nokia.ca4ci.testing

def commondimension() {
	echo "****************** copydata ***********************"
	
	sh '''
		hactive=`grep 'hactive' ${WORKSPACE}/IP.txt |cut -d':' -f3 |sed 's/ +//g'`
		echo $hactive
		hactive=`grep 'hactive' ${WORKSPACE}/IP.txt |cut -d':' -f3 |sed 's/ +//g'`
		echo $hactive
		sactive=`grep 'sactive' ${WORKSPACE}/IP.txt |cut -d':' -f3 |sed 's/ +//g'`
		echo $sactive


		ssh -o 'StrictHostKeyChecking no' -i ${key} root@$sactive "cd  /tmp/ngdb/RPMs; rm -rf *ROBO*"
		##buildenv/utilities/collect_packages.sh -d$sactive:/tmp/ngdb/RPMs -p${BUILD_PROFILE} ROBO_TESTS/latest
		ContinuousIntegration/buildscripts/GITbuildenv/utilities/collect_packages.sh -d$sactive:/tmp/ngdb/RPMs -p${BUILD_PROFILE} ROBO_TESTS:latest

		ssh -o 'StrictHostKeyChecking no' -i ${key} root@$sactive "cd  /tmp/ngdb/RPMs; rpm -ivh /tmp/ngdb/RPMs/NSN-NGDB-RobotInstaller-*"
		ssh -o 'StrictHostKeyChecking no' -i ${key} root@$sactive "cd  /tmp/ngdb/RPMs; rpm -ivh /tmp/ngdb/RPMs/NSN-NGDB-ROBO-TESTS-*"
		ssh -o 'StrictHostKeyChecking no' -i ${key} root@$sactive "cd  /tmp/ngdb/RPMs; rpm -ivh /tmp/ngdb/RPMs/NSN-NGDB-ROBO-17SP3-*"
		ssh -o 'StrictHostKeyChecking no' -i ${key} root@$sactive "cd  /tmp/ngdb/RPMs; rpm -ivh /tmp/ngdb/RPMs/NSN-NGDB-ROBO-18MP1-*"

		ssh -o 'StrictHostKeyChecking no' -i ${key} root@$sactive "rpm -ivh --force /tmp/ngdb/RPMs/NSN-NGDB-ROBO-RI-*.noarch*"

'''
}



def Spare_Dim_Denorm_Update() {
	sh '''
	ssh -o 'StrictHostKeyChecking no' -i ${key} root@$sactive ". ~/.bash_profile; cd  /opt/nsn/robot/ngdb/SCRIPTS_GEN/SPARE_SCRIPTS; sh /opt/nsn/robot/ngdb/SCRIPTS_GEN/SPARE_SCRIPTS/denorm_scripts_17.sh" || true 
	'''
	
}

