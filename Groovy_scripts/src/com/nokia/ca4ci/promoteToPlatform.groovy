package com.nokia.ca4ci

def promoteToPlatform(profile){

 if( profile != '' ) {
	echo "promoting to ${profile}"
	triggerProductBuild(profile)
 }
 else{
       def BUILD_PROFILE = env.BUILD_PROFILE.split(' ')
       for(profiles in  BUILD_PROFILE) {
		echo "promoting to ${profiles}"
       		triggerProductBuild(profiles)
       } 
 }
//checkout promoted dependency file and build_number file
//update the promoted dependency file and increment build number
//Create a Product folder and add all the rpms in the list to the newly added PRODUCT folder

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
		build job: 'Analytics/CEMOD/CA4CIBuilds/Common/PromoteToPlatform' + RELEASE, parameters: [string(name: 'PACKAGE_DIRECTORY', value: env.PACKAGE_DIRECTORY), string(name: 'BUILD_PROFILE', value: profile), string(name: 'VERSION', value: env.VERSION), string(name: 'JOB_NAME', value: env.BUILD_URL), string(name: 'COMMIT', value: env.GIT_COMMIT), string(name: 'BRANCH', value: env.GIT_BRANCH), string(name: 'URL', value: env.GIT_URL), string(name: 'SRC_RELEASE', value: env.SOURCE_RELEASE), booleanParam(name: 'CREATE_TAG', value: true)]
	}
	else{
		build job: 'Analytics/CEMOD/CA4CIBuilds/Common/PromoteToPlatform' + RELEASE, parameters: [string(name: 'PACKAGE_DIRECTORY', value: env.PACKAGE_DIRECTORY), string(name: 'BUILD_PROFILE', value: profile), string(name: 'VERSION', value: env.VERSION), string(name: 'JOB_NAME', value: env.BUILD_URL), string(name: 'SRC_RELEASE', value: env.SOURCE_RELEASE), booleanParam(name: 'CREATE_TAG', value: false)]
}	
}
