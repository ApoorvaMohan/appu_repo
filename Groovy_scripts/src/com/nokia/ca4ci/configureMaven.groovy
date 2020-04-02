package com.nokia.ca4ci

def configureMaven() {

	withCredentials([usernamePassword(credentialsId: 'cemod-artifactory', passwordVariable: 'OS_PASSWORD', usernameVariable: 'OS_USERNAME')]) {
          sh "sed -i s/OS_USERNAME/${OS_USERNAME}/ devops_ci/settings/settings.xml"
          sh "sed -i s/OS_PASSWORD/${OS_PASSWORD}/ devops_ci/settings/settings.xml"
          sh 'sudo cp -r ${WORKSPACE}/devops_ci/settings/settings.xml /nfsshare/nemesis/opt/apache-maven-3.0.5/conf/'
	}
}
