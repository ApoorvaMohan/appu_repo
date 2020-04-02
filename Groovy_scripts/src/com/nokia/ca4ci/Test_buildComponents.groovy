package com.nokia.ca4ci

def findVersion() {
        env.VERSION = readFile("${WORKSPACE}/devops_ci/scripts/buildscripts/ws/version.txt").trim()
}

def buildPlatformComponents(folderPaths) {
	
		def ERROR_STATUS = "0"	
		env.FOLDERS = folderPaths
                def BUILD_PROFILE = env.BUILD_PROFILE.split(' ')
                for(profile in  BUILD_PROFILE) {
		env.profile = profile
		   sh '''
   			EXIT=0
			echo "Building with $profile ..."
			source ${WORKSPACE}/devops_ci/scripts/buildscripts/env_cp.sh $profile

		        for folder in ${FOLDERS}
		        do
			          cd $folder
			          if [  "${RELEASE}" != "" ]; then
			          	 mvn versions:update-parent "-DparentVersion=${RELEASE_NUMBER}-SNAPSHOT" -DallowSnapshots=true
			          fi
			          if [ "${profile}" != "Cloudera57" ]; then
			                mvn versions:set -U -DnewVersion=${rpm_version} -DnoClouderaRepo && mvn exec:exec -U -DapplyProfile -pl . -DnoClouderaRepo && mvn clean -f pom-${profile}.xml -DnoClouderaRepo && mvn install -fae -U -f pom-${profile}.xml -D${profile} -DnoClouderaRepo -Dmaven.wagon.http.ssl.insecure=true -DskipTests || EXIT=$?
           			  else
			                mvn versions:set -U -DnewVersion=${rpm_version} -DnoWandiscoRepo && mvn exec:exec -U -DapplyProfile -pl . -DnoWandiscoRepo && mvn clean -f pom-${profile}.xml -DnoWandiscoRepo && mvn install -fae -U -f pom-${profile}.xml -D${profile} -Dmaven.wagon.http.ssl.insecure=true -DnoWandiscoRepo -DskipTests || EXIT=$?
           			  fi
			          cd -	
				  if [ $EXIT != 0 ]; then break; fi
			 done
			 echo "EXIT = $EXIT" > exitstatus.properties
		   '''
		   def props1 = readProperties  file: 'exitstatus.properties'
		   def EXIT = props1['EXIT']
		   if( EXIT != '0' ) {
                   	ERROR_STATUS = "1"
			echo "there are failures so continuing with other profile"
			continue
                   }
		   if( env.publish == 'true' ) {
			def lib = new com.nokia.ca4ci.Test_publish()
                        lib.uploadJars()


//                	def lib = new com.nokia.ca4ci.publish()
//		        lib.uploadFilesWithProfile(profile)
                
  //              	if( env.PROMOTE_TO_PRODUCT == 'true' ) {
//		        	def promote = new com.nokia.ca4ci.promote()
  //              		promote.promoteToProduct(profile)
//		        }               
//
//			if( env.PROMOTE_TO_PLATFORM == 'true' ) {
  //                              def promote = new com.nokia.ca4ci.promoteToPlatform()
    //                            promote.promoteToPlatform(profile)
      //                  }
       	//	   }		
	       }
               if( ERROR_STATUS != '0' ) {
                 	currentBuild.result = 'FAILURE'
                 	echo "There are failures, check full console log"
                 	return
               }
}

def buildAdaptations() {
}

def buildPortlets(){
}

def builduniverse() {
}

def buildUsecases() {
}

