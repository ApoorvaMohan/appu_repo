package com.nokia.ca4ci.install

def checkoutCIRepo() {
		echo "checking out CI scripts started"
		checkout changelog: false, poll: false, scm: [$class: 'GitSCM', branches: [[name: '*/master']], doGenerateSubmoduleConfigurations: false, extensions:                                                [
				[$class: 'RelativeTargetDirectory', relativeTargetDir: 'ContinuousIntegration'],
				[$class: 'SparseCheckoutPaths', sparseCheckoutPaths: [[path: 'buildscripts/install/oneclickinstallation/'], [path: 'collector']]],
				[$class: 'CleanBeforeCheckout'],
				[$class: 'ChangelogToBranch', options: [compareRemote: 'ContinuousIntegration', compareTarget: 'TEST1']],
				[$class: 'PathRestriction', excludedRegions: '.*', includedRegions: '']], submoduleCfg: [], userRemoteConfigs: [[credentialsId: 'd0413804-f035-4d68-8a7c-22385074a91a', url: 'ssh://bhgerrit.ext.net.nokia.com:8282/ContinuousIntegration']]]
				checkout changelog: false, poll: false, scm: [$class: 'GitSCM', branches: [[name: '*/master']], doGenerateSubmoduleConfigurations: false, extensions: [
								[$class: 'RelativeTargetDirectory', relativeTargetDir: 'devops_ci'], 
								[$class: 'SparseCheckoutPaths', sparseCheckoutPaths: [[path: 'scripts/buildscripts'], [path: 'collector'], [path: 'settings']]], 
								[$class: 'CleanBeforeCheckout'], 
								[$class: 'ChangelogToBranch', options: [compareRemote: 'devops_ci', compareTarget: 'TEST1']],
								[$class: 'PathRestriction', excludedRegions: '.*', includedRegions: '']], submoduleCfg: [], userRemoteConfigs: [[credentialsId: 'd0413804-f035-4d68-8a7c-22385074a91a', url: 'ssh://ca_cp@bhgerrit.ext.net.nokia.com:8282/CA4CI/devops_ci']]]
		echo "checking out CI scripts finished"
}

def checkoutComponent() {
		echo "checking out component started"
                env.componentRepoName = sh(returnStdout: true, script: 'echo $GIT_URL | gawk -F"/CA4CI/" \'{print $2}\'').trim()
		env.folderToBuild = componentRepoName + testcaseFolder
                echo "componentRepoName = $componentRepoName"
		echo "folderToBuild = $folderToBuild"
                checkout([$class: 'GitSCM', branches: [[name: branch]], doGenerateSubmoduleConfigurations: false, extensions: [[$class: 'CheckoutOption', timeout: 10], [$class: 'CloneOption', depth: 0, noTags: false, reference: '', shallow: false, timeout: 10],[$class: 'RelativeTargetDirectory', relativeTargetDir: componentRepoName]], submoduleCfg: [], userRemoteConfigs: [[credentialsId: 'd0413804-f035-4d68-8a7c-22385074a91a', url: env.GIT_URL]]])
                echo "checking out component finished"
}

def checkoutInventoryRepo() {
                echo "checking out inventory repo started"
		checkout changelog: false, poll: false, scm: [$class: 'GitSCM', branches: [[name: '*/master']], doGenerateSubmoduleConfigurations: false, extensions: [
                                                                [$class: 'RelativeTargetDirectory', relativeTargetDir: 'labs'],
                                                                [$class: 'SparseCheckoutPaths', sparseCheckoutPaths: [[path: 'inventory']]],
                                                                [$class: 'CleanBeforeCheckout'],
                                                                [$class: 'ChangelogToBranch', options: [compareRemote: 'labs', compareTarget: 'TEST1']],
                                                                [$class: 'PathRestriction', excludedRegions: '.*', includedRegions: '']], submoduleCfg: [], userRemoteConfigs: [[credentialsId: 'gitlab', url: 'git@gitlabe1.ext.net.nokia.com:csf_bcmt/labs.git']]]
                echo "checking out nventory repo finished"
}

