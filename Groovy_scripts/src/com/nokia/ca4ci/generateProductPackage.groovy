package com.nokia.ca4ci

def promoteComponentsToProduct() {
		
 	   env.artifactoryrepo = "cemod-yum-candidates-local"
	   env.COLLECTOR_PATH = "devops_ci/collector/product/${RELEASE}"
		
           env.PREVIOUS_PROMOTED_BUILD = sh(returnStdout: true, script: 'grep \"^$PACKAGE_DIRECTORY \" $COLLECTOR_PATH/$BUILD_PROFILE/promoteddependencies.txt | awk -F \" \" \'{print $2}\'').trim()
           env.PREVIOUS_PROMOTED_BUILD = sh(returnStdout: true, script: 'echo ${PREVIOUS_PROMOTED_BUILD##*.}').trim()
	   env.PREVIOUS_PROMOTED_BUILD = sh(returnStdout: true, script: 'echo ${PREVIOUS_PROMOTED_BUILD%%-*}').trim()
           env.PREVIOUS_PROMOTED_JOB_URL = sh(returnStdout: true, script: 'dirname ${JOB_NAME} ').trim()
	
	   
	   sh '''
                source devops_ci/scripts/buildscripts/utilities/add_promotedcomponent_to_collector.sh
                cat ${COLLECTOR_PATH}/${BUILD_PROFILE}/promoteddependencies.txt
                cat ${COLLECTOR_PATH}/${BUILD_PROFILE}/buildnumber.txt
           '''
                env.PRODUCT_VERSION = readFile("${WORKSPACE}/${env.COLLECTOR_PATH}/${env.BUILD_PROFILE}/buildnumber.txt").trim()
                echo "PRODUCT_VERSION = ${env.PRODUCT_VERSION}"
	echo "ONLY_PROPERTY_UPDATE = ${ONLY_PROPERTY_UPDATE}"
       	if( env.ONLY_PROPERTY_UPDATE != "true" ) {
          for (String i : readFile('versions.txt').split("\r?\n")) {
                def line = i.split(' ')
                def component = line[0] 
                def version = line[1] 
		def srcrelease = line[2]
                echo "component = ${component}, version = ${version}"
                if(component.startsWith("#")){
                        echo "Skipping copy of rpms for interface components"
			
                } 
		else {
			if( component != 'PLATFORM' ) {  
				sh "curl -s -u '${env.ARTIFACTORY_CREDENTIALS}' '${env.ARTIFACTORY_HTTPS_URL}/api/versions/${env.artifactoryrepo}/${env.BUILD_PROFILE}/${component}/${srcrelease}/${version}?listFiles=1' > ${component}-files.json"
				sh "cat ${component}-files.json" // Debug
				CURL_STAT = sh (
					script: "grep -oE \"errors\" ${component}-files.json",
                                	returnStatus: true
                        	) == 0
				if( CURL_STAT == true ) {
					echo "${component}/${version} doesnt exist"
					def commit = new com.nokia.ca4ci.gitcommit()
			        	commit.commitFiles("devops_ci","CEMODPBL-1:Incremented product number to ${PRODUCT_VERSION} though it is not proper","collector/product/${RELEASE}/${BUILD_PROFILE}/buildnumber.txt")
		                	currentBuild.result = 'FAILURE'
                 			return	
				}

                		// Load the list of files into an object
		                def componentJson = readJSON file: "${component}-files.json"
		
        		        // Create a directory for the profile and copy move each component's files there
                		echo "Create directory tree for this profile's new release candidate..."
	
		                def targetDir = "${env.artifactoryrepo}/${env.BUILD_PROFILE}/PRODUCT/${RELEASE}/${env.PRODUCT_VERSION}/${component}/"   
	
        		        sh "curl -X PUT -s -u '${env.ARTIFACTORY_CREDENTIALS}' '${env.ARTIFACTORY_HTTPS_URL}/${targetDir}'"
	
        		        // Copy each file to the profile's directory; this is all done on the service side
                		echo "Copy artifacts of component '${component}' to this profile's directory tree..."
		                for(artifact in componentJson.artifacts) {              
        		                def sourceFile = "${artifact.repo}/${artifact.path}"
					CURL_STAT = sh (
				        	script: "curl -X POST -s -u '${env.ARTIFACTORY_CREDENTIALS}' '${env.ARTIFACTORY_HTTPS_URL}/api/copy/${sourceFile}?to=/${targetDir}&dryRun=0' >log.txt && grep -oE 'errors|ERROR' log.txt",
                                		returnStatus: true
                        	    	) == 0
				        if( CURL_STAT == true ) {
					         echo "${component}/${version} Copying RPM failed due to some errors"
					         def commit = new com.nokia.ca4ci.gitcommit()
			        	         commit.commitFiles("devops_ci","CEMODPBL-1:Incremented product number to ${PRODUCT_VERSION} though it is not proper","collector/product/${RELEASE}/${BUILD_PROFILE}/buildnumber.txt")
		                	        currentBuild.result = 'FAILURE'
                 			        return	
			                }
                		}
      			} 
			else{
				sh "curl -s -u '${env.ARTIFACTORY_CREDENTIALS}' '${env.ARTIFACTORY_HTTPS_URL}/api/storage/${env.artifactoryrepo}/${env.BUILD_PROFILE}/${component}/${srcrelease}/${version}?list&deep=1' > ${component}-files.json"
                        	sh "cat ${component}-files.json" // Debug
				CURL_STAT = sh (
                                	script: "grep -oE \"errors\" ${component}-files.json",
                                	returnStatus: true
	                        ) == 0
				echo "CURL_STAT = $CURL_STAT"
                        	if( CURL_STAT == true ) {
                                	echo "${component}/${srcrelease}/${version} doesnt exist"
	                                def commit = new com.nokia.ca4ci.gitcommit()
        	                        commit.commitFiles("devops_ci","CEMODPBL-1:Incremented product number to ${PRODUCT_VERSION} though it is not proper","collector/product/${RELEASE}/${BUILD_PROFILE}/buildnumber.txt")
                	                currentBuild.result = 'FAILURE'
                        	        return
	                        }	
                        
				// Load the list of files into an object
                	        def componentJson = readJSON file: "${component}-files.json"

                        	// Create a directory for the profile and copy move each component's files there
	                        echo "Create directory tree for this profile's new release candidate..."

        	                def targetDir = "${env.artifactoryrepo}/${env.BUILD_PROFILE}/PRODUCT/${RELEASE}/${env.PRODUCT_VERSION}/${component}/"

                	        sh "curl -X PUT -s -u '${env.ARTIFACTORY_CREDENTIALS}' '${env.ARTIFACTORY_HTTPS_URL}/${targetDir}'"

                        	// Copy each file to the profile's directory; this is all done on the service side
                	        echo "Copy artifacts of component '${component}' to this profile's directory tree..."
				for(files in componentJson.files) {
                                	def sourceFile = "${env.artifactoryrepo}/${env.BUILD_PROFILE}/${component}/${srcrelease}/${version}${files.uri}"
					CURL_STAT = sh (
				        	script: "curl -X POST -s -u '${env.ARTIFACTORY_CREDENTIALS}' '${env.ARTIFACTORY_HTTPS_URL}/api/copy/${sourceFile}?to=/${targetDir}&dryRun=0' >log.txt && grep -oE 'errors|ERROR' log.txt",
                                	 	returnStatus: true
                        	        ) == 0
				        if( CURL_STAT == true ) {
					        echo "${component}/${version} Copying RPM failed due to some errors"
					        def commit = new com.nokia.ca4ci.gitcommit()
			        	        commit.commitFiles("devops_ci","CEMODPBL-1:Incremented product number to ${PRODUCT_VERSION} though it is not proper","collector/product/${RELEASE}/${BUILD_PROFILE}/buildnumber.txt")
		                	        currentBuild.result = 'FAILURE'
                 			        return	
			                 }
        	                }
			}
	     	}
          }	

		//Uploading buildnumber.txt and promotedepdendency.txt files
		sh "pwd"
		def publish = new com.nokia.ca4ci.publish()
        	publish.uploadAnyFile("${env.artifactoryrepo}/${env.BUILD_PROFILE}/PRODUCT/${RELEASE}/${env.PRODUCT_VERSION}","devops_ci/collector/product/${RELEASE}/${BUILD_PROFILE}/promoteddependencies.txt devops_ci/collector/product/${RELEASE}/${BUILD_PROFILE}/buildnumber.txt")	 
      } 

	  def commit = new com.nokia.ca4ci.gitcommit()
 	  if( env.ONLY_PROPERTY_UPDATE == "true" ) {
          	commit.commitFiles("devops_ci","CEMODPBL-1:Promoted ${PACKAGE_DIRECTORY} version ${VERSION} to product: ${PRODUCT_VERSION}","collector/product/${RELEASE}/${BUILD_PROFILE}/promoteddependencies.txt")	
	  }
	  else {
		commit.commitFiles("devops_ci","CEMODPBL-1:Promoted ${PACKAGE_DIRECTORY} version ${VERSION} to product: ${PRODUCT_VERSION}","collector/product/${RELEASE}/${BUILD_PROFILE}/buildnumber.txt collector/product/${RELEASE}/${BUILD_PROFILE}/promoteddependencies.txt")
      
       		sh """
	 		#Setting LATEST property on the PRODUCT build
         		output=`curl -X GET -s -u '${env.ARTIFACTORY_CREDENTIALS}' '${env.ARTIFACTORY_HTTPS_URL}/api/search/prop?LATEST=true&repos=${env.artifactoryrepo}' | grep -i "${env.BUILD_PROFILE}/PRODUCT/${RELEASE}/"`
         		DEST_FOLDER=\${output##*:}
         		DEST_FOLDER=\${DEST_FOLDER%?}
         		curl -X DELETE -s -u '${env.ARTIFACTORY_CREDENTIALS}' https:\${DEST_FOLDER}?properties=LATEST
	 		curl -X PUT -s -u '${env.ARTIFACTORY_CREDENTIALS}' '${env.ARTIFACTORY_HTTPS_URL}/api/storage/${env.artifactoryrepo}/${env.BUILD_PROFILE}/PRODUCT/${RELEASE}/${env.PRODUCT_VERSION}?properties=LATEST=true&recursive=0'
		"""
	}
	
	//locking the current build
        cto.devops.jenkins.Utils.markBuildAsKeepForever("${JOB_NAME}")	

	//Unlocking previously promoted build:
	sh """
		 curl -X POST -s -u '${env.JENKINS_CREDENTIALS}' ${env.PREVIOUS_PROMOTED_JOB_URL}/${env.PREVIOUS_PROMOTED_BUILD}/api/xml?depth=0 > keeplog.txt
        	 if grep "<keepLog>true</keepLog>" keeplog.txt > /dev/null; 
		 then 
			curl -X POST -s -u '${env.JENKINS_CREDENTIALS}' ${env.PREVIOUS_PROMOTED_JOB_URL}/${env.PREVIOUS_PROMOTED_BUILD}/toggleLogKeep
	 	 fi
	"""
}  

