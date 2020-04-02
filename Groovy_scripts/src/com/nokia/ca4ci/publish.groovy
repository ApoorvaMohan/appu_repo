package com.nokia.ca4ci

def findVersion() {
	env.VERSION = readFile("${WORKSPACE}/devops_ci/scripts/buildscripts/ws/version.txt").trim()
}

def uploadFilesWithProfile(profile) {
		findVersion()
		publish_repo = "cemod-yum-candidates"

		if(env.gerrit == "true") {
                        publish_repo = "cemod-yum-inprogress"
                }
                if(featureBuild == "true") {
                        publish_repo = "cemod-generic-inprogress"
                }	
		
		def server = Artifactory.newServer url: env.ARTIFACTORY_HTTPS_URL, credentialsId: 'cemod-jenkins-for-artifactory'
                        echo "Upload compenent build for profile '${profile}'"
                        def uploadSpec = """{
        	                "files": [
                	        {
                        	            "pattern": "installers/${profile}/**/*.rpm",
                                	    "target": "${publish_repo}/${profile}/${params.PACKAGE_DIRECTORY}/${env.RELEASE}/${env.VERSION}/",
	                                    "props": "version=${env.VERSION};profile=${profile}"
        	                }
                	                 ]
			}"""            
                        def buildInfo = Artifactory.newBuildInfo()              
                        buildInfo.env.capture = true
                        buildInfo.env.collect()              
			buildInfo.name =  buildInfo.name + "_rpm"
			cto.devops.jenkins.Utils.updateBuildRetention(currentBuild, buildInfo)
                        server.upload(uploadSpec, buildInfo)
                        server.publishBuildInfo(buildInfo) 
		if(env.gerrit == "false" ){
		   if(env.featureBuild == "false") {
                        def display = "Promote RPM " + profile
                        def promotionConfig = [
                              'buildName'          : buildInfo.name,
                              'buildNumber'        : buildInfo.number,
                              'status'             : 'Released',
                              'targetRepo'         : 'cemod-yum-releases',
                              'sourceRepo'         : 'cemod-yum-candidates-local',
                              'includeDependencies': false,
                              'copy'               : true, // "copy" must be used because "move" requires delete permission
                              'failFast'           : true
                            ]
    
                        Artifactory.addInteractivePromotion server: server, promotionConfig: promotionConfig, displayName: display
		    }
		}
}

def uploadFilesWithoutProfile() {
		findVersion()
		pattern = "installers/**/*.rpm"
		publish_repo = "cemod-yum-candidates"

		if(env.gerrit == "true") {
			publish_repo = "cemod-yum-inprogress"
		}
		if(featureBuild == "true") {
                        publish_repo = "cemod-generic-inprogress"
                }

		//Inorder to publish source files to a different location below if condition is applied
		if(RELEASE_DIR != "null"){
		    env.BUILD_PROFILE = env.RELEASE_DIR
		    pattern = "installers/*.zip"
		}

		def BUILD_PROFILE = env.BUILD_PROFILE.split(' ')
                def server = Artifactory.newServer url: env.ARTIFACTORY_HTTPS_URL, credentialsId: 'cemod-jenkins-for-artifactory'
                for(profile in  BUILD_PROFILE) {
                        echo "Upload compenent build for profile '${profile}'"
                        def uploadSpec = """{
                                "files": [
                                {
                                            "pattern": "${pattern}",
                                            "target": "${publish_repo}/${profile}/${params.PACKAGE_DIRECTORY}/${env.RELEASE}/${env.VERSION}/",
                                            "props": "version=${env.VERSION};profile=${profile}"
                                }
                                         ]
                        }"""
                        def buildInfo = Artifactory.newBuildInfo()
                        buildInfo.env.capture = true
                        buildInfo.env.collect()
			buildInfo.name =  buildInfo.name + "_rpm"
			cto.devops.jenkins.Utils.updateBuildRetention(currentBuild, buildInfo)
                        server.upload(uploadSpec, buildInfo)
                        server.publishBuildInfo(buildInfo)

		    if(env.gerrit == "false"){
		      if(env.featureBuild == "false") {
			def display = "Promote RPM "
                        def promotionConfig = [
                              'buildName'          : buildInfo.name,
                              'buildNumber'        : buildInfo.number,
                              'status'             : 'Released',
                              'targetRepo'         : 'cemod-yum-releases',
                              'sourceRepo'         : 'cemod-yum-candidates-local',
                              'includeDependencies': false,
                              'copy'               : true, // "copy" must be used because "move" requires delete permission
                              'failFast'           : true
                            ]

                        Artifactory.addInteractivePromotion server: server, promotionConfig: promotionConfig, displayName: display
		      }
		   }

    }
}

