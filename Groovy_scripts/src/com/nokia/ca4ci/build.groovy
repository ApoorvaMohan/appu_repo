package com.nokia.ca4ci

def findVersion() {
        env.VERSION = readFile("${WORKSPACE}/devops_ci/scripts/buildscripts/ws/version.txt").trim()
}

def buildPlatformComponents(folderPaths) {
	
		def ERROR_STATUS = "0"	
		def FOLDERS = folderPaths.split(' ')
		def BUILD_PROFILE = env.BUILD_PROFILE.split(' ')
                
		for(profile in  BUILD_PROFILE) {
		
			def setenv = new com.nokia.ca4ci.setenv()
			setenv.envcp()
			env.profile = profile
			
			for(folder in FOLDERS) {
				dir(folder) {
					if( RELEASE != '' ) {
						sh "mvn versions:update-parent -U -DparentVersion=\"[${RELEASE_NUMBER}-SNAPSHOT]\" -DallowSnapshots=true"
 					}
					if( profile != 'Cloudera' ) {
						sh "export WORKSPACE=${WORKSPACE} && export profile=${profile} && mvn versions:set -U -DnewVersion=${rpm_version} -DnoClouderaRepo && mvn exec:exec -U -DapplyProfile -pl . -DnoClouderaRepo && mvn clean -f pom-${profile}.xml -DnoClouderaRepo && mvn deploy -fae -U -B -f pom-${profile}.xml -D${profile} -DnoClouderaRepo -Dmaven.wagon.http.ssl.insecure=true -DaltDeploymentRepository=cemod-mvn-snapshots::default::https://repo.lab.pl.alcatel-lucent.com/cemod-mvn-candidates/ -DskipTests=true"
                                        }
					else {
						sh "export WORKSPACE=${WORKSPACE} && export profile=${profile} && mvn versions:set -U -DnewVersion=${rpm_version} -DnoWandiscoRepo && mvn exec:exec -U -DapplyProfile -pl . -DnoWandiscoRepo && mvn clean -f pom-${profile}.xml -DnoWandiscoRepo && mvn deploy -fae -U -B -f pom-${profile}.xml -D${profile} -Dmaven.wagon.http.ssl.insecure=true -DnoWandiscoRepo -DaltDeploymentRepository=cemod-mvn-snapshots::default::https://repo.lab.pl.alcatel-lucent.com/cemod-mvn-candidates/ -DskipTests=true"
					}
				}
			}
		
		   if( env.publish == 'true' ) {
                	def lib = new com.nokia.ca4ci.publish()
		        lib.uploadFilesWithProfile(profile)
                
                	if( env.PROMOTE_TO_PRODUCT == 'true' ) {
		        	def promote = new com.nokia.ca4ci.promote()
                		promote.promoteToProduct(profile)
		        }               

			if( env.PROMOTE_TO_PLATFORM == 'true' ) {
                                def promote = new com.nokia.ca4ci.promoteToPlatform()
                                promote.promoteToPlatform(profile)
                        }
       		   }		
	       }
}

