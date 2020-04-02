package com.nokia.ca4ci.testing

def copydataforproduct() {
	echo "****************** copydata ***********************"
	
	sh '''
		hactive=`grep 'hactive' ${WORKSPACE}/IP.txt |cut -d':' -f3 |sed 's/ +//g'`
		echo $hactive
		hactive=`grep 'hactive' ${WORKSPACE}/IP.txt |cut -d':' -f3 |sed 's/ +//g'`
		echo $hactive
		sactive=`grep 'sactive' ${WORKSPACE}/IP.txt |cut -d':' -f3 |sed 's/ +//g'`
		echo $sactive

		sactivepvtip=`grep 'sactive' ${WORKSPACE}/IP.txt |cut -d':' -f2 |sed 's/ +//g'`
		echo $sactivepvtip
		hactivepvtip=`grep 'hactive' ${WORKSPACE}/IP.txt |cut -d':' -f2 |sed 's/ +//g'`
		echo $hactivepvtip

		sed -i "s/value=\"hactiveip\"/value=\"$hactive\"/g" ${WORKSPACE}/ContinuousIntegration/buildscripts/install/oneclickinstallation/DataLoading/JenkinsFarm/deploy_product_SP4.xml
		sed -i "s/value=\"sactiveip\"/value=\"$sactive\"/g" ${WORKSPACE}/ContinuousIntegration/buildscripts/install/oneclickinstallation/DataLoading/JenkinsFarm/deploy_product_SP4.xml


		sed -i "s/hactive=\"hactivepvtip\"/hactive=\"$hactivepvtip\"/g" ${WORKSPACE}/ContinuousIntegration/buildscripts/install/oneclickinstallation/DataLoading/JenkinsFarm/Copy_scripts_and_data_product_SP4.sh
		sed -i "s/sactive=\"sactivepvtip\"/sactive=\"$sactivepvtip\"/g" ${WORKSPACE}/ContinuousIntegration/buildscripts/install/oneclickinstallation/DataLoading/JenkinsFarm/Copy_scripts_and_data_product_SP4.sh

		sed -i "s/hactive=\"hactivepvtip\"/hactive=\"$hactivepvtip\"/g" ${WORKSPACE}/ContinuousIntegration/buildscripts/install/oneclickinstallation/DataLoading/JenkinsFarm/copy_scripts_data_newdata.sh
		sed -i "s/sactive=\"sactivepvtip\"/sactive=\"$sactivepvtip\"/g" ${WORKSPACE}/ContinuousIntegration/buildscripts/install/oneclickinstallation/DataLoading/JenkinsFarm/copy_scripts_data_newdata.sh
		scp -i ${key} -o 'StrictHostKeyChecking no' ${WORKSPACE}/ContinuousIntegration/buildscripts/install/oneclickinstallation/DataLoading/JenkinsFarm/*.sh root@$sactive:/tmp

		ssh -o 'StrictHostKeyChecking no' -i ${key} root@$sactive "dos2unix /tmp/copy_scripts_data_newdata.sh"

		ssh -o 'StrictHostKeyChecking no' -i ${key} root@$sactive "sh -x /tmp/copy_scripts_data_newdata.sh"

		ssh -o 'StrictHostKeyChecking no' -i ${key} root@$sactive "chown -R ngdb:ninstall /mnt/staging/import/"

		## This execute shell is to set server side sshd parameters to keep ssh connection Alive for 24 hours and restart of ##sshd service

		ssh -o 'StrictHostKeyChecking no' -i ${key} root@$sactive "sed -i 's/ClientAliveCountMax 0/ClientAliveCountMax 720/g' /etc/ssh/sshd_config"
		ssh -o 'StrictHostKeyChecking no' -i ${key} root@$sactive "sed -i '/#ClientAliveInterval/aClientAliveInterval 120' /etc/ssh/sshd_config"

		## Restarting sshd service

		#ssh root@$sactive "/sbin/service sshd restart"
		#ssh root@$sactive "/etc/init.d/sshd restart"

		ssh -o 'StrictHostKeyChecking no' -i ${key} root@$sactive "systemctl restart systemd-logind"
		ssh -o 'StrictHostKeyChecking no' -i ${key} root@$sactive "systemctl restart sshd"


'''
}