def uploadISO() {

                publish_repo = "cemod-yum-candidates"
                def server = Artifactory.newServer url: env.ARTIFACTORY_HTTPS_URL, credentialsId: 'cemod-jenkins-for-artifactory'
                        echo "Upload compenent build for profile '${BUILD_PROFILE}'"
                        def uploadSpec = """{
                                "files": [
                                {
                                            "pattern": "*.iso",
                                            "target": "${publish_repo}/${BUILD_PROFILE}/ISO/${env.PRODUCT_NAME}/${env.RELEASE}/${env.ISO_VERSION}/",
                                            "props": "version=${env.ISO_VERSION};profile=${BUILD_PROFILE}"
                                }
                                         ]
                        }"""
                        def buildInfo = Artifactory.newBuildInfo()
                        buildInfo.env.capture = true
                        buildInfo.env.collect()
                        buildInfo.name =  buildInfo.name
                        cto.devops.jenkins.Utils.updateBuildRetention(currentBuild, buildInfo)
                        server.upload(uploadSpec, buildInfo)
                        server.publishBuildInfo(buildInfo)
                        def display = "Promote ISO "
                        def promotionConfig = [
                              'buildName'          : buildInfo.name,
                              'buildNumber'        : buildInfo.number,
                              'status'             : 'Released',
                              'targetRepo'         : 'cemod-yum-releases',
                              'sourceRepo'         : 'cemod-yum-candidates-local',
                              'includeDependencies': false,
                              'copy'               : true, // "copy" must be used because "move" requires delete permission
                              'failFast'           : true
                            ]

                        Artifactory.addInteractivePromotion server: server, promotionConfig: promotionConfig, displayName: display
}
	



def publishJarsInfo() {
                        def server = Artifactory.newServer url: env.ARTIFACTORY_HTTPS_URL, credentialsId: 'cemod-jenkins-for-artifactory'

			def buildInfo = readJSON file: componentToBuild + "/target/build-info.json"

			def promotionConfig = [
		              'buildName'          : buildInfo.name,
		              'buildNumber'        : buildInfo.number,
		              'status'             : 'Released',
		              'targetRepo'         : 'cemod-mvn-releases',
		              'sourceRepo'         : 'cemod-mvn-candidates-local',
		              'includeDependencies': false,
		              'copy'               : true, // "copy" must be used because "move" requires delete permission
		              'failFast'           : true
		        ]

                        Artifactory.addInteractivePromotion server: server, promotionConfig: promotionConfig, displayName: "Promote jars to releases"


}


