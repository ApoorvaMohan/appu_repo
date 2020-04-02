package com.nokia.ca4ci

def buildAnyComponents(folderPaths) {
	
		def ERROR_STATUS = "0"	
		env.FOLDERS = folderPaths
		   sh '''
			source ${WORKSPACE}/devops_ci/scripts/buildscripts/env_cp.sh

		        for folder in ${FOLDERS}
		        do
			          cd $folder
			          if [  "${RELEASE}" != "" ]; then
			          	 mvn versions:update-parent "-DparentVersion=${RELEASE_NUMBER}-SNAPSHOT" -DallowSnapshots=true
			          fi
					  mvn -U versions:set -DnewVersion=${rpm_version} && mvn clean deploy -B -ff -U -Dmaven.javadoc.skip=true -Dmaven.wagon.http.ssl.insecure=true -DaltDeploymentRepository=cemod-mvn-snapshots::default::https://repo.lab.pl.alcatel-lucent.com/cemod-mvn-candidates/
			          cd -	
			done
		   '''
		   if( env.publish == 'true' ) {
                	def lib = new com.nokia.ca4ci.publish()
		        lib.uploadFilesWithoutProfile()
                
                	if( env.PROMOTE_TO_PRODUCT == 'true' ) {
		        	def promote = new com.nokia.ca4ci.promote()
                		promote.promoteToProduct('')
		        }               

			if( env.PROMOTE_TO_PLATFORM == 'true' ) {
                                def promote = new com.nokia.ca4ci.promoteToPlatform()
                                promote.promoteToPlatform('')
                        }
       		   }	
}
