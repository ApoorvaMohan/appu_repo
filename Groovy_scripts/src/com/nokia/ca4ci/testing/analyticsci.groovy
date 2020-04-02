package com.nokia.ca4ci.testing

	
def soapuitesting() {
	sh '''
	

    echo "Copying data loading script on analytics node temp folder"
	scp -i ${key} -o 'StrictHostKeyChecking no' -r ${WORKSPACE}/CommonComponents/Analytics/testcases/sample_data/RHEL7_cloudera root@10.75.99.6:/tmp/
	ssh -o 'StrictHostKeyChecking no' -i ${key} root@10.75.99.6 "chmod -R 777 /tmp/RHEL7_cloudera"
	ssh -o 'StrictHostKeyChecking no' -i ${key} root@10.75.99.6 "dos2unix /tmp/RHEL7_cloudera/loadDataInHive.sh"
	ssh -o 'StrictHostKeyChecking no' -i ${key} root@10.75.99.6 "sh /tmp/RHEL7_cloudera/loadDataInHive.sh"

	echo "Data loading done"

	#sleep 15m
	#########################Running the soap ui test cases ############################
	#echo "Running the soap ui test cases"
	
	mvn clean surefire-report:report  -f ${WORKSPACE}/CommonComponents/Analytics/testcases/analytics-rest-test/subseg-rest-test/pom.xml -Dsoapui.https.protocols=SSLv3,TLSv1.2 -Dmaven.javadoc.skip=true -Dmaven.wagon.http.ssl.insecure=true
    


	'''
	}
	def soapuireports() {
	publishHTML(target: [allowMissing: false, keepAll: true, reportDir: WORKSPACE + "/CommonComponents/Analytics/testcases/analytics-rest-test/subseg-rest-test/target/site", reportFiles: 'surefire-report.html', reportName: 'SOAP_UI_REPORT'])
	
}