def uploadFilesHelmchart() {
		        def server = Artifactory.newServer url: env.ARTIFACTORY_HTTPS_URL, credentialsId: 'cemod-jenkins-for-artifactory'
			def uploadSpec = """{
                                	"files": [
	                                {
        	                                    "pattern": "**/target/**/*.tgz",
                	                            "target": "cemnova-helm-incubator/",
                        	                    "props": "version=${env.VERSION}"
                                	}
                                         	]
	            	}"""
			if(env.buildType == "ncmprofile"){
			        uploadSpec = """{
                                "files": [
                	                {
                        	                    "pattern": "*-profile-*.tgz",
                                	            "target": "cemnova-helm-incubator/",
                                        	    "props": "version=${env.VERSION}"
                               		},
                               		{
                        	                    "pattern": "*-setup-*.tgz",
                                	            "target": "cemnova-helm-incubator/",
                                        	    "props": "version=${env.VERSION}"
                               		}
                                         ]
        	                }"""
			}
                        def buildInfo = Artifactory.newBuildInfo()
                        buildInfo.env.capture = true
                        buildInfo.env.collect()
			env.DOCKER_BUILD_NAME =  buildInfo.name
                        env.DOCKER_BUILD_NUMBER = buildInfo.number
			buildInfo.name =  buildInfo.name + "_helm"
	                cto.devops.jenkins.Utils.updateBuildRetention(currentBuild, buildInfo)
                        server.upload(uploadSpec, buildInfo)
                        server.publishBuildInfo(buildInfo)

			def display = "Promote helm to releases"
                        def promotionConfig = [
                              'buildName'          : buildInfo.name,
                              'buildNumber'        : buildInfo.number,
                              'status'             : 'Released',
                              'targetRepo'         : 'cemnova-helm-stable-local',
                              'sourceRepo'         : 'cemnova-helm-incubator-local',
                              'includeDependencies': false,
                              'copy'               : true, // "copy" must be used because "move" requires delete permission
                              'failFast'           : true
                            ]

                        Artifactory.addInteractivePromotion server: server, promotionConfig: promotionConfig, displayName: display

    
}

def uploadDockerImage() {
			env.SOURCE_REPO = "cemod-docker-candidates"
			env.tag_name = env.project_helm_version
			env.latest_tag_name = "latest" 
			def server = Artifactory.newServer url: env.ARTIFACTORY_HTTPS_URL, credentialsId: 'cemod-jenkins-for-artifactory'
			def Docker = Artifactory.docker server: server, host: env.DOCKER_HOST
			def buildInfoDocker = Artifactory.newBuildInfo()
			def imageNamesList = env.imageNames.split(' ')
			for(imageName in imageNamesList) {
				env.DOCKER_IMAGE = imageName
				def buildInfoDockerImage = Docker.push "${SOURCE_REPO}.${ARTIFACTORY_URL}/${DOCKER_IMAGE}:${tag_name}", env.SOURCE_REPO
				def buildInfoDockerLatest = Docker.push "${SOURCE_REPO}.${ARTIFACTORY_URL}/${DOCKER_IMAGE}:${latest_tag_name}", env.SOURCE_REPO
				//Append the "latest" tagged image buildinfo object with version tagged image
				buildInfoDocker.append buildInfoDockerImage
				buildInfoDocker.append buildInfoDockerLatest
			}
			buildInfoDocker.env.capture = true
			//buildinfo is not proper if any enable below line
			//buildInfoDocker.name = buildInfoDocker.name + "_docker"
			cto.devops.jenkins.Utils.updateBuildRetention(currentBuild, buildInfoDocker)
			publishBuildInfo buildInfo: buildInfoDocker, server: server

                        def displayDocker = "Promote Docker to releases"
                        def promotionConfigDocker = [
                              'buildName'          : buildInfoDocker.name,
                              'buildNumber'        : buildInfoDocker.number,
                              'status'             : 'Released',
                              'targetRepo'         : 'cemod-docker-releases',
                              'sourceRepo'         : 'cemod-docker-candidates-local',
                              'includeDependencies': false,
                              'copy'               : true, // "copy" must be used because "move" requires delete permission
                              'failFast'           : true
                            ]
			env.DOCKER_BUILD_NAME =  buildInfoDocker.name
			env.DOCKER_BUILD_NUMBER = buildInfoDocker.number
			Artifactory.addInteractivePromotion server: server, promotionConfig: promotionConfigDocker, displayName: displayDocker
}

