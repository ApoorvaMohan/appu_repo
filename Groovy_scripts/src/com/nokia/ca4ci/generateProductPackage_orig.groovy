package com.nokia.ca4ci

def promoteComponentsToProduct() {
		
		env.artifactoryrepo = "cemod-yum-candidates-local"
		env.COLLECTOR_PATH = "devops_ci/collector/product"

	    sh '''
                source devops_ci/scripts/buildscripts/utilities/add_promotedcomponent_to_collector.sh
                cat ${COLLECTOR_PATH}/${BUILD_PROFILE}/promoteddependencies.txt
                cat ${COLLECTOR_PATH}/${BUILD_PROFILE}/buildnumber.txt
            '''
                env.PRODUCT_VERSION = readFile("${WORKSPACE}/${env.COLLECTOR_PATH}/${env.BUILD_PROFILE}/buildnumber.txt").trim()
                echo "PRODUCT_VERSION = ${env.PRODUCT_VERSION}"
                
          for (String i : readFile('versions.txt').split("\r?\n")) {
                def line = i.split(' ')
                def component = line[0] 
                def version = line[1] 
                echo "component = ${component}, version = ${version}"
               
		if( component != 'PLATFORM' ) {  
			//sh "curl -s -u '${env.ARTIFACTORY_CREDENTIALS}' '${env.ARTIFACTORY_HTTPS_URL}/api/versions/${env.artifactoryrepo}/${env.BUILD_PROFILE}/${component}/${version}?listFiles=1' > ${component}-files.json"
			CURL_STAT = sh (
			    script: "curl -s -u '${env.ARTIFACTORY_CREDENTIALS}' '${env.ARTIFACTORY_HTTPS_URL}/api/versions/${env.artifactoryrepo}/${env.BUILD_PROFILE}/${component}/${version}?listFiles=1' > ${component}-files.json",
			    returnStatus: true
			) == 0
			echo "Curl List status: ${CURL_STAT}"	

			sh "cat ${component}-files.json" // Debug
			sh "if grep -oE \"errors\" ${component}-files.json; then echo \"${component}/${version} doesnt exist\" && exit -1; fi"
                
                	// Load the list of files into an object
	                def componentJson = readJSON file: "${component}-files.json"
	
        	        // Create a directory for the profile and copy move each component's files there
                	echo "Create directory tree for this profile's new release candidate..."

	                def targetDir = "${env.artifactoryrepo}/${env.BUILD_PROFILE}/PRODUCT/${env.PRODUCT_VERSION}/${component}/"   

        	        sh "curl -X PUT -s -u '${env.ARTIFACTORY_CREDENTIALS}' '${env.ARTIFACTORY_HTTPS_URL}/${targetDir}'"
	
        	        // Copy each file to the profile's directory; this is all done on the service side
                	echo "Copy artifacts of component '${component}' to this profile's directory tree..."
	                for(artifact in componentJson.artifacts) {              
        	                def sourceFile = "${artifact.repo}/${artifact.path}"
                	        sh "curl -X POST -s -u '${env.ARTIFACTORY_CREDENTIALS}' '${env.ARTIFACTORY_HTTPS_URL}/api/copy/${sourceFile}?to=/${targetDir}&dryRun=0'"
                	}
      		} 
		else{
			sh "curl -s -u '${env.ARTIFACTORY_CREDENTIALS}' '${env.ARTIFACTORY_HTTPS_URL}/api/storage/${env.artifactoryrepo}/${env.BUILD_PROFILE}/${component}/${version}?list&deep=1' > ${component}-files.json"
                        sh "cat ${component}-files.json" // Debug
			sh "if grep -oE \"errors\" ${component}-files.json; then echo \"${component}/${version} doesnt exist\" && exit -1; fi"
                        
			// Load the list of files into an object
                        def componentJson = readJSON file: "${component}-files.json"

                        // Create a directory for the profile and copy move each component's files there
                        echo "Create directory tree for this profile's new release candidate..."

                        def targetDir = "${env.artifactoryrepo}/${env.BUILD_PROFILE}/PRODUCT/${env.PRODUCT_VERSION}/${component}/"

                        sh "curl -X PUT -s -u '${env.ARTIFACTORY_CREDENTIALS}' '${env.ARTIFACTORY_HTTPS_URL}/${targetDir}'"

                        // Copy each file to the profile's directory; this is all done on the service side
                        echo "Copy artifacts of component '${component}' to this profile's directory tree..."
			for(files in componentJson.files) {
                                def sourceFile = "${env.artifactoryrepo}/${env.BUILD_PROFILE}/${component}/${version}${files.uri}"
                                sh "curl -X POST -s -u '${env.ARTIFACTORY_CREDENTIALS}' '${env.ARTIFACTORY_HTTPS_URL}/api/copy/${sourceFile}?to=/${targetDir}&dryRun=0'"
                        }
		}
          }	
	 sshagent (credentials: ['8dfb365b-f6cd-4274-8b75-689036fa840b']) {
		dir('devops_ci') {	
		sh ''' 
			git config --global user.email "apoorva.m@nokia.com"
			git config --global user.name "M Apoorva"
			git commit -m "CEMODPBL-1:Promoted ${PACKAGE_DIRECTORY} version ${VERSION} to product: ${PRODUCT_VERSION}" collector/product/${BUILD_PROFILE}/buildnumber.txt collector/product/${BUILD_PROFILE}/promoteddependencies.txt
			git pull --rebase origin master
			#git push origin master
			git push origin master || git pull --rebase origin master && git push origin master
		'''
		}
	}

 }    
