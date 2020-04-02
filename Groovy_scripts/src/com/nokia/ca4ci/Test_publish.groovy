package com.nokia.ca4ci

def findVersion() {
	env.VERSION = readFile("${WORKSPACE}/devops_ci/scripts/buildscripts/ws/version.txt").trim()
}


def uploadJars() {
	def server = Artifactory.newServer url: env.ARTIFACTORY_HTTPS_URL, credentialsId: 'cemod-jenkins-for-artifactory'
    	def buildInfo = Artifactory.newBuildInfo()
	buildInfo.env.capture = true
	buildInfo.env.collect()
    	def target = (env.BRANCH_NAME != 'master') ? "myproject-snapshot-feature-local/${env.BRANCH_NAME}/" : "myproject-snapshot-local/"

    def uploadSpec = """{
        "files": [{
            "pattern": "target/(.*).(jar|war|ear)",
            "target": "${target}",
            "recursive": "false",
            "regexp": "true"
        }]

    }"""

    artifactoryServer.upload(uploadSpec)
    artifactoryServer.publishBuildInfo(buildInfo)
	def buildInfo = Artifactory.newBuildInfo()
	server.publishBuildInfo(buildInfo)
	
}
def uploadFilesWithProfile(profile) {
		findVersion()
		def server = Artifactory.newServer url: env.ARTIFACTORY_HTTPS_URL, credentialsId: 'cemod-jenkins-for-artifactory'
                        echo "Upload compenent build for profile '${profile}'"
                        def uploadSpec = """{
        	                "files": [
                	        {
                        	            "pattern": "installers/${profile}/**/*.rpm",
                                	    "target": "cemod-yum-candidates/${profile}/${params.PACKAGE_DIRECTORY}/${env.VERSION}/",
	                                    "props": "version=${env.VERSION};profile=${profile}"
        	                }
                	                 ]
			}"""            
                        def buildInfo = Artifactory.newBuildInfo()              
                        buildInfo.env.capture = true
                        buildInfo.env.collect()              
                        server.upload(uploadSpec, buildInfo)
                        server.publishBuildInfo(buildInfo) 
}

def uploadFilesWithoutProfile() {
		findVersion()
		def BUILD_PROFILE = env.BUILD_PROFILE.split(' ')
                def server = Artifactory.newServer url: env.ARTIFACTORY_HTTPS_URL, credentialsId: 'cemod-jenkins-for-artifactory'
                for(profile in  BUILD_PROFILE) {
                        echo "Upload compenent build for profile '${profile}'"
                        def uploadSpec = """{
                                "files": [
                                {
                                            "pattern": "installers/**/*.rpm",
                                            "target": "cemod-yum-candidates/${profile}/${params.PACKAGE_DIRECTORY}/${env.VERSION}/",
                                            "props": "version=${env.VERSION};profile=${profile}"
                                }
                                         ]
                        }"""
                        def buildInfo = Artifactory.newBuildInfo()
                        buildInfo.env.capture = true
                        buildInfo.env.collect()
                        server.upload(uploadSpec, buildInfo)
                        server.publishBuildInfo(buildInfo)
                }
}
