package com.nokia.ca4ci

def promoteToProduct(profile){
	
	if( profile != '' ) {
		echo "promoting to ${profile}"
		triggerProductBuild(profile)
 	}
	else{
		if (fileExists('outFile.properties')) {
			def props = readProperties  file: 'outFile.properties'
			env.BUILD_PROFILE = props['BUILD_PROFILE']
		}
       		def BUILD_PROFILE = env.BUILD_PROFILE.split(' ')
	        for(profiles in  BUILD_PROFILE) {
			echo "promoting to ${profiles}"
	       		triggerProductBuild(profiles)
       		} 
	 }	
}
	

def promoteHelm(){
	if(env.imageNames == null){
                echo "ImageName variable is not defined"
                currentBuild.result = 'FAILURE'
                return
        }
	if(env.project_helm_version == null){
                echo "VERSION variable is not defined "
                currentBuild.result = 'FAILURE'
                return
        }
	if(env.COMMIT == null){
                echo "COMMIT ID is not defined "
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
	build job: 'Analytics/CEMOD/CA4CIBuilds/Common/PromoteHelm' + RELEASE, quietPeriod: 2, parameters: [string(name: 'CHART_NAME', value: env.imageNames), string(name: 'CHART_VERSION', value: env.project_helm_version), string(name: 'COMMIT', value: env.COMMIT),string(name: 'DOCKER_BUILD_NAME', value: env.DOCKER_BUILD_NAME), string(name: 'DOCKER_BUILD_NUMBER', value: env.DOCKER_BUILD_NUMBER)]
}

def triggerProductBuild(profile){
              
	if(env.PACKAGE_DIRECTORY == null){
        	echo "PACKAGE DIRECTORY variable is not defined"
		currentBuild.result = 'FAILURE'
		return	
        }
        if(env.VERSION == null){
                echo "VERSION variable is not defined "
		currentBuild.result = 'FAILURE'
		return
        }

	if(env.CREATE_TAG == "true"){
		if(env.PROPERTY != " "){
			build job: 'Analytics/CEMOD/CA4CIBuilds/Common/PromoteToProduct' + RELEASE, quietPeriod: 2, parameters: [string(name: 'PACKAGE_DIRECTORY', value: env.PACKAGE_DIRECTORY), string(name: 'BUILD_PROFILE', value: profile), string(name: 'VERSION', value: env.VERSION), string(name: 'JOB_NAME', value: env.BUILD_URL), string(name: 'COMMIT', value: env.GIT_COMMIT), string(name: 'BRANCH', value: env.GIT_BRANCH), string(name: 'URL', value: env.GIT_URL), string(name: 'SRC_RELEASE', value: env.SOURCE_RELEASE), booleanParam(name: 'CREATE_TAG', value: true), string(name: 'PROPERTY', value: env.PROPERTY)]
		}
		else{
		build job: 'Analytics/CEMOD/CA4CIBuilds/Common/PromoteToProduct' + RELEASE , quietPeriod: 2,  parameters: [string(name: 'PACKAGE_DIRECTORY', value: env.PACKAGE_DIRECTORY), string(name: 'BUILD_PROFILE', value: profile), string(name: 'VERSION', value: env.VERSION), string(name: 'JOB_NAME', value: env.BUILD_URL), string(name: 'COMMIT', value: env.GIT_COMMIT), string(name: 'BRANCH', value: env.GIT_BRANCH), string(name: 'URL', value: env.GIT_URL), string(name: 'SRC_RELEASE', value: env.SOURCE_RELEASE), booleanParam(name: 'CREATE_TAG', value: true)]
		}
	}
	else{
		build job: 'Analytics/CEMOD/CA4CIBuilds/Common/PromoteToProduct' + RELEASE, quietPeriod: 2,  parameters: [string(name: 'PACKAGE_DIRECTORY', value: env.PACKAGE_DIRECTORY), string(name: 'BUILD_PROFILE', value: profile), string(name: 'VERSION', value: env.VERSION), string(name: 'JOB_NAME', value: env.BUILD_URL), string(name: 'SRC_RELEASE', value: env.SOURCE_RELEASE), booleanParam(name: 'CREATE_TAG', value: false)]
}	
}
