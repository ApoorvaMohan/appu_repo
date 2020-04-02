package com.nokia.ca4ci.build

def findVersion() {
        env.VERSION = readFile("${WORKSPACE}/devops_ci/scripts/buildscripts/ws/version.txt").trim()
}

def buildComponent(folderPaths, buildType, deployToArtefactory = 'true', thirdpartyComponent) {
	echo "ca4cibuild==> buildComponent $folderPaths"
	def ERROR_STATUS = "0"	
	def FOLDERS = folderPaths.split(' ')
	downloadBuildThirdPartyDependencies()
	if(buildType == 'contentpack') {
		buildContentPacks(buildThese)
	} else if(buildType == 'profile') {
		echo "Starting profile build"
		buildProfileComponent(FOLDERS)
	} else if(buildType == 'nonprofile') {
		buildNonProfileComponent(FOLDERS, deployToArtefactory)
	}else if(buildType == 'ncmprofile') {
		buildNCMProfile(FOLDERS)
	}else if(buildType == 'ncmprofileOffline') {
                buildNCMProfileOffline(FOLDERS)
	}else if(buildType == 'sonar') {
                buildSonar(FOLDERS)
        }else if(buildType == 'sonar_with_profile') {
                buildSonarProfile(FOLDERS)
        }
}
def downloadBuildThirdPartyDependencies () {
              echo "Downloading thirdparty component $thirdpartyComponent " 
              if(thirdpartyComponent == "null" || thirdpartyComponent == null ) {
		      echo "No thirdparty component to download"	        
            }
           else{
               echo "Downloading thirdparty component" 
                    sh 'mkdir -p /nfsshare/nemesis/cemod/installers/' 
                    sh 'cd /nfsshare/nemesis/cemod/installers/ && jfrog rt download --url https://repo.lab.pl.alcatel-lucent.com --user $OS_USERNAME --apikey $OS_PASSWORD cemod-generic-pucontrolled/thirdparty/$thirdpartyComponent/* /nfsshare/nemesis/cemod/installers/'
					sh 'ls /nfsshare/nemesis/cemod/installers/thirdparty/$thirdpartyComponent/'
		   
		   }
	}	

def publishBuild() {
	findVersion()
		def lib = new com.nokia.ca4ci.publish()
		if(env.gerrit == "false" ){
		    if( env.featureBuild == "false") {
			if( env.buildType != 'contentpack' ) {
				if( env.publishJarInfo != 'false' ) {
					lib.publishJarsInfo()
				}
			}
		    }
		}
		if( env.publish == 'true' ) {
			lib.uploadFilesWithoutProfile()
		}

		if( env.PROMOTE_TO_PRODUCT == 'true' ) {
				def promote = new com.nokia.ca4ci.promote()
				promote.promoteToProduct('')
				if ( env.currentRelease != '0' ){
					if( env.PROMOTE_TWICE == 'true' ){
		                        	env.ORIG_RELEASE = env.RELEASE
	                		        //env.RELEASE = env.NEXT_RELEASE
        	                		//env.PROMOTE_TO_RELEASE='true'
					}
				}
		}

		if( env.PROMOTE_TO_PLATFORM == 'true' ) {
				def promote = new com.nokia.ca4ci.promoteToPlatform()
				promote.promoteToPlatform('')
				if( env.PROMOTE_TWICE == 'true' ){
                                        env.ORIG_RELEASE = env.RELEASE
                                        //env.RELEASE = env.NEXT_RELEASE
                                        //env.PROMOTE_TO_PLATFORMCOLLECTOR_RELEASE='true'
                                }
		}
		
		if( env.PROMOTE_TO_PLATFORMCOLLECTOR_RELEASE == 'true' ) {
                                def promote = new com.nokia.ca4ci.promoteToPlatform()
                                promote.promoteToPlatform('')
				env.RELEASE = env.CEMNOVA_RELEASE
				promote.promoteToPlatform('')
                                if( env.PROMOTE_TWICE == 'true' ){
					env.RELEASE = env.ORIG_RELEASE
                                }
                }
		
		if( env.PROMOTE_TO_RELEASE == 'true' ) {
				def promote = new com.nokia.ca4ci.promote()
                                promote.promoteToProduct('')
				env.RELEASE = env.CEMNOVA_RELEASE
				promote.promoteToProduct('')
                                if( env.PROMOTE_TWICE == 'true' ){
                                        env.RELEASE = env.ORIG_RELEASE
                                }
                }
}