def checkoutTestParentRepo() {
                echo "checking out Test Parent/common-test-definitions repo started"
                checkout changelog: false, poll: false, scm: [$class: 'GitSCM', branches: [[name: '*/master']], doGenerateSubmoduleConfigurations: false, extensions: [
                                                                [$class: 'RelativeTargetDirectory', relativeTargetDir: 'common-test-definitions'],
                                                                [$class: 'SparseCheckoutPaths', sparseCheckoutPaths: [[path: 'sut-properties']]],
                                                                [$class: 'CleanBeforeCheckout'],
                                                                [$class: 'ChangelogToBranch', options: [compareRemote: 'common-test-definitions', compareTarget: 'TEST1']],
                                                                [$class: 'PathRestriction', excludedRegions: '.*', includedRegions: '']], submoduleCfg: [], userRemoteConfigs: [[credentialsId: 'd0413804-f035-4d68-8a7c-22385074a91a', url: 'ssh://bhgerrit.ext.net.nokia.com:8282/CA4CI/CommonComponents/common-test-definitions']]]
                echo "checking out Test Parent/common-test-definitions repo finished"
}

def checkoutDependantComponents() {
	echo "checking out dependant components started  $dependantComponents"
	if(dependantComponents == null) {
		echo "No dependant components to checkout"
	} else {
		echo "started checking out dependencies..."
		def dependantList = dependantComponents.tokenize(',')
		dependantList.each{
			def dependantInfo = it.tokenize(':')
			if(dependantInfo.size() == 2) {
				checkout changelog: false, poll: false, scm: [$class: 'GitSCM', branches: [[name: '*/master']], doGenerateSubmoduleConfigurations: false, extensions: [[$class: 'CheckoutOption', timeout: 100], [$class: 'CloneOption', depth: 0, noTags: false, reference: '', shallow: false, timeout: 100], [$class: 'RelativeTargetDirectory', relativeTargetDir: dependantInfo[0]], [$class: 'SparseCheckoutPaths', sparseCheckoutPaths: [[path: dependantInfo[1]]]], [$class: 'PathRestriction', excludedRegions: '.*', includedRegions: '']], submoduleCfg: [], userRemoteConfigs: [[credentialsId: 'd0413804-f035-4d68-8a7c-22385074a91a', url: projectRepoUrl + dependantInfo[0]]]]
			} else {
				echo "skipping checking out dependant info as information is insufficient"
			}
		}

		echo "checking out dependant components finished"
	}
}

def revertSnapshot() {
    echo "****************** Revert Snapshot ***********************"
    env.script_path = "/home/centos/CI/" + BUILD_NUMBER
    sh ''' 
        cat ${WORKSPACE}/hosts
        if [ "$machine_type" = 'vlab' ]; then
            ssh -o 'StrictHostKeyChecking no' -i ${key}  centos@10.75.138.220  "mkdir -p ${script_path}"
            scp -i ${key} -o 'StrictHostKeyChecking no' ${WORKSPACE}/hosts ${WORKSPACE}/ContinuousIntegration/buildscripts/install/oneclickinstallation/vlab_infrastructure/clusterrebuild.sh centos@10.75.138.220:${script_path}
            ssh -o 'StrictHostKeyChecking no' -i ${key}  centos@10.75.138.220 "source ${source_file} && chmod 777 /home/centos/CI/${BUILD_NUMBER}/clusterrebuild.sh && sh -x ${script_path}/clusterrebuild.sh '$LOG_NAME' $OS_VERSION $OS_PORTAL $machine_type"
       else
              ssh -o 'StrictHostKeyChecking no' -i ${key}   centos@10.75.138.220 "mkdir -p /home/centos/C1/${BUILD_NUMBER}/"
#scp ${WORKSPACE}/hosts centos@10.75.138.220:/home/centos/C1/${BUILD_NUMBER}/
scp -i ${key} -o 'StrictHostKeyChecking no'  ${WORKSPACE}/hosts centos@10.75.138.220:/home/centos/C1/${BUILD_NUMBER}/
scp -i ${key} -o 'StrictHostKeyChecking no'  ${WORKSPACE}/ContinuousIntegration/buildscripts/install/oneclickinstallation/vlab_infrastructure/c1_rebuild.sh centos@10.75.138.220:/home/centos/C1/${BUILD_NUMBER}/
ssh -o 'StrictHostKeyChecking no' -i ${key}   centos@10.75.138.220 "sh -x /home/centos/C1/${BUILD_NUMBER}/c1_rebuild.sh  /home/centos/C1/${BUILD_NUMBER}/hosts \"$BUILD_NUMBER\" \"$WORKSPACE\""
       fi
       sleep 2m
   '''
}