def promoteComponentsToProductWithoutRPMS() {

           env.artifactoryrepo = "cemod-yum-candidates-local"
           env.COLLECTOR_PATH = "devops_ci/collector/product/${RELEASE}"

           env.PREVIOUS_PROMOTED_BUILD = sh(returnStdout: true, script: 'grep \"^$PACKAGE_DIRECTORY \" $COLLECTOR_PATH/$BUILD_PROFILE/promoteddependencies.txt | awk -F \" \" \'{print $2}\'').trim()
	   env.PREVIOUS_PROMOTED_HELM = env.PREVIOUS_PROMOTED_BUILD
           env.PREVIOUS_PROMOTED_BUILD = sh(returnStdout: true, script: 'echo ${PREVIOUS_PROMOTED_BUILD##*.}').trim()
           env.PREVIOUS_PROMOTED_BUILD = sh(returnStdout: true, script: 'echo ${PREVIOUS_PROMOTED_BUILD%%-*}').trim()
           env.PREVIOUS_PROMOTED_JOB_URL = sh(returnStdout: true, script: 'dirname ${JOB_NAME} ').trim()


           sh '''
                source devops_ci/scripts/buildscripts/utilities/add_promotedcomponent_to_collector.sh
                cat ${COLLECTOR_PATH}/${BUILD_PROFILE}/promoteddependencies.txt
                cat ${COLLECTOR_PATH}/${BUILD_PROFILE}/buildnumber.txt
           '''
                env.PRODUCT_VERSION = readFile("${WORKSPACE}/${env.COLLECTOR_PATH}/${env.BUILD_PROFILE}/buildnumber.txt").trim()
                echo "PRODUCT_VERSION = ${env.PRODUCT_VERSION}"
        echo "ONLY_PROPERTY_UPDATE = ${ONLY_PROPERTY_UPDATE}"
        if( env.ONLY_PROPERTY_UPDATE != "true" ) {
                //Uploading buildnumber.txt and promotedepdendency.txt files
                sh "pwd"
		try {
        		def publish = new com.nokia.ca4ci.publish()
        		publish.uploadAnyFile("${env.artifactoryrepo}/${env.BUILD_PROFILE}/PRODUCT/${RELEASE}/${env.PRODUCT_VERSION}","devops_ci/collector/product/${RELEASE}/${BUILD_PROFILE}/promoteddependencies.txt devops_ci/collector/product/${RELEASE}/${BUILD_PROFILE}/buildnumber.txt")	 
        	}
	        catch (err) {
                	def commit = new com.nokia.ca4ci.gitcommit()
			commit.commitFiles("devops_ci","CEMODPBL-1:Incremented product number to ${PRODUCT_VERSION} though it is not proper","collector/product/${RELEASE}/${BUILD_PROFILE}/buildnumber.txt")
			currentBuild.result = 'FAILURE'
                	throw err
        	        return
	        }	
        }

          def commit = new com.nokia.ca4ci.gitcommit()
          if( env.ONLY_PROPERTY_UPDATE == "true" ) {
                commit.commitFiles("devops_ci","CEMODPBL-1:Promoted ${PACKAGE_DIRECTORY} version ${VERSION} to product: ${PRODUCT_VERSION}","collector/product/${RELEASE}/${BUILD_PROFILE}/promoteddependencies.txt")
          }
          else {
                commit.commitFiles("devops_ci","CEMODPBL-1:Promoted ${PACKAGE_DIRECTORY} version ${VERSION} to product: ${PRODUCT_VERSION}","collector/product/${RELEASE}/${BUILD_PROFILE}/buildnumber.txt collector/product/${RELEASE}/${BUILD_PROFILE}/promoteddependencies.txt")

                sh """
                        #Setting LATEST property on the PRODUCT build
                        output=`curl -X GET -s -u '${env.ARTIFACTORY_CREDENTIALS}' '${env.ARTIFACTORY_HTTPS_URL}/api/search/prop?LATEST=true&repos=${env.artifactoryrepo}' | grep -i "${env.BUILD_PROFILE}/PRODUCT/${RELEASE}/"`
                        DEST_FOLDER=\${output##*:}
                        DEST_FOLDER=\${DEST_FOLDER%?}
                        curl -X DELETE -s -u '${env.ARTIFACTORY_CREDENTIALS}' https:\${DEST_FOLDER}?properties=LATEST
                        curl -X PUT -s -u '${env.ARTIFACTORY_CREDENTIALS}' '${env.ARTIFACTORY_HTTPS_URL}/api/storage/${env.artifactoryrepo}/${env.BUILD_PROFILE}/PRODUCT/${RELEASE}/${env.PRODUCT_VERSION}?properties=LATEST=true&recursive=0'
                """
        }

        //locking the current build
        cto.devops.jenkins.Utils.markBuildAsKeepForever("${JOB_NAME}")

        //Unlocking previously promoted build:
	sh '''
                JOB_URL1=`echo ${PREVIOUS_PROMOTED_JOB_URL#https://build13.cci.nokia.net/job/}`
                JOB_URL1=`echo ${JOB_URL1} | sed -e "s+/job/+ :: +g"`
                echo "JOB_URL1=${JOB_URL1}"
                if grep "^[^ ]* $PREVIOUS_PROMOTED_HELM [^ ]* \\"$JOB_URL1\\"" $COLLECTOR_PATH/$BUILD_PROFILE/promotedHelmdependencies.txt
                then
                        echo "helm is also promoted for this build ${PREVIOUS_PROMOTED_JOB_URL}/${PREVIOUS_PROMOTED_BUILD} so this will not be unlocked"
                else
                        echo "unlocking the previous build"
                        curl -X POST -s -u ${JENKINS_CREDENTIALS} ${PREVIOUS_PROMOTED_JOB_URL}/${PREVIOUS_PROMOTED_BUILD}/api/xml?depth=0 > keeplog.txt
                        if grep "<keepLog>true</keepLog>" keeplog.txt > /dev/null;
                        then
				echo "disabled unlocking for a while"
                                #curl -X POST -s -u ${JENKINS_CREDENTIALS} ${PREVIOUS_PROMOTED_JOB_URL}/${PREVIOUS_PROMOTED_BUILD}/toggleLogKeep
                        fi
                fi
       '''
}
 