def buildAnyComponent(folderPaths) {
		def ERROR_STATUS = "0"
                def FOLDERS = folderPaths.split(' ')
                def BUILD_PROFILE = env.BUILD_PROFILE.split(' ')

		def setenv = new com.nokia.ca4ci.setenv()
                setenv.envcp()

		for(folder in FOLDERS) {
                                dir(folder) {
                                        if( RELEASE != '' ) {
                                                sh "mvn versions:update-parent -U -DparentVersion=\"[${RELEASE_NUMBER}-SNAPSHOT]\" -DallowSnapshots=true"
                                        }
					sh "export WORKSPACE=${WORKSPACE} && mvn -U versions:set -DnewVersion=${rpm_version} && mvn clean deploy -B -ff -U -Dmaven.javadoc.skip=true -Dmaven.wagon.http.ssl.insecure=true -DaltDeploymentRepository=cemod-mvn-snapshots::default::https://repo.lab.pl.alcatel-lucent.com/cemod-mvn-candidates/ -DskipTests=true"	
				}
		}
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

def buildAnyComponentWithoutDeploy(folderPaths) {
                def ERROR_STATUS = "0"
                def FOLDERS = folderPaths.split(' ')
                def BUILD_PROFILE = env.BUILD_PROFILE.split(' ')

                def setenv = new com.nokia.ca4ci.setenv()
                setenv.envcp()

                for(folder in FOLDERS) {
                                dir(folder) {
                                        if( RELEASE != '' ) {
                                                sh "mvn versions:update-parent -U -DparentVersion=\"[${RELEASE_NUMBER}-SNAPSHOT]\" -DallowSnapshots=true"
                                        }
                                        sh "export WORKSPACE=${WORKSPACE} && mvn -U versions:set -DnewVersion=${rpm_version} && mvn clean install -B -ff -U -Dmaven.javadoc.skip=true -Dmaven.wagon.http.ssl.insecure=true -DskipTests=true"
                                }
                }
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

def buildContentPacks(buildThese){

	def BUILD_THESE = buildThese.split(',')
	env.NGDB_HOME = "/opt/nsn/ngdb"
	
	def setenv = new com.nokia.ca4ci.setenv()
        setenv.envcp()

	if ( BUILD_THESE =~ "Adaptations" ){
		buildAdaptations()
	}
	if ( BUILD_THESE =~ "Universe" ){
		buildUniverse()
	}
	if ( BUILD_THESE =~ "Newuniverse" ){
		buildNewuniverse()
	}
	if ( BUILD_THESE =~ "Subscriptions" ){
                buildSubscriptions()
        }
	if ( BUILD_THESE =~ "Usecases" ){
		buildUsecases()
	}
	if ( BUILD_THESE =~ "Portlets" ){
		buildPortlets()
	}
	if ( BUILD_THESE =~ "Interface" ){
                buildInterface()
        }
	if ( BUILD_THESE =~ "Applications" ){
                buildApplications()
        }
	

	if( env.publish == 'true' ) {
                        def lib = new com.nokia.ca4ci.publish()
                        lib.uploadFilesWithoutProfile()

                        if( env.PROMOTE_TO_PRODUCT == 'true' ) {
                                def promote = new com.nokia.ca4ci.promote()
                                promote.promoteToProduct('')
                        }
        }

}

def buildAdaptations() {

        def ADAPTATION = env.ADAPTATION.split(' ')
        echo "Building adapatations"
        sh "cp -r CommonComponents/Adaptations/* ContentPacks/**/Adaptations/"
        sh "cp -r CommonComponents/Utilities/scripts/AdapterRPMGen_Scripts ContentPacks/**/Adaptations"
        sh "cp ContentPacks/**/Adaptations/AdapterRPMGen_Scripts/buildscripts/wrapper.sh ContentPacks/**/Adaptations"
        sh "chmod -R +x ContentPacks/**/Adaptations/*.sh"
        def adap_directory = component_path + "/Adaptations"

        dir(adap_directory) {
                for(adaptationName in ADAPTATION) {
                        echo "Building $adaptationName adaptation..."
                        sh "export WORKSPACE=${WORKSPACE} &&  export SAI_HOME=${NGDB_HOME} && export RELEASE_NUMBER=${RELEASE_NUMBER} && ./wrapper.sh ${adaptationName} ${BUILD_NUMBER}"
                }
        }
}

def buildUniverse(){

	echo "Building universe"
	def UNIVERSE = env.UNIVERSE.split(' ')
	sh "cp -r CommonComponents/Universe/* ContentPacks/**/Universe"
	sh "cp -r CommonComponents/Utilities/scripts/UniverseRPMGen_Scripts ContentPacks/**/Universe"
	sh "cp ContentPacks/**/Universe/UniverseRPMGen_Scripts/wrapper.sh ContentPacks/**/Universe"
	sh "chmod -R +x ContentPacks/**/Universe/*.sh"
	def univ_directory = component_path + "/Universe"

	dir(univ_directory) {
		for(universeName in UNIVERSE) {
			echo "Building $universeName ..."
			sh "export WORKSPACE=${WORKSPACE} &&  export SAI_HOME=${NGDB_HOME} && export RELEASE_NUMBER=${RELEASE_NUMBER} && ./wrapper.sh ${universeName} ${BUILD_NUMBER}"
		}
	}
}


def buildNewuniverse(){

        echo "Building New universe"
	def NEW_UNIVERSE = env.NEW_UNIVERSE.split(' ')
        sh "cp -r CommonComponents/Universe/* ContentPacks/**/Universe"
        sh "cp -r CommonComponents/Utilities/scripts/UniverseRPMGen_Scripts ContentPacks/**/Universe"
        sh "cp ContentPacks/**/Universe/UniverseRPMGen_Scripts/Universe_Builder/wrapper.sh ContentPacks/**/Universe"
        sh "chmod -R +x ContentPacks/**/Universe/*.sh"
	sh "chmod -R +x ContentPacks/**/Universe/UniverseRPMGen_Scripts/Universe_Builder/*.sh"
        def newuniv_directory = component_path + "/Universe"

        dir(newuniv_directory) {
                for(universeName in NEW_UNIVERSE) {
                        echo "Building $universeName ..."
                        sh "export WORKSPACE=${WORKSPACE} &&  export SAI_HOME=${NGDB_HOME} && export RELEASE_NUMBER=${RELEASE_NUMBER} && ./wrapper.sh ${universeName} ${BUILD_NUMBER}"
                }
        }
}

def buildSubscriptions(){
	
	echo "Building Subscriptions"
	def SUBSCRIPTION_MAPPING = env.SUBSCRIPTION_MAPPING.split(' ')
	sh "cp -r CommonComponents/Utilities/scripts/Subscriptions_Scripts/scripts ContentPacks/**/Subscriptions"
	def subs_directory = component_path + "/Subscriptions"

        dir(subs_directory) {
                for(mappingName in SUBSCRIPTION_MAPPING) {
			dir(mappingName){
				echo "Building $mappingName subscription mapping ..."
                        	sh "export WORKSPACE=${WORKSPACE} && mvn -U versions:set -DnewVersion=${rpm_version} && mvn clean package -e -DPackageRelease=${BUILD_NUMBER}"
			}
                }
        }	
}

def buildPortlets(){
	 echo "Building Portlets"
	 sh "export WORKSPACE=${WORKSPACE} && mvn -U -f ContentPacks/**/Portlets/pom.xml versions:set -DnewVersion=${rpm_version} && mvn clean deploy -f ContentPacks/**/Portlets/pom.xml -ff -U -Dmaven.javadoc.skip=true -Dmaven.wagon.http.ssl.insecure=true -DaltDeploymentRepository=cemod-mvn-snapshots::default::https://repo.lab.pl.alcatel-lucent.com/cemod-mvn-candidates/ -DskipTests=true"
}

def buildUsecases() {
	echo "Building Usecases"
	sh "export WORKSPACE=${WORKSPACE} && mvn -U -f ContentPacks/**/Usecases/pom.xml versions:set -DnewVersion=${rpm_version} && mvn clean install -f ContentPacks/**/Usecases/pom.xml -ff -U -Dmaven.javadoc.skip=true -Dmaven.wagon.http.ssl.insecure=true -DaltDeploymentRepository=cemod-mvn-snapshots::default::https://repo.lab.pl.alcatel-lucent.com/cemod-mvn-candidates/"
}

def buildInterface() {
        echo "Building Interface"
        sh "export WORKSPACE=${WORKSPACE} && mvn -U -f ContentPacks/**/interface/pom.xml versions:set -DnewVersion=${rpm_version} && mvn clean deploy -f ContentPacks/**/interface/pom.xml -ff -U -Dmaven.javadoc.skip=true -DskipTests=true  -Dmaven.wagon.http.ssl.insecure=true -DaltDeploymentRepository=cemod-mvn-snapshots::default::https://repo.lab.pl.alcatel-lucent.com/cemod-mvn-candidates/ "
}

def buildApplications() {
        echo "Building Application"
        sh "export WORKSPACE=${WORKSPACE} && mvn -U -f ContentPacks/**/Applications/pom.xml versions:set -DnewVersion=${rpm_version} && mvn clean install -f ContentPacks/**/Applications/pom.xml -ff -U -Dmaven.javadoc.skip=true -Dmaven.wagon.http.ssl.insecure=true -DaltDeploymentRepository=cemod-mvn-snapshots::default::https://repo.lab.pl.alcatel-lucent.com/cemod-mvn-candidates/ -DskipTests=true"
}
