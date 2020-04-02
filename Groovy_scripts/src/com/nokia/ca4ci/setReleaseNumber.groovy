package com.nokia.ca4ci

def setRelease(profile){

        echo "profile = $profile, release = $RELEASE"
        def branchName = "origin/" + profile + "_" + RELEASE
	def branchName2 = "origin/" + profile + "_" + NEXT_RELEASE
	def branchName3 = "origin/" + profile + "_" + CEMNOVA_RELEASE
        echo "Verify if $branchName branch exists in the repo"
        def LIST_BRANCHES = LIST_BRANCHES.split(' ')
	env.PROMOTE_TWICE = "true"
        for(branches in LIST_BRANCHES ) {
                branches = branches.trim();
               	if( branches == branchName ) {
               		echo "Branch exists so resetting RELEASE to NextRelease(empty value)"
    			env.RELEASE = env.NEXT_RELEASE
			env.PROMOTE_TWICE = "false"
             	} else if( branches == branchName2 ) {
                        echo "Next release Branch exists so not promoting to next release"
                        env.PROMOTE_TWICE = "false"
                } else if( branches == branchName3 ) {
                        echo "CEMNOVA release Branch exists so not promoting to cemmova release"
                        env.PROMOTE_TWICE = "false"
                }
		
        }
}
