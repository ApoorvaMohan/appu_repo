package com.nokia.ca4ci

def promoteHelmVersion(){
	  
	   env.artifactoryrepo = "cemod-yum-candidates-local" 
	   env.COLLECTOR_PATH = "devops_ci/collector/product/${RELEASE}"

	   env.PREVIOUS_PROMOTED_JOB_URL = env.DOCKER_BUILD_NAME
	   env.PREVIOUS_PROMOTED_JOB_URL = sh(returnStdout: true, script: 'echo $PREVIOUS_PROMOTED_JOB_URL | sed -e \"s/ :: /\\/job\\//g\"').trim()
           env.PREVIOUS_PROMOTED_JOB_URL = "https://build13.cci.nokia.net/job/" + env.PREVIOUS_PROMOTED_JOB_URL
           env.CURRENT_PROMOTED_JOB_URL = env.PREVIOUS_PROMOTED_JOB_URL + "/" + env.DOCKER_BUILD_NUMBER + "/"
           echo "PREVIOUS_PROMOTED_JOB_URL = $PREVIOUS_PROMOTED_JOB_URL"
	   echo "CURRENT_PROMOTED_JOB_URL =  $CURRENT_PROMOTED_JOB_URL "

           def chartList = env.CHART_NAME.split(' ')
           for(chart in chartList) {
              env.CHART_NAME = chart
	      env.PREVIOUS_PROMOTED_BUILD = sh(returnStdout: true, script: 'grep \"^$CHART_NAME \" $COLLECTOR_PATH/$BUILD_PROFILE/promotedHelmdependencies.txt | awk -F \" \" \'{print $2}\'').trim()
              sh "source devops_ci/scripts/buildscripts/utilities/add_promoted_helm_to_collector.sh"
           }
	   env.PREVIOUS_PROMOTED_BUILD = sh(returnStdout: true, script: 'echo ${PREVIOUS_PROMOTED_BUILD##*.}').trim()
           env.PREVIOUS_PROMOTED_BUILD = sh(returnStdout: true, script: 'echo ${PREVIOUS_PROMOTED_BUILD%%-*}').trim()
	   echo "env.PREVIOUS_PROMOTED_BUILD = $env.PREVIOUS_PROMOTED_BUILD"
           sh '''
                cat ${COLLECTOR_PATH}/${BUILD_PROFILE}/promotedHelmdependencies.txt
                cat ${COLLECTOR_PATH}/${BUILD_PROFILE}/helmBuildnumber.txt
           '''
	   env.HELM_VERSION = readFile("${WORKSPACE}/${env.COLLECTOR_PATH}/${env.BUILD_PROFILE}/helmBuildnumber.txt").trim()
	   //Publish the files to Artifactory
	   //def publish = new com.nokia.ca4ci.publish()
           //publish.uploadAnyFile("${env.artifactoryrepo}/${env.BUILD_PROFILE}/HELM/${RELEASE}/${env.HELM_VERSION}","devops_ci/collector/product/${RELEASE}/${BUILD_PROFILE}/promotedHelmdependencies.txt devops_ci/collector/product/${RELEASE}/${BUILD_PROFILE}/helmBuildnumber.txt")

           def commit = new com.nokia.ca4ci.gitcommit()
           commit.commitFiles("devops_ci","CEMODPBL-1:Promoted ${chartList} version ${CHART_VERSION}","collector/product/${RELEASE}/${BUILD_PROFILE}/promotedHelmdependencies.txt collector/product/${RELEASE}/${BUILD_PROFILE}/helmBuildnumber.txt")           

	  //locking the current build
          cto.devops.jenkins.Utils.markBuildAsKeepForever("${CURRENT_PROMOTED_JOB_URL}")

         //Unlocking previously promoted build:
         sh """
		 if grep -rnw $COLLECTOR_PATH/$BUILD_PROFILE/promoteddependencies.txt -e ${env.PREVIOUS_PROMOTED_JOB_URL}/${env.PREVIOUS_PROMOTED_BUILD}
		 then
		        echo "RPM is also promoted for this build ${env.PREVIOUS_PROMOTED_JOB_URL}/${env.PREVIOUS_PROMOTED_BUILD} so this will not be unlocked"
		 else
		        echo "unlocking the build"
			curl -X POST -s -u '${env.JENKINS_CREDENTIALS}' ${env.PREVIOUS_PROMOTED_JOB_URL}/${env.PREVIOUS_PROMOTED_BUILD}/api/xml?depth=0 > keeplog.txt
                 	if grep "<keepLog>true</keepLog>" keeplog.txt > /dev/null;
                 	then
				echo "disabled unlocking for a while"
                        	#curl -X POST -s -u '${env.JENKINS_CREDENTIALS}' ${env.PREVIOUS_PROMOTED_JOB_URL}/${env.PREVIOUS_PROMOTED_BUILD}/toggleLogKeep
                 	fi
		 fi
         """
}