def Update_Boundary_and_Retention() {
	sh '''
	ant -f ContinuousIntegration/buildscripts/install/oneclickinstallation/DataLoading/JenkinsFarm/deploy_product_SP4.xml set-up-boundaries-and-retention
	'''
	
}

def CEI_Parallel_insert_Workaround() {
	sh '''
	sactive=`grep 'sactive' ${WORKSPACE}/IP.txt |cut -d':' -f3 |sed 's/ +//g'`
	echo $sactive

	ssh -o 'StrictHostKeyChecking no' -i ${key} root@$sactive "cd  /tmp/ngdb/RPMs; rpm -ivh /tmp/ngdb/RPMs/NSN-NGDB-RobotInstaller-*"
	ssh -o 'StrictHostKeyChecking no' -i ${key} root@$sactive "cd  /tmp/ngdb/RPMs; rpm -ivh /tmp/ngdb/RPMs/NOKIA-CEMOD-ADAPTATION-TEST-AUTO-GENX-*"

	ssh -o 'StrictHostKeyChecking no' -i ${key} root@$sactive "dos2unix /opt/nsn/robot/ngdb/TestAutomation/Adaptation/SCRIPTS/*"
	ssh -o 'StrictHostKeyChecking no' -i ${key} root@$sactive "mkdir -p /home/ngdb/dp"
	ssh -o 'StrictHostKeyChecking no' -i ${key} root@$sactive "cp /opt/nsn/ngdb/ContentAndAdaptation/*/*.xlsm /home/ngdb/dp/"

	ssh -o 'StrictHostKeyChecking no' -i ${key} root@$sactive "sh /opt/nsn/robot/ngdb/TestAutomation/Adaptation/SCRIPTS/adaptation_excel_property_updater.sh DISABLE-PARALLELINSERT /home/ngdb/dp CEI2_DATA_DP.xlsm"
	ssh -o 'StrictHostKeyChecking no' -i ${key} root@$sactive "sh /opt/nsn/robot/ngdb/TestAutomation/Adaptation/SCRIPTS/adaptation_excel_property_updater.sh DISABLE-PARALLELINSERT /home/ngdb/dp CEI2_RADIO_DP.xlsm"
	ssh -o 'StrictHostKeyChecking no' -i ${key} root@$sactive "sh /opt/nsn/robot/ngdb/TestAutomation/Adaptation/SCRIPTS/adaptation_excel_property_updater.sh DISABLE-PARALLELINSERT /home/ngdb/dp CEI2_SMS_DP.xlsm"
	ssh -o 'StrictHostKeyChecking no' -i ${key} root@$sactive "sh /opt/nsn/robot/ngdb/TestAutomation/Adaptation/SCRIPTS/adaptation_excel_property_updater.sh DISABLE-PARALLELINSERT /home/ngdb/dp CEI2_VOICE_CS_DP.xlsm"
	ssh -o 'StrictHostKeyChecking no' -i ${key} root@$sactive "sh /opt/nsn/robot/ngdb/TestAutomation/Adaptation/SCRIPTS/adaptation_excel_property_updater.sh DISABLE-PARALLELINSERT /home/ngdb/dp CEI2_VOLTE_DP.xlsm"
	ssh -o 'StrictHostKeyChecking no' -i ${key} root@$sactive "sh /opt/nsn/robot/ngdb/TestAutomation/Adaptation/SCRIPTS/adaptation_excel_property_updater.sh DISABLE-PARALLELINSERT /home/ngdb/dp CEI2_APPCAT_TYPE.xlsm"
	ssh -o 'StrictHostKeyChecking no' -i ${key} root@$sactive "sh /opt/nsn/robot/ngdb/TestAutomation/Adaptation/SCRIPTS/adaptation_excel_property_updater.sh DISABLE-PARALLELINSERT /home/ngdb/dp CEI2_APP_SUBS.xlsm"
	ssh -o 'StrictHostKeyChecking no' -i ${key} root@$sactive "sh /opt/nsn/robot/ngdb/TestAutomation/Adaptation/SCRIPTS/adaptation_excel_property_updater.sh DISABLE-PARALLELINSERT /home/ngdb/dp CEI2_O_INDEX.xlsm"
	ssh -o 'StrictHostKeyChecking no' -i ${key} root@$sactive "sh /opt/nsn/robot/ngdb/TestAutomation/Adaptation/SCRIPTS/adaptation_excel_property_updater.sh DISABLE-PARALLELINSERT /home/ngdb/dp CEI2_O_INDEX_CITY.xlsm"
	ssh -o 'StrictHostKeyChecking no' -i ${key} root@$sactive "sh /opt/nsn/robot/ngdb/TestAutomation/Adaptation/SCRIPTS/adaptation_excel_property_updater.sh DISABLE-PARALLELINSERT /home/ngdb/dp CEI2_BB.xlsm"
	'''
	
}	
def Load_Dimensuon_Tables() {
	sh '''
	ant -f ContinuousIntegration/buildscripts/install/oneclickinstallation/DataLoading/JenkinsFarm/deploy_product_SP4.xml load-dimension-tables
	'''
	
}

