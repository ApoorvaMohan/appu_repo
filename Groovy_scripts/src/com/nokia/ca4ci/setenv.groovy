package com.nokia.ca4ci

def envcp() {

	    env.RELEASE_NUMBER = sh(returnStdout: true, script: 'date +%y.%m').trim()
	    env.HELM_DOCKER_RELEASE_NUMBER = sh(returnStdout: true, script: 'date +%y.%-m').trim()
	    env.QUALIFIER = ''
	    echo "RELEASE_NUMBER = $RELEASE_NUMBER"
	    echo "HELM_DOCKER_RELEASE_NUMBER = $HELM_DOCKER_RELEASE_NUMBER"
            if( env.RELEASE == '17SP1USCC' ) {
		 env.RELEASE_NUMBER = "16.11"
            }
	    if( env.RELEASE == '17MP3' ) {
                 env.RELEASE_NUMBER = "17.12"
            }
	    if( env.RELEASE == '2' ) {
		 env.QUALIFIER="-2"
        	 env.PARENT_VERSION="2"
        	 env.SOURCE_RELEASE="2"
            }	
	    if( env.RELEASE == '3' ) {
                 env.QUALIFIER="-3"
                 env.PARENT_VERSION="3"
                 env.SOURCE_RELEASE="3"
            }
	    if( env.RELEASE == '4' ) {
                 env.QUALIFIER="-4"
                 env.PARENT_VERSION="4"
                 env.SOURCE_RELEASE="4"
            }
	    if( env.RELEASE == '5' ) {
                 env.QUALIFIER="-5"
                 env.PARENT_VERSION="5"
                 env.SOURCE_RELEASE="5"
            }
	    if( env.RELEASE == '6' ) {
		 env.RELEASE_NUMBER="19.12"
                 env.QUALIFIER="-6"
                 env.PARENT_VERSION="6"
                 env.SOURCE_RELEASE="6"
            }
	    if( env.RELEASE == '7' ) {
                 env.QUALIFIER="-7"
                 env.PARENT_VERSION="7"
                 env.SOURCE_RELEASE="7"
            }
	    if( env.RELEASE == '8' ) {
                 env.QUALIFIER="-8"
                 env.PARENT_VERSION="8"
                 env.SOURCE_RELEASE="8"
            }
	    if( env.RELEASE == '10' ) {
                 env.QUALIFIER="-10"
                 env.PARENT_VERSION="10"
                 env.SOURCE_RELEASE="10"
            }
	    if( env.RELEASE == '6-CT' ) {
		 env.RELEASE_NUMBER="20.01"
                 env.QUALIFIER="-6"
                 env.PARENT_VERSION="6-CT"
                 env.SOURCE_RELEASE="6-CT"
            }
	    if( env.RELEASE == '0' ) {
                 env.QUALIFIER="-0"
                 env.PARENT_VERSION="0"
                 env.SOURCE_RELEASE="0"
            }
	    env.rpm_version =  RELEASE_NUMBER + "." +  BUILD_NUMBER + QUALIFIER
	    env.project_helm_version = HELM_DOCKER_RELEASE_NUMBER + "." +  BUILD_NUMBER + QUALIFIER 
	    sh "echo ${rpm_version} > ${WORKSPACE}/devops_ci/scripts/buildscripts/ws/version.txt"
	    sh "echo ${PACKAGE_DIRECTORY} > ${WORKSPACE}/devops_ci/scripts/buildscripts/ws/packagedirectory.txt"
}