def uploadAnyFile(targetPath,filesToUpload){
            		def server = Artifactory.newServer url: env.ARTIFACTORY_HTTPS_URL, credentialsId: 'cemod-jenkins-for-artifactory'
			env.targetPath = targetPath
			env.filesToUpload = filesToUpload
			echo "filesToUpload = ${filesToUpload}"
			echo "targetPath = ${targetPath}"
			filesToUpload = env.filesToUpload.split(' ')
			def filenumber = 0
			for(fileName in filesToUpload) {
	                        echo "Uploading ${fileName}"
        	                def uploadSpec = """{
                	                "files": [
                        	        {
                                	            "pattern": "${fileName}",
                                        	    "target": "${targetPath}/"
        	                        }
                	                         ]
                        	}"""
	                        def buildInfo = Artifactory.newBuildInfo()
        	                buildInfo.env.capture = true
                	        buildInfo.env.collect()
                        	buildInfo.name =  buildInfo.name + "_" + filenumber 
	                        cto.devops.jenkins.Utils.updateBuildRetention(currentBuild, buildInfo)
        	                server.upload(uploadSpec, buildInfo)
                	        server.publishBuildInfo(buildInfo)
				filenumber = filenumber + 1
			}
}

def uploadDockerHelmImage() {
			env.SOURCE_REPO = "cemod-docker-candidates"
			env.DOCKER_IMAGE = env.imageName
			env.tag_name = env.VERSION
			env.latest_tag_name = "latest" 
			env.ARTIFACTORY_URL = "repo.lab.pl.alcatel-lucent.com"
			def server = Artifactory.newServer url: env.ARTIFACTORY_HTTPS_URL, credentialsId: 'cemod-jenkins-for-artifactory'
			def Docker = Artifactory.docker server: server, host: env.DOCKER_HOST

			def uploadSpec = """{
                                "files": [
                                {
                                            "pattern": "**/target/**/*.tgz",
                                            "target": "cemnova-helm-incubator/",
                                            "props": "version=${env.VERSION}"
                                }
                                         ]
                        }"""
                        def buildInfo = Artifactory.newBuildInfo()	
			def buildInfoDocker = Docker.push "${SOURCE_REPO}.${ARTIFACTORY_URL}/${DOCKER_IMAGE}:${tag_name}", env.SOURCE_REPO
			def buildInfoDockerLatest = Docker.push "${SOURCE_REPO}.${ARTIFACTORY_URL}/${DOCKER_IMAGE}:${latest_tag_name}", env.SOURCE_REPO
			//Comment below line if its failing
			buildInfo.env.collect()
			
			buildInfo.append buildInfoDocker
			//Append the "latest" tagged image buildinfo object with version tagged image
			buildInfo.append buildInfoDockerLatest
			buildInfo.env.capture = true
			cto.devops.jenkins.Utils.updateBuildRetention(currentBuild, buildInfo)
			publishBuildInfo buildInfo: buildInfo, server: server

                        def displayDocker = "Promote Docker to releases"
                        def promotionConfigDocker = [
                              'buildName'          : buildInfo.name,
                              'buildNumber'        : buildInfo.number,
                              'status'             : 'Released',
                              'targetRepo'         : 'cemod-docker-releases',
                              'sourceRepo'         : 'cemod-docker-candidates-local',
                              'includeDependencies': false,
                              'copy'               : true, // "copy" must be used because "move" requires delete permission
                              'failFast'           : true
                            ]
			
			def displayHelm = "Promote helm to releases"
			def promotionConfigHelm = [
                              'buildName'          : buildInfo.name,
                              'buildNumber'        : buildInfo.number,
                              'status'             : 'Released',
                              'targetRepo'         : 'cemnova-helm-stable',
                              'sourceRepo'         : 'cemnova-helm-incubator-local',
                              'includeDependencies': false,
                              'copy'               : true, // "copy" must be used because "move" requires delete permission
                              'failFast'           : true
                            ]

                        Artifactory.addInteractivePromotion server: server, promotionConfig: promotionConfigHelm, displayName: displayHelm
			Artifactory.addInteractivePromotion server: server, promotionConfig: promotionConfigDocker, displayName: displayDocker


}