def publishHelm(){
	def helm = new com.nokia.ca4ci.publish()
        helm.uploadFilesHelmchart()
	if ( PACKAGE_DIRECTORY == '#UMBRELLA_HELM' ) {
				env.artifactoryrepo = "cemod-yum-candidates-local"
		                env.COLLECTOR_PATH = "devops_ci/collector/product/${RELEASE}"
				def publish = new com.nokia.ca4ci.publish()
			        publish.uploadAnyFile("${env.artifactoryrepo}/${env.BUILD_PROFILE}/HELM/${RELEASE}/${env.VERSION}","devops_ci/collector/product/${RELEASE}/${BUILD_PROFILE}/promotedHelmdependencies.txt")
        }
}

def publishDocker(){
	if( skip_csfp_helm  == 'true' ) {
        	env.project_helm_version = env.VERSION
        }
	if( env.publishDocker == 'true' ) {
		def dock = new com.nokia.ca4ci.publish()
		dock.uploadDockerImage()
	}
	env.imageNames_backup = env.imageNames
	if( env.PROMOTE_HELM == 'true' && env.INSTALL == 'false' ) {
		//not needed anymore as umbrella helm doesnt updated all promoted components
		//if( env.publishHelm == 'false' ) {
                //	env.imageNames = "#" + env.imageNames
        	//}
	    //Enable below line for avoiding direct promotion from build job
//	    if ( env.installJobName == "" ){
                	def promote = new com.nokia.ca4ci.promote()
	                promote.promoteHelm()
		//comment below if-loop after fc_nova branch is merged to trunk
		if ( env.currentRelease == '0' ){
                	env.ORIG_RELEASE = env.RELEASE
                        env.RELEASE = '3'
			promote.promoteHelm()	
			env.RELEASE = env.ORIG_RELEASE
                }
		if ( env.currentRelease != '6-CT' ){
			if( env.PROMOTE_TWICE == 'true' ){
                        	env.ORIG_RELEASE = env.RELEASE
	                        env.RELEASE = env.NEXT_RELEASE
				//promote.promoteHelm()
				env.RELEASE = env.CEMNOVA_RELEASE
				//promote.promoteHelm()
				env.RELEASE = env.ORIG_RELEASE
        	        }
		}
                	env.imageNames = env.imageNames_backup
//	    }
//		else{
//			echo "**************************************\n"
//			echo "CANNOT PROMOTE WITHOUT INSTALL"
//		}
        }
}

def cleanUp() {
	sh "rm -rf installers"
}

def buildProfileComponent(folderPaths) {
		echo "Entered buildProfileComponent for $folderPaths" 
		def BUILD_PROFILE = env.BUILD_PROFILE.split(' ')
		for(profile in  BUILD_PROFILE) {
			echo "Before Triggering build for profile $profile" 
			triggerBuild(profile, folderPaths)
	    }
}

def buildNonProfileComponent(folderPaths, deployToArtefactory = 'true') {
		def ERROR_STATUS = "0"
		def BUILD_PROFILE = env.BUILD_PROFILE.split(' ')
		triggerBuild('', folderPaths, deployToArtefactory)
}