def Start_Topology() {
	sh '''
	ant -f ContinuousIntegration/buildscripts/install/oneclickinstallation/DataLoading/JenkinsFarm/deploy_product_SP4.xml run_script_for_topology_usage_loading_kafka_with_reduced_dataset
	'''
	
}
def Start_Data_Aggregation() {
	sh '''
	ant -f ContinuousIntegration/buildscripts/install/oneclickinstallation/DataLoading/JenkinsFarm/deploy_product_SP4.xml run-script-for-data-aggregation
	'''
	
}
def Enable_adqm_and_reload_data() {
	sh '''
	ant -f ContinuousIntegration/buildscripts/install/oneclickinstallation/DataLoading/JenkinsFarm/deploy_product_SP4.xml enable-adqm_and_reload_data
	'''
	
}
def Disable_adqm() {
	sh '''
	ant -f ContinuousIntegration/buildscripts/install/oneclickinstallation/DataLoading/JenkinsFarm/deploy_product_SP4.xml disable-adqm
	'''
	
}
def Enable_timezone_for_tnp() {
	sh '''
	ant -f ContinuousIntegration/buildscripts/install/oneclickinstallation/DataLoading/JenkinsFarm/deploy_product_SP4.xml enable-timezone-for-tnp
	'''
	
}
def Update_boundary_and_Retention_for_TNP() {
	sh '''
	ant -f ContinuousIntegration/buildscripts/install/oneclickinstallation/DataLoading/JenkinsFarm/deploy_product_SP4.xml update_boundary_and_Retention_for_TNP
	'''
	
}
def Run_tnp_jobs() {
	sh '''
	ant -f ContinuousIntegration/buildscripts/install/oneclickinstallation/DataLoading/JenkinsFarm/deploy_product_SP4.xml run_tnp_jobs
	'''
	
}
def Copy_data_for_yesterday() {
	sh '''
	ant -f ContinuousIntegration/buildscripts/install/oneclickinstallation/DataLoading/JenkinsFarm/deploy_product_SP4.xml copy_data_for_yesterday
	'''
	
}
def Enable_export_to_excel() {
	sh '''
	
	portal=`grep 'portal1' ${WORKSPACE}/IP.txt |cut -d':' -f3 |sed 's/ +//g'`
	echo $portal

	scp -o 'StrictHostKeyChecking no' ${WORKSPACE}/DataLoading/Enabling_export_to_excel_option.sh root@${portal}:/home/portal/
	scp -o 'StrictHostKeyChecking no' ${WORKSPACE}/DataLoading/Widgets.txt root@${portal}:/home/portal/
	ssh -o 'StrictHostKeyChecking no' -i ${key} root@${portal} "chmod 777 /home/portal/Enabling_export_to_excel_option.sh  /home/portal/Widgets.txt"
	ssh -o 'StrictHostKeyChecking no' -i ${key} root@${portal} "dos2unix /home/portal/Enabling_export_to_excel_option.sh"
	ssh -o 'StrictHostKeyChecking no' -i ${key} root@${portal} "dos2unix /home/portal/Widgets.txt"
	ssh -o 'StrictHostKeyChecking no' -i ${key} root@${portal} "sh -x /home/portal/Enabling_export_to_excel_option.sh /home/portal/Widgets.txt"
	'''
	
}