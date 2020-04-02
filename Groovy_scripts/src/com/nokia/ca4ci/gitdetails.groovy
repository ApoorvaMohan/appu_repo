package com.nokia.ca4ci

def getGITdetails(folder){
	dir(folder) {
                    env.GIT_COMMIT = sh(returnStdout: true, script: 'git rev-parse HEAD').trim()
                    env.GIT_BRANCH = sh(returnStdout: true, script: 'git rev-parse --abbrev-ref HEAD').trim()
                    env.GIT_URL = sh(returnStdout: true, script: 'git config --get remote.origin.url').trim()
		    env.LIST_BRANCHES = sh(returnStdout: true, script: 'git branch -r').trim()
                    echo "${env.GIT_COMMIT} ${env.GIT_BRANCH} ${env.GIT_URL}"
		    echo "${env.LIST_BRANCHES}"
        }
}