def triggerBuild(profile = '', folderPaths, deployToArtefactory = 'true') {
	def setenv = new com.nokia.ca4ci.setenv()
	setenv.envcp()
	echo "Triggering build for profile $profile" 
	for(folder in folderPaths) {
		dir(folder) {
			if ( PACKAGE_DIRECTORY == '#UMBRELLA_HELM' ) {
				if( RELEASE == '6-CT' ) {
					sh "export WORKSPACE=${WORKSPACE} && export masterHelmPromotedDependendecyFile=$masterHelmPromotedDependendecyFile && for i in \$(find . -name requirements.yaml);do echo \$i && sh ${WORKSPACE}/devops_ci/scripts/buildscripts/ResolveDependencyVersion.sh \$i ${WORKSPACE}/${masterHelmPromotedDependendecyFile}; done"
				}
				sh "export WORKSPACE=${WORKSPACE} && export helmPromotedDependendecyFile=$helmPromotedDependendecyFile && for i in \$(find . -name requirements.yaml);do echo \$i && sh ${WORKSPACE}/devops_ci/scripts/buildscripts/ResolveDependencyVersion.sh \$i ${WORKSPACE}/${helmPromotedDependendecyFile}; done"
			}
			if( RELEASE != '' ) {
				sh "mvn versions:update-parent -U -DparentVersion=\"[${PARENT_VERSION}-SNAPSHOT]\" -DallowSnapshots=true -DnoWandiscoRepo"
			}
			if( profile == 'Wandisco27' || profile == 'Wandisco23' ) {
				sh "export MAVEN_OPTS=\"-XX:+TieredCompilation -XX:TieredStopAtLevel=1\" && export WORKSPACE=${WORKSPACE} && export profile=${profile} && export talkocheck=${talkocheck} && mvn versions:set -U -DnewVersion=${rpm_version} -DnoClouderaRepo && mvn exec:exec -U -DapplyProfile -pl . -DnoClouderaRepo && mvn clean -f pom-${profile}.xml -DnoClouderaRepo && mvn deploy -fae -U -B -f pom-${profile}.xml -D${profile} -DnoClouderaRepo -Dmaven.wagon.http.ssl.insecure=true -D${deploy_profile} -Dtalkocheck=${talkocheck} -Dartifactory.username=${OS_USERNAME} -Dartifactory.password=${OS_PASSWORD}"
			} else if( profile == 'Cloudera' ) {
				sh "export MAVEN_OPTS=\"-XX:+TieredCompilation -XX:TieredStopAtLevel=1\" && export WORKSPACE=${WORKSPACE} && export profile=${profile} && export talkocheck=${talkocheck} && mvn versions:set -U -DnewVersion=${rpm_version} -DnoWandiscoRepo && mvn exec:exec -U -DapplyProfile -pl . -DnoWandiscoRepo && mvn clean -f pom-${profile}.xml -DnoWandiscoRepo && mvn deploy -fae -U -B -f pom-${profile}.xml -D${profile} -Dmaven.wagon.http.ssl.insecure=true -DnoWandiscoRepo -D${deploy_profile} -Dtalkocheck=${talkocheck} -Dartifactory.username=${OS_USERNAME} -Dartifactory.password=${OS_PASSWORD}"
			} else if( profile == null || profile == '') {
				if(deployToArtefactory == 'true') {
					sh "export MAVEN_OPTS=\"-XX:+TieredCompilation -XX:TieredStopAtLevel=1\" && export WORKSPACE=${WORKSPACE} && export talkocheck=${talkocheck} && mvn -U versions:set -DnewVersion=${rpm_version} && mvn clean deploy -B -ff -U -Dmaven.javadoc.skip=true -Dmaven.wagon.http.ssl.insecure=true -D${deploy_profile} -Dtalkocheck=${talkocheck} -Dartifactory.username=${OS_USERNAME} -Dartifactory.password=${OS_PASSWORD}"
				} else {
					sh "export MAVEN_OPTS=\"-XX:+TieredCompilation -XX:TieredStopAtLevel=1\" && export WORKSPACE=${WORKSPACE} && export talkocheck=${talkocheck} && mvn -U versions:set -DnewVersion=${rpm_version} && mvn clean install -B -ff -U -Dtalkocheck=${talkocheck} -Dmaven.javadoc.skip=true -Dmaven.wagon.http.ssl.insecure=true"
				}
			}
		}
	}
	if ( talkocheck == 'true' ) {
		sh '''
			for i in $(find . -name dependencies.txt);
			do
			                cat $i >> talko_dependencies.txt
			done
		'''
	}
	publishBuild()
}