def copyRPMS() {
   echo "****************** Copying RPMS ***********************"
   sh '''
	hactive=`grep 'hactive' ${WORKSPACE}/IP.txt |cut -d':' -f3 |sed 's/ +//g'`
		echo $hactive
	ssh -o 'StrictHostKeyChecking no' -i ${key}  root@$hactive "echo "10.75.154.40 repo.lab.pl.alcatel-lucent.com" >> /etc/hosts"
	echo $PACKAGE_DIRECTORY
	
	     ssh -o 'StrictHostKeyChecking no' -i ${key} root@$hactive "mkdir -p /tmp/ngdb/RPMs"
		 ssh -o 'StrictHostKeyChecking no' -i ${key} root@$hactive "mkdir -p /opt/cloudera/parcel-repo"
		 ssh -o 'StrictHostKeyChecking no' -i ${key} root@$hactive "mkdir -p /opt/cto/distribution"
         ssh -o 'StrictHostKeyChecking no' -i ${key}   root@10.99.10.26 "scp -o 'StrictHostKeyChecking no' /home/COLLECT_PACKAGE/PRODUCT/$RELEASE/${collect_package}/* root@$hactive:/tmp/ngdb/RPMs"
		 ssh -o 'StrictHostKeyChecking no' -i ${key}   root@10.99.10.26 "scp -o 'StrictHostKeyChecking no' /home/COLLECT_PACKAGE/PLATFORM_DISTRIBUTION/PARCELS/$RELEASE/${platformdistribution}/* root@$hactive:/opt/cto/distribution"
		 ssh -o 'StrictHostKeyChecking no' -i ${key}   root@10.99.10.26 "scp -o 'StrictHostKeyChecking no' /home/COLLECT_PACKAGE/PLATFORM_DISTRIBUTION/PARCELS/$RELEASE/${platformdistribution}/* root@$hactive:/opt/cloudera/parcel-repo"
		sleep 30s 
	
   '''
}

	

