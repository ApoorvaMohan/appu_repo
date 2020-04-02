package com.nokia.ca4ci

def commitFiles(directory,commitMessage,files) {
	env.directory = directory
	env.commitMessage = commitMessage
	env.files = files
	sshagent (credentials: ['d0413804-f035-4d68-8a7c-22385074a91a']) {
        dir(directory) {
			echo "directory = ${directory}, commitMessage = ${commitMessage} , files = ${files}"
		sh '''
                        git config --global user.email "apoorva.m@nokia.com"
                        git config --global user.name "M Apoorva"
                        git commit -m "${commitMessage}" ${files} 
                        git push origin master || git pull --rebase origin master && git push origin master
		'''
                }
        }
}