def buildDocker(componentToBuild = '', deployToArtefactory = 'true') {
	def setenv = new com.nokia.ca4ci.setenv()
	setenv.envcp()
	echo "Triggering docker build for component $componentToBuild" 
	if(buildType != 'contentpack') {
		dir(componentToBuild + dockerBuildPath) {
			if(buildType == 'nonprofile') {
				sh "export MAVEN_OPTS=\"-XX:+TieredCompilation -XX:TieredStopAtLevel=1\" && export https_proxy=10.158.100.2:8080 && export WORKSPACE=${WORKSPACE} && mvn package dockerfile:build dockerfile:tag@tag-version  -f pom.xml -Dcontainer -Dcsfp.skip=${CSFP_SKIP} -Dhelm.folder.skip=${HELM_FOLDER_SKIP} -Dartifactory.username=${OS_USERNAME} -Dartifactory.password=${OS_PASSWORD}"	
			}  else {
				sh "export MAVEN_OPTS=\"-XX:+TieredCompilation -XX:TieredStopAtLevel=1\" && export https_proxy=10.158.100.2:8080 && export WORKSPACE=${WORKSPACE} && mvn package dockerfile:build dockerfile:tag@tag-version -DCloudera -f pom-Cloudera.xml -DnoWandiscoRepo -Dcontainer -Dcsfp.skip=${CSFP_SKIP} -Dhelm.folder.skip=${HELM_FOLDER_SKIP} -Dartifactory.username=${OS_USERNAME} -Dartifactory.password=${OS_PASSWORD}"
			}
		}	
		if( env.publishHelm == 'true' ) {
			publishHelm()
	        }
		publishDocker()
	}
}