def ifwUpdate() {
	echo "****************** IFW update ***********************"
	sh '''
			set +e
		hactive=`grep 'hactive' ${WORKSPACE}/IP.txt |cut -d':' -f3 |sed 's/ +//g'`
		sactive=`grep 'sactive' ${WORKSPACE}/IP.txt |cut -d':' -f3 |sed 's/ +//g'`
		spassive=`grep 'spassive' ${WORKSPACE}/IP.txt |cut -d':' -f3 |sed 's/ +//g'`
		hslave1=`grep 'hslave1' ${WORKSPACE}/IP.txt |cut -d':' -f3 |sed 's/ +//g'`
		hslave2=`grep 'hslave2' ${WORKSPACE}/IP.txt |cut -d':' -f3 |sed 's/ +//g'`
		hslave3=`grep 'hslave3' ${WORKSPACE}/IP.txt |cut -d':' -f3 |sed 's/ +//g'`
		hslave2=`grep 'hslave2' ${WORKSPACE}/IP.txt |cut -d':' -f3 |sed 's/ +//g'`
		
		echo $hactive

		for node in $hactive ; do
			echo "Installing pre requisistes on node"
			ssh -o 'StrictHostKeyChecking no' -i ${key} root@$node "rpm -ivh /tmp/ngdb/RPMs/NOKIA-CEMOD-PREREQUISITES-*"
			echo "Installing all IFW RPMS on node"
			ssh -o 'StrictHostKeyChecking no' -i ${key} root@$node "rpm -ivh /tmp/ngdb/RPMs/NOKIA-CEMOD-MIFW-COMMON-* /tmp/ngdb/RPMs/NOKIA-CEMOD-MIFW-PLATFORM-* /tmp/ngdb/RPMs/NOKIA-CEMOD-MIFW-APPLICATION-*"
			echo "Copying config files from SVN Workspace"
			scp -i ${key} -o 'StrictHostKeyChecking no' $WORKSPACE/ContinuousIntegration/buildscripts/install/oneclickinstallation/mifw_config/OR_Cloudera/${templateFolder}/platform_config.xml root@$node:/opt/nsn/ngdb/ifw/etc/platform/
			scp -i ${key} -o 'StrictHostKeyChecking no' $WORKSPACE/ContinuousIntegration/buildscripts/install/oneclickinstallation/mifw_config/OR_Cloudera/${templateFolder}/common_config.xml root@$node:/opt/nsn/ngdb/ifw/etc/common/
			scp -i ${key} -o 'StrictHostKeyChecking no' $WORKSPACE/ContinuousIntegration/buildscripts/install/oneclickinstallation/mifw_config/OR_Cloudera/${templateFolder}/application_config.xml root@$node:/opt/nsn/ngdb/ifw/etc/application/
			echo "Copying NGDB Licenses from SVN Workspace"
		done

		for node in $hactive; do
			ssh -o 'StrictHostKeyChecking no' -i ${key} root@$node "mkdir -p /tmp/ngdb/licensefiles/"
			scp -i ${key} -o 'StrictHostKeyChecking no' $WORKSPACE/ContinuousIntegration/buildscripts/install/oneclickinstallation/licenses/Latest/* root@$node:/tmp/ngdb/licensefiles/
			ssh -o 'StrictHostKeyChecking no' -i ${key}  root@$node "rm -rf /tmp/ngdb/licensefiles/N1100821.XML"
		done
		scp -i ${key} -o 'StrictHostKeyChecking no' $WORKSPACE/IP.txt  root@$hactive:/opt/nsn/ngdb/ifw/etc/
		scp -i ${key} -o 'StrictHostKeyChecking no' $WORKSPACE/ContinuousIntegration/buildscripts/install/oneclickinstallation/install_scripts/common/*.pl  root@$hactive:/opt/nsn/ngdb/ifw/etc/
		scp -i ${key} -o 'StrictHostKeyChecking no' $WORKSPACE/ContinuousIntegration/buildscripts/install/oneclickinstallation/install_scripts/common/ContentApps_disable.sh root@$hactive:/opt/nsn/ngdb/ifw/etc/
		ssh -o 'StrictHostKeyChecking no' -i ${key}  root@$hactive "sh /opt/nsn/ngdb/ifw/etc/ContentApps_disable.sh"
		ssh -o 'StrictHostKeyChecking no' -i ${key}  root@$hactive "perl /opt/nsn/ngdb/ifw/etc/ip_change_common.pl  /opt/nsn/ngdb/ifw/etc/IP.txt"
		scp -i ${key} -o 'StrictHostKeyChecking no' $WORKSPACE/ContinuousIntegration/buildscripts/install/oneclickinstallation/install_scripts/common/update_cp.sh root@$hactive:/opt/nsn/ngdb/ifw/etc/
		ssh -o 'StrictHostKeyChecking no' -i ${key}  root@$hactive "chmod 777 /opt/nsn/ngdb/ifw/etc/update_cp.sh"
		
	
		if [ "$machine_type" = 'vlab' ]; then
		ssh -o 'StrictHostKeyChecking no' -i ${key} root@$sactive "mkdir -p /Data3/spark/tmp;chmod 777 /Data3/spark/tmp"
        ssh -o 'StrictHostKeyChecking no' -i ${key}  root@$spassive "mkdir -p /Data3/spark/tmp;chmod 777 /Data3/spark/tmp"
		ssh -o 'StrictHostKeyChecking no' -i ${key}  root@$hslave1 "mkdir -p /Data0/spark/tmp;chmod 777 /Data0/spark/tmp"
		ssh -o 'StrictHostKeyChecking no' -i ${key}  root@$hslave2 "mkdir -p /Data0/spark/tmp;chmod 777 /Data0/spark/tmp"
		ssh -o 'StrictHostKeyChecking no' -i ${key}  root@$hslave3 "mkdir -p /Data0/spark/tmp;chmod 777 /Data0/spark/tmp"
		ssh -o 'StrictHostKeyChecking no' -i ${key}  root@$hslave2 "mkdir -p /Data2/"
		ssh -o 'StrictHostKeyChecking no' -i ${key}  root@$hslave2 "mkdir -p /Data3/"
		ssh -o 'StrictHostKeyChecking no' -i ${key}  root@$hslave3 "mkdir -p /Data2/"
		ssh -o 'StrictHostKeyChecking no' -i ${key}  root@$hslave3 "mkdir -p /Data3/"
	else
	ssh -o 'StrictHostKeyChecking no' -i ${key} root@$sactive "mkdir -p /Data3/spark/tmp;chmod 777 /Data3/spark/tmp"
        ssh -o 'StrictHostKeyChecking no' -i ${key}  root@$spassive "mkdir -p /Data3/spark/tmp;chmod 777 /Data3/spark/tmp"
		ssh -o 'StrictHostKeyChecking no' -i ${key}  root@$hslave1 "mkdir -p /Data0/spark/tmp;chmod 777 /Data0/spark/tmp"
		ssh -o 'StrictHostKeyChecking no' -i ${key}  root@$hslave2 "mkdir -p /Data0/spark/tmp;chmod 777 /Data0/spark/tmp"
		ssh -o 'StrictHostKeyChecking no' -i ${key}  root@$hslave3 "mkdir -p /Data0/spark/tmp;chmod 777 /Data0/spark/tmp"
		ssh -o 'StrictHostKeyChecking no' -i ${key}  root@$hslave2 "mkdir -p /Data2/"
		ssh -o 'StrictHostKeyChecking no' -i ${key}  root@$hslave3 "mkdir -p /Data2/"
		fi
        '''
}


