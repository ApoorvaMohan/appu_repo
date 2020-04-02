package com.nokia.ca4ci

def promoteComponentsToPlatform() {
		
		env.artifactoryrepo = "cemod-yum-candidates-local"
		env.staticrepo = "cemod-generic-pucontrolled"
		env.COLLECTOR_PATH = "devops_ci/collector/platform/${RELEASE}"

           env.PREVIOUS_PROMOTED_BUILD = sh(returnStdout: true, script: 'grep \"^$PACKAGE_DIRECTORY \" $COLLECTOR_PATH/$BUILD_PROFILE/promoteddependencies.txt | awk -F \" \" \'{print $2}\'').trim()
           env.PREVIOUS_PROMOTED_BUILD = sh(returnStdout: true, script: 'echo ${PREVIOUS_PROMOTED_BUILD##*.}').trim()
	   env.PREVIOUS_PROMOTED_BUILD = sh(returnStdout: true, script: 'echo ${PREVIOUS_PROMOTED_BUILD%%-*}').trim()
           env.PREVIOUS_PROMOTED_JOB_URL = sh(returnStdout: true, script: 'dirname ${JOB_NAME} ').trim()

	    sh '''
                source devops_ci/scripts/buildscripts/utilities/add_promotedcomponent_to_collector.sh
                cat ${COLLECTOR_PATH}/${BUILD_PROFILE}/promoteddependencies.txt
                cat ${COLLECTOR_PATH}/${BUILD_PROFILE}/buildnumber.txt
            '''
                env.PLATFORM_VERSION = readFile("${WORKSPACE}/${env.COLLECTOR_PATH}/${env.BUILD_PROFILE}/buildnumber.txt").trim()
                echo "PLATFORM_VERSION = ${env.PLATFORM_VERSION}"
		
		// Create a directory for the profile and copy move each component's files there
                echo "Create directory tree for this profile's new release candidate..."
		def targetDir = "${env.artifactoryrepo}/${env.BUILD_PROFILE}/PLATFORM/${RELEASE}/${env.PLATFORM_VERSION}/"	
		sh "curl -X PUT -s -u '${env.ARTIFACTORY_CREDENTIALS}' '${env.ARTIFACTORY_HTTPS_URL}/${targetDir}'"
                
          for (String i : readFile('versions.txt').split("\r?\n")) {
                def line = i.split(' ')
                def component = line[0] 
                def version = line[1] 
		def srcrelease = line[2]
		def sourcerepoAPI = "api/versions/${env.artifactoryrepo}/${env.BUILD_PROFILE}/${component}/${srcrelease}"
                echo "component = ${component}, version = ${version}"
		if( component == 'STATIC_RPMS' ) {
			sourcerepoAPI = "api/storage/${env.staticrepo}/${env.BUILD_PROFILE}/${component}"
		}
		//if( component == 'STATIC_COMMONS' || component == 'PORTAL_STATIC' ) {
		if( component == 'STATIC_COMMONS' ) {
                        sourcerepoAPI = "api/storage/${env.staticrepo}"
                }
                sh "curl -s -u '${env.ARTIFACTORY_CREDENTIALS}' '${env.ARTIFACTORY_HTTPS_URL}/${sourcerepoAPI}/${version}?listFiles=1' > ${component}-files.json"
		sh "cat ${component}-files.json" // Debug
		CURL_STAT = sh (
                                script: "grep -oE \"errors\" ${component}-files.json",
                                returnStatus: true
                ) == 0
                if( CURL_STAT == true ) {
               		echo "${component}/${version} doesnt exist"
                        def commit = new com.nokia.ca4ci.gitcommit()
                        commit.commitFiles("devops_ci","CEMODPBL-1:Incremented platform build number to ${PLATFORM_VERSION} though it is not proper","collector/platform/${RELEASE}/${BUILD_PROFILE}/buildnumber.txt")
                        currentBuild.result = 'FAILURE'
                        return
                }
                
                // Load the list of files into an object
                def componentJson = readJSON file: "${component}-files.json"

                // Copy each file to the profile's directory; this is all done on the service side
                echo "Copy artifacts of component '${component}' to this profile's directory tree..."
		if( component == 'STATIC_RPMS' ) {
			for(children in componentJson.children) {
                              def sourceFile = "${env.staticrepo}/${env.BUILD_PROFILE}/${component}/${version}${children.uri}"
			      CURL_STAT = sh (
                              	script: "curl -X POST -s -u '${env.ARTIFACTORY_CREDENTIALS}' '${env.ARTIFACTORY_HTTPS_URL}/api/copy/${sourceFile}?to=/${targetDir}/${children.uri}&dryRun=0' >log.txt && grep -oE 'errors|ERROR' log.txt",
                                returnStatus: true
                              ) == 0
                              if( CURL_STAT == true ) {
                              	echo "Copying RPM failed due to some errors"
                                def commit = new com.nokia.ca4ci.gitcommit()
				commit.commitFiles("devops_ci","CEMODPBL-1:Incremented platform build number to ${PLATFORM_VERSION} though it is not proper","collector/platform/${RELEASE}/${BUILD_PROFILE}/buildnumber.txt")
                                currentBuild.result = 'FAILURE'
                                return
                              }
                        }
		}
//		else if( component == 'STATIC_COMMONS' || component == 'PORTAL_STATIC'){
		else if( component == 'STATIC_COMMONS'){
			for(children in componentJson.children) {
                              def sourceFile = "${env.staticrepo}/${component}/${version}${children.uri}"
			      CURL_STAT = sh (
                                script: "curl -X POST -s -u '${env.ARTIFACTORY_CREDENTIALS}' '${env.ARTIFACTORY_HTTPS_URL}/api/copy/${sourceFile}?to=/${targetDir}/${children.uri}&dryRun=0' >log.txt && grep -oE 'errors|ERROR' log.txt",
                                returnStatus: true
                              ) == 0
                              if( CURL_STAT == true ) {
                                echo "Copying RPM failed due to some errors"
                                def commit = new com.nokia.ca4ci.gitcommit()
                                commit.commitFiles("devops_ci","CEMODPBL-1:Incremented platform build number to ${PLATFORM_VERSION} though it is not proper","collector/platform/${RELEASE}/${BUILD_PROFILE}/buildnumber.txt")
                                currentBuild.result = 'FAILURE'
                                return
                              }
                        }
		}
		else{
                	for(artifact in componentJson.artifacts) {              
	                        def sourceFile = "${artifact.repo}/${artifact.path}"
				CURL_STAT = sh (
                                  script: "curl -X POST -s -u '${env.ARTIFACTORY_CREDENTIALS}' '${env.ARTIFACTORY_HTTPS_URL}/api/copy/${sourceFile}?to=/${targetDir}&dryRun=0' >log.txt && grep -oE 'errors|ERROR' log.txt",
                                  returnStatus: true
                                ) == 0
                              	if( CURL_STAT == true ) {
                                  echo "Copying RPM failed due to some errors"
                                  def commit = new com.nokia.ca4ci.gitcommit()
                                  commit.commitFiles("devops_ci","CEMODPBL-1:Incremented platform build number to ${PLATFORM_VERSION} though it is not proper","collector/platform/${RELEASE}/${BUILD_PROFILE}/buildnumber.txt")
                                  currentBuild.result = 'FAILURE'
                                  return
                                }
                	}
	  	}
	  }
	
	  //Uploading buildnumber.txt and promotedepdendency.txt files
          def publish = new com.nokia.ca4ci.publish()
          publish.uploadAnyFile("${env.artifactoryrepo}/${env.BUILD_PROFILE}/PLATFORM/${RELEASE}/${env.PLATFORM_VERSION}","devops_ci/collector/platform/${RELEASE}/${BUILD_PROFILE}/promoteddependencies.txt devops_ci/collector/platform/${RELEASE}/${BUILD_PROFILE}/buildnumber.txt")

          def commit = new com.nokia.ca4ci.gitcommit()
          commit.commitFiles("devops_ci","CEMODPBL-1:Promoted ${PACKAGE_DIRECTORY} version ${VERSION} to platform: ${PLATFORM_VERSION}","collector/platform/${RELEASE}/${BUILD_PROFILE}/buildnumber.txt collector/platform/${RELEASE}/${BUILD_PROFILE}/promoteddependencies.txt")
	  //locking the current build
          cto.devops.jenkins.Utils.markBuildAsKeepForever("${JOB_NAME}")

	//setting LATEST property	
        sh "curl -X DELETE -s -u '${env.ARTIFACTORY_CREDENTIALS}' '${env.ARTIFACTORY_HTTPS_URL}/api/storage/${env.artifactoryrepo}/${env.BUILD_PROFILE}/PLATFORM/${RELEASE}?properties=LATEST'"
        sh "curl -X PUT -s -u '${env.ARTIFACTORY_CREDENTIALS}' '${env.ARTIFACTORY_HTTPS_URL}/api/storage/${env.artifactoryrepo}/${env.BUILD_PROFILE}/PLATFORM/${RELEASE}/${env.PLATFORM_VERSION}?properties=LATEST=true&recursive=0'"
	sh """
		#Unlocking previously promoted build:
	         curl -X POST -s -u '${env.JENKINS_CREDENTIALS}' ${env.PREVIOUS_PROMOTED_JOB_URL}/${env.PREVIOUS_PROMOTED_BUILD}/api/xml?depth=0 > keeplog.txt
        	 if grep "<keepLog>true</keepLog>" keeplog.txt > /dev/null;
	         then
        	        curl -X POST -s -u '${env.JENKINS_CREDENTIALS}' ${env.PREVIOUS_PROMOTED_JOB_URL}/${env.PREVIOUS_PROMOTED_BUILD}/toggleLogKeep
	         fi
	"""

}    