def buildContentPacks(buildThese){

	def BUILD_THESE = buildThese.split(' ')
	env.NGDB_HOME = "/opt/nsn/ngdb"
	
	def setenv = new com.nokia.ca4ci.setenv()
        setenv.envcp()

	if ( BUILD_THESE =~ "Adaptations" ){
		buildAdaptations()
	}
	if ( BUILD_THESE =~ "Insights_adaptations" ){
                buildAIAdaptations()
        }
	if ( BUILD_THESE =~ "Universe" ){
		buildUniverse()
	}
	if ( BUILD_THESE =~ "Newuniverse" ){
		buildNewuniverse()
	}
	if ( BUILD_THESE =~ "InsightsMetadata" ){
                buildMetadata()
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
	if ( BUILD_THESE =~ "Utilities" ){
                buildUtilities()
        }
	publishBuild()
	if(env.DOCKERBUILD == "true") {	
		if( env.publishHelm == 'true' ) {
                	publishHelm()
	        }
		echo "imageNames = ${imageNames}"
		publishDocker()
	}
}

def buildAdaptations() {

        def ADAPTATION = env.ADAPTATION.split(' ')
        echo "Building adapatations"
        sh '''
		cp -r CommonComponents/Adaptations/* ContentPacks/**/Adaptations/
        	cp -r CommonComponents/Utilities/scripts/AdapterRPMGen_Scripts ContentPacks/**/Adaptations
        	cp ContentPacks/**/Adaptations/AdapterRPMGen_Scripts/buildscripts/wrapper.sh ContentPacks/**/Adaptations
        	chmod -R +x ContentPacks/**/Adaptations/*.sh
	'''
        def adap_directory = componentRepoPath + "/Adaptations"

        dir(adap_directory) {
                for(adaptationName in ADAPTATION) {
			adaptationName = adaptationName.replaceAll("\"","")
                        echo "Building $adaptationName adaptation..."
                        sh "export WORKSPACE=${WORKSPACE} &&  export SAI_HOME=${NGDB_HOME} && export RELEASE_NUMBER=${RELEASE_NUMBER} && ./wrapper.sh ${adaptationName} ${BUILD_NUMBER}"
                }
        }
}

def buildAIAdaptations() {

        def ADAPTATION = env.ADAPTATION.split(' ')
        echo "Building adapatations"
        sh '''
                cp -r CommonComponents/AI_Adaptations/* ContentPacks/**/Adaptations/
                cp -r CommonComponents/Utilities/scripts/AdapterRPMGen_Scripts ContentPacks/**/Adaptations
                cp ContentPacks/**/Adaptations/AdapterRPMGen_Scripts/buildscripts/wrapper.sh ContentPacks/**/Adaptations
                chmod -R +x ContentPacks/**/Adaptations/*.sh
        '''
        def adap_directory = componentRepoPath + "/Adaptations"

        dir(adap_directory) {
                for(adaptationName in ADAPTATION) {
                        adaptationName = adaptationName.replaceAll("\"","")
                        echo "Building $adaptationName adaptation..."
                        sh "export WORKSPACE=${WORKSPACE} &&  export SAI_HOME=${NGDB_HOME} && export RELEASE_NUMBER=${RELEASE_NUMBER} && ./wrapper.sh ${adaptationName} ${BUILD_NUMBER}"
                }
        }
}

def buildUniverse(){

	echo "Building universe"
	def UNIVERSE = env.UNIVERSE.split(' ')
	sh '''
		cp -r CommonComponents/Universe/* ContentPacks/**/Universe
		cp -r CommonComponents/Utilities/scripts/UniverseRPMGen_Scripts ContentPacks/**/Universe
		cp ContentPacks/**/Universe/UniverseRPMGen_Scripts/wrapper.sh ContentPacks/**/Universe
		chmod -R +x ContentPacks/**/Universe/*.sh
	'''
	def univ_directory = componentRepoPath + "/Universe"

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
	sh '''
		export productionMode=true
        	cp -r CommonComponents/Universe/* ContentPacks/**/Universe
	        cp -r CommonComponents/Utilities/scripts/UniverseRPMGen_Scripts ContentPacks/**/Universe
        	cp ContentPacks/**/Universe/UniverseRPMGen_Scripts/Universe_Builder/wrapper.sh ContentPacks/**/Universe
	        chmod -R +x ContentPacks/**/Universe/*.sh
		chmod -R +x ContentPacks/**/Universe/UniverseRPMGen_Scripts/Universe_Builder/*.sh
	'''
        def newuniv_directory = componentRepoPath + "/Universe"

        dir(newuniv_directory) {
                for(universeName in NEW_UNIVERSE) {
                        echo "Building $universeName ..."
                        sh "export productionMode=true && export WORKSPACE=${WORKSPACE} &&  export SAI_HOME=${NGDB_HOME} && export RELEASE_NUMBER=${RELEASE_NUMBER} && ./wrapper.sh ${universeName} ${BUILD_NUMBER}"
			if(env.DOCKERBUILD == "true") {
				def imageName = readFile "imagename.txt".trim()
				if(env.imageNames == "null") {
					env.imageNames = imageName.trim()
				} else {
         	                	env.imageNames = env.imageNames + " " + imageName.trim()
		                        echo "imageNames = ${imageNames}"
				}
			}
                }
        }
}

def buildMetadata(){

    echo "Building Insights Metadata"
	def INSIGHTS_METADATA = env.INSIGHTS_METADATA.split(' ')
	sh '''
		cp -r CommonComponents/Utilities/scripts/InsightsMetadataGen_Scripts $componentRepoPath
		cp $componentRepoPath/InsightsMetadataGen_Scripts/container/wrapper.sh $componentRepoPath
		chmod -R +x $componentRepoPath/*.sh
	'''
	dir(componentRepoPath) {
		 for(metadata in INSIGHTS_METADATA) {
			 echo "Building insights metadata for $metadata ..."
			 sh "export WORKSPACE=${WORKSPACE} &&  export SAI_HOME=${NGDB_HOME} && export RELEASE_NUMBER=${RELEASE_NUMBER} && ./wrapper.sh ${metadata} ${BUILD_NUMBER}"
		
			if(env.DOCKERBUILD == "true") {
					def imageName = readFile "imagename.txt".trim()
					if(env.imageNames == "null") {
						env.imageNames = imageName.trim()
					} else {
									env.imageNames = env.imageNames + " " + imageName.trim()
									echo "imageNames = ${imageNames}"
					}
				}
			}
	}
}


def buildSubscriptions(){
	
	echo "Building Subscriptions"
	def SUBSCRIPTION_MAPPING = env.SUBSCRIPTION_MAPPING.split(' ')
	sh "cp -r CommonComponents/Utilities/scripts/Subscriptions_Scripts/scripts ContentPacks/**/Subscriptions"
	def subs_directory = componentRepoPath + "/Subscriptions"

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
	 if( RELEASE != '' ) {
         	sh "mvn versions:update-parent -U -f ContentPacks/**/Portlets/pom.xml -DparentVersion=\"[${PARENT_VERSION}-SNAPSHOT]\" -DallowSnapshots=true"
         }
	 sh "export WORKSPACE=${WORKSPACE} && mvn -U -f ContentPacks/**/Portlets/pom.xml versions:set -DnewVersion=${rpm_version} && mvn clean deploy -f ContentPacks/**/Portlets/pom.xml -ff -U -Dmaven.javadoc.skip=true -Dmaven.wagon.http.ssl.insecure=true -D${deploy_profile} -Dartifactory.username=${OS_USERNAME} -Dartifactory.password=${OS_PASSWORD}"
}

def buildUsecases() {
	echo "Building Usecases"
	if( RELEASE != '' ) {
                sh "mvn versions:update-parent -U -f ContentPacks/**/Usecases/pom.xml -DparentVersion=\"[${PARENT_VERSION}-SNAPSHOT]\" -DallowSnapshots=true"
        }
	sh "export WORKSPACE=${WORKSPACE} && mvn -U -f ContentPacks/**/Usecases/pom.xml versions:set -DnewVersion=${rpm_version} && mvn clean install -f ContentPacks/**/Usecases/pom.xml -ff -U -Dmaven.javadoc.skip=true -Dmaven.wagon.http.ssl.insecure=true -D${deploy_profile} -Dartifactory.username=${OS_USERNAME} -Dartifactory.password=${OS_PASSWORD}"
	if(env.DOCKERBUILD == "true") {
		sh "export MAVEN_OPTS=\"-XX:+TieredCompilation -XX:TieredStopAtLevel=1\" && export https_proxy=10.158.100.2:8080 && export WORKSPACE=${WORKSPACE} && mvn package dockerfile:build dockerfile:tag@tag-version -f ContentPacks/**/Usecases/pom.xml -Dcontainer  -Dcsfp.skip=${CSFP_SKIP} -Dhelm.folder.skip=${HELM_FOLDER_SKIP} -Dartifactory.username=${OS_USERNAME} -Dartifactory.password=${OS_PASSWORD}"
	}
}

def buildInterface() {
        echo "Building Interface"
	if( RELEASE != '' ) {
                sh "mvn versions:update-parent -U -f ContentPacks/**/interface/pom.xml -DparentVersion=\"[${PARENT_VERSION}-SNAPSHOT]\" -DallowSnapshots=true"
        }
        sh "export WORKSPACE=${WORKSPACE} && mvn -U -f ContentPacks/**/interface/pom.xml versions:set -DnewVersion=${rpm_version} && mvn clean deploy -f ContentPacks/**/interface/pom.xml -ff -U -Dmaven.javadoc.skip=true -Dmaven.wagon.http.ssl.insecure=true -D${deploy_profile} -Dartifactory.username=${OS_USERNAME} -Dartifactory.password=${OS_PASSWORD}"
}

def buildApplications() {
        echo "Building Application"
	if( RELEASE != '' ) {
                sh "mvn versions:update-parent -U -f ContentPacks/**/Applications/pom.xml -DparentVersion=\"[${PARENT_VERSION}-SNAPSHOT]\" -DallowSnapshots=true"
        }
        sh "export WORKSPACE=${WORKSPACE} && mvn -U -f ContentPacks/**/Applications/pom.xml versions:set -DnewVersion=${rpm_version} && mvn clean install -f ContentPacks/**/Applications/pom.xml -ff -U -Dmaven.javadoc.skip=true -Dmaven.wagon.http.ssl.insecure=true -D${deploy_profile} -Dartifactory.username=${OS_USERNAME} -Dartifactory.password=${OS_PASSWORD}"
}

def buildUtilities() {
        echo "Building Utilities"
        sh "export WORKSPACE=${WORKSPACE} && cd CommonComponents && zip -r --exclude=*.git*  Utilities-${rpm_version}.zip Utilities && ls -latr && mkdir -p ${WORKSPACE}/installers && cp Utilities-${rpm_version}.zip ${WORKSPACE}/installers"
}

def buildSonar(folders){
        def setenv = new com.nokia.ca4ci.setenv()
        setenv.envcp()
	echo "Triggering sonar builds"
        for(folder in folders) {
                dir(folder) {
		if( RELEASE != '' ) {
                                sh "mvn versions:update-parent -U -DparentVersion=\"[${PARENT_VERSION}-SNAPSHOT]\" -DallowSnapshots=true -DnoWandiscoRepo"
                }
		sh "export WORKSPACE=${WORKSPACE} && export sonar_profile=${sonar_profile} && export sonar_branch=${sonar_branch} &&  mvn clean install && mvn clover2:instrument clover2:aggregate clover2:clover sonar:sonar  -Psonar-clover-analysis -B -ff -U -Dmaven.javadoc.skip=true -Dmaven.wagon.http.ssl.insecure=true -Dsonar.login=b66b78a4fc2cc6ada2c98b757325dba9f1e917fb '-Dsonar.profile=${sonar_profile}' -Dsonar.branch=${sonar_branch} -Dsonar.exclusions=**/*.xml,**/*.XML"
		sh "export WORKSPACE=${WORKSPACE} && export sonar_branch=${sonar_branch} && mvn clean install && mvn clover2:instrument clover2:aggregate clover2:clover sonar:sonar -Psonar-clover-analysis -B -ff -U -Dmaven.javadoc.skip=true -Dmaven.wagon.http.ssl.insecure=true -Dsonar.login=b66b78a4fc2cc6ada2c98b757325dba9f1e917fb '-Dsonar.profile=FindBugs Security Audit'  -Dsonar.language=java -Dsonar.branch=${sonar_branch}security -Dsonar.exclusions=**/*.xml,**/*.XML"
	}
}
}

def buildSonarProfile(folders) {
		def setenv = new com.nokia.ca4ci.setenv()
	        setenv.envcp()
                echo "Entered sonar build with profiles"
                def BUILD_PROFILE = env.BUILD_PROFILE.split(' ')
                for(profile in  BUILD_PROFILE) {
			for(folder in folders) {
                		dir(folder) {
					if( RELEASE != '' ) {
                        		        sh "mvn versions:update-parent -U -DparentVersion=\"[${PARENT_VERSION}-SNAPSHOT]\" -DallowSnapshots=true -DnoWandiscoRepo"
		                        }
		                	sh "export WORKSPACE=${WORKSPACE} && export profile=${profile} && export sonar_profile=${sonar_profile} && export sonar_branch=${sonar_branch} && mvn exec:exec -B -U -DapplyProfile -pl . -Dprofile=${profile} && mvn clean install -f pom-${profile}.xml  &&  mvn clover2:instrument clover2:aggregate clover2:clover sonar:sonar -Psonar-clover-analysis -Dsonar.language=java -f pom-${profile}.xml -Dmaven.wagon.http.ssl.insecure=true -Dsonar.login=b66b78a4fc2cc6ada2c98b757325dba9f1e917fb '-Dsonar.profile=${sonar_profile}' -Dsonar.branch=${sonar_branch} -Dsonar.exclusions=**/*.xml,**/*.XML"
					sh "export WORKSPACE=${WORKSPACE} && export profile=${profile} && export sonar_branch=${sonar_branch} && mvn exec:exec -B -U -DapplyProfile -pl . -Dprofile=${profile} && mvn clean install -f pom-${profile}.xml  &&  mvn clover2:instrument clover2:aggregate clover2:clover sonar:sonar -Psonar-clover-analysis -Dsonar.language=java -f pom-${profile}.xml -Dmaven.wagon.http.ssl.insecure=true -Dsonar.login=b66b78a4fc2cc6ada2c98b757325dba9f1e917fb '-Dsonar.profile=FindBugs Security Audit' -Dsonar.branch=${sonar_branch}security  -Dsonar.exclusions=**/*.xml,**/*.XML"
        			}	
            		}
		}
}

def buildNCMProfileOffline(folders){
        def setenv = new com.nokia.ca4ci.setenv()
        setenv.envcp()
        echo "Triggering NCM profile build"
        for(folder in folders) {
                dir(folder) {
			sh "export WORKSPACE=${WORKSPACE} && export helmPromotedDependendecyFile=$helmPromotedDependendecyFile && for i in \$(find . -name app_list.tpl);do echo \$i && sh ${WORKSPACE}/devops_ci/scripts/buildscripts/ResolveDependencyVersion.sh \$i ${WORKSPACE}/${helmPromotedDependendecyFile}; done"
			sh '''
				wget $exporter_path
				chmod 777 exporter
				cat app_list.tpl
				./exporter --tar --clean --name customer-insights-profile-${project_helm_version} --multi 1000 app_list.tpl values.tpl --repo 'https://repo.lab.pl.alcatel-lucent.com/cemnova-helm-incubator/'
			'''
			publishHelm()
        	}
	}
}

def buildNCMProfile(folders){
        def setenv = new com.nokia.ca4ci.setenv()
        setenv.envcp()
        echo "Triggering NCM profile build"
        for(folder in folders) {
                dir(folder) {
			sh "export WORKSPACE=${WORKSPACE} && export helmPromotedDependendecyFile=$helmPromotedDependendecyFile && for i in \$(find . -name app_list.tpl);do echo \$i && sh ${WORKSPACE}/devops_ci/scripts/buildscripts/ResolveDependencyVersion.sh \$i ${WORKSPACE}/${helmPromotedDependendecyFile}; done"
			sh '''
			     tar -czvf ci-profile-${project_helm_version}.tgz *.tpl
			     ls
			'''
			publishHelm()
		}	
	}		
	env.artifactoryrepo = "cemod-yum-candidates-local"
	def publish = new com.nokia.ca4ci.publish()
	publish.uploadAnyFile("${env.artifactoryrepo}/${env.BUILD_PROFILE}/HELM/${RELEASE}/${env.project_helm_version}","devops_ci/collector/product/${RELEASE}/${BUILD_PROFILE}/promotedHelmdependencies.txt")
}


def callInstallJob() {

if(env.imageNames == null){
                echo "ImageName variable is not defined"
                currentBuild.result = 'FAILURE'
                return
        }
        if(env.VERSION == null){
                echo "VERSION variable is not defined "
                currentBuild.result = 'FAILURE'
                return
        }
        if(env.DOCKER_BUILD_NAME == null){
                echo "DOCKER_BUILD_NAME variable is not defined "
                currentBuild.result = 'FAILURE'
                return
        }
        if(env.DOCKER_BUILD_NUMBER == null){
                echo "DOCKER_BUILD_NUMBER variable is not defined "
                currentBuild.result = 'FAILURE'
                return
        }
	build job: env.installJobName, parameters: [string(name: 'GIT_URL', value: env.componentFullUrl),string(name: 'COMMIT_HASH', value: env.commitHash),string(name: 'CHART_NAME', value: env.imageNames), string(name: 'CHART_VERSION', value: env.VERSION), string(name: 'DOCKER_BUILD_NUMBER', value: env.DOCKER_BUILD_NUMBER), string(name: 'DOCKER_BUILD_NAME', value: env.DOCKER_BUILD_NAME), string(name: 'RELEASE', value: env.RELEASE), booleanParam(name: 'INSTALL', value: true), booleanParam(name: 'TEST', value: true), booleanParam(name: 'PROMOTE', value: env.PROMOTE_HELM), booleanParam(name: 'PROMOTE_TWICE', value: env.PROMOTE_TWICE)], wait: false
}
