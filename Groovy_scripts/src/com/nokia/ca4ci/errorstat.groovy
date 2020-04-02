package com.nokia.ca4ci

def ErrorStatus() {
		
	    if (fileExists('errorStat.properties')) {
		def props1 = readProperties  file: 'errorStat.properties'
	        env.ERROR_STATUS = props1['ERROR_STATUS']
	    }
	    else {
		env.ERROR_STATUS = "0"
	    }	 
           
            if( env.ERROR_STATUS != '0' ) {
                 currentBuild.result = 'FAILURE'
		 echo "There are failures"
                 return
            }
}