def promoteComponentsToPlatformWithoutRPMS() {

           env.artifactoryrepo = "cemod-yum-candidates-local"
           env.staticrepo = "cemod-generic-pucontrolled"
           env.COLLECTOR_PATH = "devops_ci/collector/platform/${RELEASE}"

           env.PREVIOUS_PROMOTED_BUILD = sh(returnStdout: true, script: 'grep \"^$PACKAGE_DIRECTORY \" $COLLECTOR_PATH/$BUILD_PROFILE/promoteddependencies.txt | awk -F \" \" \'{print $2}\'').trim()
           env.PREVIOUS_PROMOTED_BUILD = sh(returnStdout: true, script: 'echo ${PREVIOUS_PROMOTED_BUILD##*.}').trim()
           env.PREVIOUS_PROMOTED_BUILD = sh(returnStdout: true, script: 'echo ${PREVIOUS_PROMOTED_BUILD%%-*}').trim()
           env.PREVIOUS_PROMOTED_JOB_URL = sh(returnStdout: true, script: 'dirname ${JOB_NAME} ').trim()

            sh '''
                source devops_ci/scripts/buildscripts/utilities/add_promotedcomponent_to_collector.sh
                cat ${COLLECTOR_PATH}/${BUILD_PROFILE}/promoteddependencies.txt
                cat ${COLLECTOR_PATH}/${BUILD_PROFILE}/buildnumber.txt
            '''
                env.PLATFORM_VERSION = readFile("${WORKSPACE}/${env.COLLECTOR_PATH}/${env.BUILD_PROFILE}/buildnumber.txt").trim()
                echo "PLATFORM_VERSION = ${env.PLATFORM_VERSION}"

                // Create a directory for the profile and copy move each component's files there
                echo "Create directory tree for this profile's new release candidate..."
                def targetDir = "${env.artifactoryrepo}/${env.BUILD_PROFILE}/PLATFORM/${RELEASE}/${env.PLATFORM_VERSION}/"
                sh "curl -X PUT -s -u '${env.ARTIFACTORY_CREDENTIALS}' '${env.ARTIFACTORY_HTTPS_URL}/${targetDir}'"
		
		//Uploading buildnumber.txt and promotedepdendency.txt files
		try {
			def publish = new com.nokia.ca4ci.publish()
		        publish.uploadAnyFile("${env.artifactoryrepo}/${env.BUILD_PROFILE}/PLATFORM/${RELEASE}/${env.PLATFORM_VERSION}","devops_ci/collector/platform/${RELEASE}/${BUILD_PROFILE}/promoteddependencies.txt devops_ci/collector/platform/${RELEASE}/${BUILD_PROFILE}/buildnumber.txt")
                }
                catch (err) {
                        def commit = new com.nokia.ca4ci.gitcommit()
                        commit.commitFiles("devops_ci","CEMODPBL-1:Incremented platform build number to ${PLATFORM_VERSION} though it is not proper","collector/platform/${RELEASE}/${BUILD_PROFILE}/buildnumber.txt")
                        currentBuild.result = 'FAILURE'
                        throw err
                        return
                }

          def commit = new com.nokia.ca4ci.gitcommit()
          commit.commitFiles("devops_ci","CEMODPBL-1:Promoted ${PACKAGE_DIRECTORY} version ${VERSION} to platform: ${PLATFORM_VERSION}","collector/platform/${RELEASE}/${BUILD_PROFILE}/buildnumber.txt collector/platform/${RELEASE}/${BUILD_PROFILE}/promoteddependencies.txt")
          //locking the current build
          cto.devops.jenkins.Utils.markBuildAsKeepForever("${JOB_NAME}")

        //setting LATEST property
        sh "curl -X DELETE -s -u '${env.ARTIFACTORY_CREDENTIALS}' '${env.ARTIFACTORY_HTTPS_URL}/api/storage/${env.artifactoryrepo}/${env.BUILD_PROFILE}/PLATFORM/${RELEASE}?properties=LATEST'"
        sh "curl -X PUT -s -u '${env.ARTIFACTORY_CREDENTIALS}' '${env.ARTIFACTORY_HTTPS_URL}/api/storage/${env.artifactoryrepo}/${env.BUILD_PROFILE}/PLATFORM/${RELEASE}/${env.PLATFORM_VERSION}?properties=LATEST=true&recursive=0'"
        sh """
                #Unlocking previously promoted build:
                 curl -X POST -s -u '${env.JENKINS_CREDENTIALS}' ${env.PREVIOUS_PROMOTED_JOB_URL}/${env.PREVIOUS_PROMOTED_BUILD}/api/xml?depth=0 > keeplog.txt
                 if grep "<keepLog>true</keepLog>" keeplog.txt > /dev/null;
                 then
			echo "disabled unlocking for a while"
                        #curl -X POST -s -u '${env.JENKINS_CREDENTIALS}' ${env.PREVIOUS_PROMOTED_JOB_URL}/${env.PREVIOUS_PROMOTED_BUILD}/toggleLogKeep
                 fi
        """

}