def updateContentPacks() {
	echo "****************** Updating ContentPacks ***********************"
	sh '''
		hactive=`grep 'hactive' ${WORKSPACE}/IP.txt |cut -d':' -f3 |sed 's/ +//g'`
		echo ${contentPacks} > contentPacks.txt
		cat contentPacks.txt |  awk -F',' '{for (i=1; i<= NF; i++) print $i}' > listcontentPacks.txt
		for CP in `cat listcontentPacks.txt`
		do
			echo $CP
			ssh -o 'StrictHostKeyChecking no' -i ${key}  root@$hactive "export CP=\$CP && sed -i \'s:<property name=\\"$CP\\".*:<property name=\\"$CP\\" value=\\"yes\\"/>:g\' /opt/nsn/ngdb/ifw/etc/application/application_config.xml"
		done   
	'''		
}

def installProduct() {
	echo "****************** Installating product ***********************"
	sh '''
		hactive=`grep 'hactive' ${WORKSPACE}/IP.txt |cut -d':' -f3 |sed 's/ +//g'`
		echo $hactive
		mv ${WORKSPACE}/ContinuousIntegration/buildscripts/install/oneclickinstallation/vlab_infrastructure/mount.pl ${WORKSPACE}/ContinuousIntegration/buildscripts/install/oneclickinstallation/vlab_infrastructure/mount_disks_with_gluster_configuration.pl
		scp -i ${key} -o 'StrictHostKeyChecking no' ${WORKSPACE}/ContinuousIntegration/buildscripts/install/oneclickinstallation/vlab_infrastructure/mount_disks_with_gluster_configuration.pl root@$hactive:/opt/nsn/ngdb/ifw/bin/common/
		echo "Starting installation"
		ssh -o 'StrictHostKeyChecking no' -i ${key} root@$hactive "/usr/bin/perl /opt/nsn/ngdb/ifw/install_CEM.pl --all"
		echo "Install complete"
		echo "======================================================================"
		ssh -o 'StrictHostKeyChecking no' -i ${key} root@$hactive "rm -rf /Data0/rpms"
	'''
}



