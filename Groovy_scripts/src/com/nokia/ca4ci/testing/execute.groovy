package com.nokia.ca4ci.testing

def installhelm() {

	if(env.ciType == "ice-dimension") {
		env.TEST_ENABLER_CHART = sh(returnStdout: true, script: 'ssh -i $key -o \'StrictHostKeyChecking no\' root@$IP "helm ls | grep "ice-dimension-test-enabler" || exit 0"');
		env.ICEDIMENSION = sh(returnStdout: true, script: 'ssh -i $key -o \'StrictHostKeyChecking no\' root@$IP "helm ls | grep "ice-dimension-test" || exit 0"');

	        sh '''
    			echo "Inside ice-dimension install function"
				if [ -n "$TEST_ENABLER_CHART" ];then
                ssh -i $key -o 'StrictHostKeyChecking no' root@$IP "helm delete --purge ice-dimension-test-enabler";
				fi
				if [ -n "$ICEDIMENSION" ];then
                ssh -i $key -o 'StrictHostKeyChecking no' root@$IP "helm delete --purge ice-dimension-test";
				fi
				
				echo "Installing ice-dimension test enabler"
				ssh -i $key -o 'StrictHostKeyChecking no' root@$IP "helm install --name=ice-dimension-test-enabler  https://repo.lab.pl.alcatel-lucent.com/cemnova-helm-incubator/ca4ci-test-enabler-${ice_dimension_test_enabler_version}.tgz --set application.environment="test" --set is_kerberos_enabled="true""
				ssh -i $key -o 'StrictHostKeyChecking no' root@$IP "sleep 1m"
				echo "Installing ce-dimension test enabler helm"
				ssh -i $key -o 'StrictHostKeyChecking no' root@$IP "helm install --name=ice-dimension-test  https://repo.lab.pl.alcatel-lucent.com/cemnova-helm-incubator/$CHART_NAME-$CHART_VERSION.tgz --set application.environment="test" --set is_kerberos_enabled="true" --set timezone.localtimezone=Asia/Kolkata"
				ssh -i $key -o 'StrictHostKeyChecking no' root@$IP "sleep 1m"
				echo "Installation complete"
				ssh -i $key -o 'StrictHostKeyChecking no' root@$IP "helm ls"


        	'''
	}
	if(env.ciType == "icecache") {
		   	env.ICECACHE_UNI_CHART = sh(returnStdout: true, script: 'ssh -i $key -o \'StrictHostKeyChecking no\' root@$IP "helm ls |  grep "ice-universe" || exit 0"');
			env.ICECACHE_CHART = sh(returnStdout: true, script: 'ssh -i $key -o \'StrictHostKeyChecking no\' root@$IP "helm ls |  grep "icecache" || exit 0"');

	        sh '''
    			echo "Inside icecache install function"
				
				if [ -n "$ICECACHE_UNI_CHART" ];then
				ssh -i $key -o 'StrictHostKeyChecking no' root@$IP "helm delete --purge ice-universe --no-hooks || exit 0";
				fi
				
			
				ssh -i $key -o 'StrictHostKeyChecking no' root@$IP "helm ls"

				ssh -i $key -o 'StrictHostKeyChecking no' root@$IP "sleep 2m"

				echo "Installing icecache universe"
				
				ice_uni_version=$(grep "^ice-universe" ${WORKSPACE}/devops_ci/collector/product/${RELEASE}/Cloudera/promotedHelmdependencies.txt | awk '{print $2}');
				ssh -i $key -o 'StrictHostKeyChecking no' root@$IP "helm install --name=ice-universe  https://repo.lab.pl.alcatel-lucent.com/cemnova-helm-incubator/ice-universe-${ice_uni_version}.tgz"

				
        	'''
	}
	
	if(env.ciType == "aeetl") {
		env.TEST_ENABLER_CHART = sh(returnStdout: true, script: 'ssh -i $key -o \'StrictHostKeyChecking no\' root@$IP "helm ls | grep "ae-etl-test-enabler" || exit 0"');
		env.ETLSAMPLE_SINK_CHART = sh(returnStdout: true, script: 'ssh -i $key -o \'StrictHostKeyChecking no\' root@$IP "helm ls | grep "ae-etltopology-sample-sink" || exit 0"');
		env.TEST_ETLCONFIGSERVICE = sh(returnStdout: true, script: 'ssh -i $key -o \'StrictHostKeyChecking no\' root@$IP "helm ls | grep "ae-etl-test" || exit 0"');
		env.ETLTOPOLOGY = sh(returnStdout: true, script: 'ssh -i $key -o \'StrictHostKeyChecking no\' root@$IP "helm ls | grep "ae-etltopology" || exit 0"');

	        sh '''
    			echo "Inside ae etltopology install function"

				if [ -n "$ETLSAMPLE_SINK_CHART" ];then
                ssh -i $key -o 'StrictHostKeyChecking no' root@$IP "helm delete --purge ae-etltopology-sample-sink";
				fi
				if [ -n "$TEST_ENABLER_CHART" ];then
                ssh -i $key -o 'StrictHostKeyChecking no' root@$IP "helm delete --purge ae-etl-test-enabler";
				fi
				if [ -n "$TEST_ETLCONFIGSERVICE" ];then
                ssh -i $key -o 'StrictHostKeyChecking no' root@$IP "helm delete --purge ae-etl-test";
				fi
				if [ -n "$ETLTOPOLOGY" ];then
                ssh -i $key -o 'StrictHostKeyChecking no' root@$IP "helm delete --purge ae-etltopology";
				fi

				ssh -i $key -o 'StrictHostKeyChecking no' root@$IP "helm install --name=ae-etl-test  https://repo.lab.pl.alcatel-lucent.com/cemnova-helm-incubator/etlconfigservice-${ae_etl_test_configservice_version}.tgz"
				ssh -i $key -o 'StrictHostKeyChecking no' root@$IP "sleep 1m"
				
				ssh -i $key -o 'StrictHostKeyChecking no' root@$IP "helm install --name=ae-etl-test-enabler  https://repo.lab.pl.alcatel-lucent.com/cemnova-helm-incubator/ca4ci-test-enabler-${ae_etl_test_enabler_version}.tgz --set application.environment="test" --set is_kerberos_enabled="true""
				ssh -i $key -o 'StrictHostKeyChecking no' root@$IP "sleep 1m"

				ssh -i $key -o 'StrictHostKeyChecking no' root@$IP "wget https://repo.lab.pl.alcatel-lucent.com/cemnova-helm-incubator/$CHART_NAME-$CHART_VERSION.tgz"
				ssh -i $key -o 'StrictHostKeyChecking no' root@$IP "tar -xvf $CHART_NAME-$CHART_VERSION.tgz"

				ssh -i $key -o 'StrictHostKeyChecking no' root@$IP "helm install --set adap.name=SAMPLE --set topology.name=SAMPLE --set connector.type=Sink  --set configfile.name=sink-connector.properties --set is_kerberos_enabled=yes --set application.environment="test" --set image.replicaCount=1 --set timezone.localtimezone=Asia/Kolkata --set etlconfigservice.name=ae-etl-test-etlconfigservice ./ae-etltopology -n ae-etltopology-sample-sink"

				ssh -i $key -o 'StrictHostKeyChecking no' root@$IP "sleep 2m"
				ssh -i $key -o 'StrictHostKeyChecking no' root@$IP "helm ls"

        	'''
	}

	if(env.ciType == "wsrest") {
			env.RS_TEST_ENABLER_CHART = sh(returnStdout: true, script: 'ssh -i $key -o \'StrictHostKeyChecking no\' root@$IP "helm ls | grep "ca4ci-test-enabler" || exit 0"');
		    env.RS_TEST_UNI_CHART = sh(returnStdout: true, script: 'ssh -i $key -o \'StrictHostKeyChecking no\' root@$IP "helm ls | grep "rs-test-uni" || exit 0"');
		    env.RS_TEST_CUSTOM_UNI_CHART = sh(returnStdout: true, script: 'ssh -i $key -o \'StrictHostKeyChecking no\' root@$IP "helm ls |  grep "rs-test-cust-uni" || exit 0"');
			env.RS_TEST_STD_UNI_CHART = sh(returnStdout: true, script: 'ssh -i $key -o \'StrictHostKeyChecking no\' root@$IP "helm ls |  grep "rs-test-std-uni" || exit 0"');
		    env.CI_RS_CHART = sh(returnStdout: true, script: 'ssh -i $key -o \'StrictHostKeyChecking no\' root@$IP "helm ls |  grep "ci-rs" || exit 0"');
			env.CI_RS_WEBSERVICE_CHART = sh(returnStdout: true, script: 'ssh -i $key -o \'StrictHostKeyChecking no\' root@$IP "helm ls |  grep "rs-ci" || exit 0"');

	        sh '''
    			echo "Inside wsrest install function"
    			
				if [ -n "$RS_TEST_ENABLER_CHART" ];then
				ssh -i $key -o 'StrictHostKeyChecking no' root@$IP "helm delete --purge ca4ci-test-enabler --no-hooks || exit 0";
				fi
				
				if [ -n "$RS_TEST_UNI_CHART" ];then
				ssh -i $key -o 'StrictHostKeyChecking no' root@$IP "helm delete --purge rs-test-uni --no-hooks || exit 0";
				fi
				
				if [ -n "$CI_RS_CHART" ];then
				ssh -i $key -o 'StrictHostKeyChecking no' root@$IP "helm delete --purge ci-rs --no-hooks || exit 0"
				fi
				
				if [ -n "$RS_TEST_CUSTOM_UNI_CHART" ];then
				ssh -i $key -o 'StrictHostKeyChecking no' root@$IP "helm delete --purge rs-test-cust-uni --no-hooks || exit 0"
				fi

				if [ -n "$RS_TEST_STD_UNI_CHART" ];then
				ssh -i $key -o 'StrictHostKeyChecking no' root@$IP "helm delete --purge rs-test-std-uni --no-hooks || exit 0"
				fi

				if [ -n "$CI_RS_WEBSERVICE_CHART" ];then
				ssh -i $key -o 'StrictHostKeyChecking no' root@$IP "helm delete --purge rs-ci  || exit 0"
				fi
				
				ssh -i $key -o 'StrictHostKeyChecking no' root@$IP "helm install -n ci-rs https://repo.lab.pl.alcatel-lucent.com/cemnova-helm-incubator/webservice-rest-$CHART_VERSION.tgz  --set=webservice-rest-websso.websso.service.name='ci-rs-webservice-rest' --set=webservice-rest-websso.websso.service.fullnameOverride='ci-rs-webservice-rest.default.svc.cluster.local' --set=webservice-rest-websso.websso.service.route='/ci-rs/webservice-rest' --set=webservice-rest-websso.websso.service.ckngOauth2Opts='--data config.cookie_path=/ci-rs'"
				
				ssh -i $key -o 'StrictHostKeyChecking no' root@$IP "helm ls"

				ssh -i $key -o 'StrictHostKeyChecking no' root@$IP "sleep 2m"
				
				ssh -i $key -o 'StrictHostKeyChecking no' root@$IP "kubectl get cm ci-rs-webservice-rest-app-config -o yaml | sed 's/ci-rs-webservice.default.svc/rs-ci-webservice.default.svc/' | kubectl replace -f -"

				echo "Installing webservice-rest test enabler"
				ssh -i $key -o 'StrictHostKeyChecking no' root@$IP "helm install --name=ca4ci-test-enabler  https://repo.lab.pl.alcatel-lucent.com/cemnova-helm-incubator/ca4ci-test-enabler-${rs_test_enabler_version}.tgz --set application.environment="test" --set is_kerberos_enabled="true""

				echo "Installing webservice for testing"
				ssh -i $key -o 'StrictHostKeyChecking no' root@$IP "helm install --name rs-ci https://repo.lab.pl.alcatel-lucent.com/cemnova-helm-incubator/webservice-${rs_webservice_version}.tgz  --set is_kerberos_enabled=true --set universe.releaseId=rs-ci --set database.schemaName=cemod"

				echo "Installing test universe"
				ssh -i $key -o 'StrictHostKeyChecking no' root@$IP "helm install --name=rs-test-uni  https://repo.lab.pl.alcatel-lucent.com/cemnova-helm-incubator/wsresttest-universe-${rs_test_universe_version}.tgz --set universe.releaseId=rs-ci"
				
				echo "Installing custom test universe"
				ssh -i $key -o 'StrictHostKeyChecking no' root@$IP "helm install --name=rs-test-cust-uni  https://repo.lab.pl.alcatel-lucent.com/cemnova-helm-incubator/wsresttestcustom-universe-${rs_custom_universe_version}.tgz --set universe.releaseId=rs-ci"
				
				echo "Installing custom test universe"
				ssh -i $key -o 'StrictHostKeyChecking no' root@$IP "helm install --name=rs-test-std-uni  https://repo.lab.pl.alcatel-lucent.com/cemnova-helm-incubator/sgsngn-universe-${rs_std_universe_version}.tgz --set universe.releaseId=rs-ci"

				
        	'''
	}
	
    if(env.ciType == "queryscheduler"){
          sh '''
		   ssh -i $key -o 'StrictHostKeyChecking no' root@$IP "helm delete --purge queryscheduler || exit 0"
		   ssh -i $key -o 'StrictHostKeyChecking no' root@$IP "sleep 2m"
		   ssh -i $key -o 'StrictHostKeyChecking no' root@$IP "helm delete --purge qs-test-enabler || exit 0"
		   ssh -i $key -o 'StrictHostKeyChecking no' root@$IP "sleep 2m"
		   ssh -i $key -o 'StrictHostKeyChecking no' root@$IP "helm delete --purge test-qs-xml || exit 0"
		   ssh -i $key -o 'StrictHostKeyChecking no' root@$IP "sleep 2m"
		   
		   qs_stable_version=$(grep "queryscheduler" ${WORKSPACE}/devops_ci/collector/product/${RELEASE}/Cloudera/promotedHelmdependencies.txt | awk '{print $2}');
		   qs_test_xml_stable_version=$(grep "test-qs-xmls" ${WORKSPACE}/devops_ci/collector/product/${RELEASE}/Cloudera/promotedHelmdependencies.txt | awk '{print $2}');
		   
		   echo "Installing QS Test"
		   ssh -i $key -o 'StrictHostKeyChecking no' root@$IP "helm install --name=queryscheduler --set=qsxmlsRef.releaseId='cucumber' --set ingress.enabled=true --set=app.queryscheduler.service.fullnameOverride=queryscheduler https://repo.lab.pl.alcatel-lucent.com/cemnova-helm-incubator-local/queryscheduler-${qs_stable_version}.tgz"
		   ssh -i $key -o 'StrictHostKeyChecking no' root@$IP "sleep 1m"
			
		   echo "Installing Test Enabler "
		   ssh -i $key -o 'StrictHostKeyChecking no' root@$IP "helm install -n qs-test-enabler https://repo.lab.pl.alcatel-lucent.com/cemnova-helm-incubator-local/ca4ci-test-enabler-${qs_test_enabler_stable_version}.tgz"
		   ssh -i $key -o 'StrictHostKeyChecking no' root@$IP "sleep 1m"
			
		    echo "Installing QS test XMLS "
			ssh -i $key -o 'StrictHostKeyChecking no' root@$IP "helm install -n test-qs-xml --set=qsxmlsRef.releaseId='cucumber' https://repo.lab.pl.alcatel-lucent.com/cemnova-helm-incubator-local/test-qs-xmls-${qs_test_xml_stable_version}.tgz"
			ssh -i $key -o 'StrictHostKeyChecking no' root@$IP "sleep 1m"
		
	      '''
	}	
	
	 if(env.ciType == "insights-sdk") {
		env.TEST_AI_SDK_TEST_CHART = sh(returnStdout: true, script: 'ssh -i $key -o \'StrictHostKeyChecking no\' root@$IP "helm ls |  grep ".*aisdk-test$" || exit 0"');
	        sh '''
    			echo "Installing insights sdk related helms"

				if [ -n "$TEST_AI_SDK_TEST_CHART" ];then
				ssh -i $key -o 'StrictHostKeyChecking no' root@$IP "helm delete --purge aisdk-test"
				fi
				echo "Installing insights sdk helm"
				ssh -i $key -o 'StrictHostKeyChecking no' root@$IP "helm install --name=aisdk-test  https://repo.lab.pl.alcatel-lucent.com/cemnova-helm-incubator/$CHART_NAME-$CHART_VERSION.tgz $helmInstallParameter"
				ssh -i $key -o 'StrictHostKeyChecking no' root@$IP "sleep 1m"
				echo "Installation complete"
				ssh -i $key -o 'StrictHostKeyChecking no' root@$IP "helm ls"
        	'''
	}
	
	if(env.ciType == "summarizationengine") {
		env.TEST_SE_TEST_CHART = sh(returnStdout: true, script: 'ssh -i $key -o \'StrictHostKeyChecking no\' root@$IP "helm ls |  grep "se-test" || exit 0"');
		env.TEST_ENABLER_CHART = sh(returnStdout: true, script: 'ssh -i $key -o \'StrictHostKeyChecking no\' root@$IP "helm ls | grep "se-test-enabler" || exit 0"');
		env.TEST_CONFIGMAP = sh(returnStdout: true, script: 'ssh -i $key -o \'StrictHostKeyChecking no\' root@$IP "helm ls | grep "se-test-summarizationengine-configuration" || exit 0"');
		env.TEST_SDK_DB_CHART = sh(returnStdout: true, script: 'ssh -i $key -o \'StrictHostKeyChecking no\' root@$IP "helm ls | grep "se-test-db" || exit 0"');
		
	        sh '''
    			echo "Installing summarizationengine related helms"

				if [[ ! -z "$TEST_ENABLER_CHART" ]];then
				ssh -i $key -o 'StrictHostKeyChecking no' root@$IP "helm delete --purge se-test-enabler";
				fi
				if [[ ! -z "$TEST_SDK_DB_CHART" ]];then
				ssh -i $key -o 'StrictHostKeyChecking no' root@$IP "helm delete --purge se-test-db";
				fi
				if [[ ! -z "$TEST_CONFIGMAP" ]];then
				ssh -i $key -o 'StrictHostKeyChecking no' root@$IP "helm delete --purge se-test-summarizationengine-configuration"
				fi
				if [[ ! -z "$TEST_SE_TEST_CHART" ]];then
				ssh -i $key -o 'StrictHostKeyChecking no' root@$IP "helm delete --purge se-test"
				fi
				echo "Installing SE Configmap"
				ssh -i $key -o 'StrictHostKeyChecking no' root@$IP "helm install --name=se-test-summarizationengine-configuration  https://repo.lab.pl.alcatel-lucent.com/cemnova-helm-incubator/summarizationengine-configuration-${se_test_configuration_version}.tgz"
				ssh -i $key -o 'StrictHostKeyChecking no' root@$IP "sleep 1m"
				echo "Installing SDK Test DB"
				ssh -i $key -o 'StrictHostKeyChecking no' root@$IP "helm install --name=se-test-db  https://repo.lab.pl.alcatel-lucent.com/cemnova-helm-incubator/sdk-postgres-test-container-${se_test_db_version}.tgz"
				ssh -i $key -o 'StrictHostKeyChecking no' root@$IP "sleep 1m"
				echo "Installing summarizationengine test enabler"
				ssh -i $key -o 'StrictHostKeyChecking no' root@$IP "helm install --name=se-test-enabler  https://repo.lab.pl.alcatel-lucent.com/cemnova-helm-incubator/ca4ci-test-enabler-${se_test_enabler_version}.tgz --set application.environment="test" --set is_kerberos_enabled="true""
				ssh -i $key -o 'StrictHostKeyChecking no' root@$IP "sleep 1m"
				echo "Installing summarizationengine helm"
				ssh -i $key -o 'StrictHostKeyChecking no' root@$IP "helm install --name=se-test  https://repo.lab.pl.alcatel-lucent.com/cemnova-helm-incubator/$CHART_NAME-$CHART_VERSION.tgz $helmInstallParameter"
				ssh -i $key -o 'StrictHostKeyChecking no' root@$IP "sleep 5m"
				echo "Installation complete"
				ssh -i $key -o 'StrictHostKeyChecking no' root@$IP "helm ls"
        	'''
	}
	if(env.ciType == "cacheservice") {
		env.ENABLER_CHART = sh(returnStdout: true, script: 'ssh -i $key -o \'StrictHostKeyChecking no\' root@$IP "helm ls | grep "cs-test-enabler" || exit 0"');
		env.TEST_CONFIGMAP = sh(returnStdout: true, script: 'ssh -i $key -o \'StrictHostKeyChecking no\' root@$IP "helm ls | grep "cs-test-cacheserviceagent-configuration" || exit 0"');
		env.COUCHBASE_CHART = sh(returnStdout: true, script: 'ssh -i $key -o \'StrictHostKeyChecking no\' root@$IP "helm ls | grep "cs-test-couchbase" || exit 0"');
		env.TEST_SDK_DB_CHART = sh(returnStdout: true, script: 'ssh -i $key -o \'StrictHostKeyChecking no\' root@$IP "helm ls | grep "cs-test-db" || exit 0"');
		env.TEST_CS_TEST_CHART = sh(returnStdout: true, script: 'ssh -i $key -o \'StrictHostKeyChecking no\' root@$IP "helm ls |  grep "cs-test" || exit 0"');
	        sh '''
    			echo "Inside cacheservice install function"

				if [[ ! -z "$TEST_SDK_DB_CHART" ]];then
				ssh -i $key -o 'StrictHostKeyChecking no' root@$IP "helm delete --purge cs-test-db";
				fi
				if [[ ! -z "$ENABLER_CHART" ]];then
                ssh -i $key -o 'StrictHostKeyChecking no' root@$IP "helm delete --purge cs-test-enabler";
				fi
				if [[ ! -z "$COUCHBASE_CHART" ]];then
                ssh -i $key -o 'StrictHostKeyChecking no' root@$IP "helm delete --purge cs-test-couchbase";
				fi
				if [[ ! -z "$TEST_CONFIGMAP" ]];then
				ssh -i $key -o 'StrictHostKeyChecking no' root@$IP "helm delete --purge cs-test-cacheserviceagent-configuration"
				fi
				echo "Installing CS Configmap"
				ssh -i $key -o 'StrictHostKeyChecking no' root@$IP "helm install --name=cs-test-cacheserviceagent-configuration  https://repo.lab.pl.alcatel-lucent.com/cemnova-helm-incubator/cacheserviceagent-configuration-${cs_test_configuration_version}.tgz"
				ssh -i $key -o 'StrictHostKeyChecking no' root@$IP "sleep 1m"
				ssh -i $key -o 'StrictHostKeyChecking no' root@$IP "helm install --name=cs-test-db  https://repo.lab.pl.alcatel-lucent.com/cemnova-helm-incubator/sdk-postgres-test-container-${cs_test_db_version}.tgz"
				ssh -i $key -o 'StrictHostKeyChecking no' root@$IP "helm install --name=cs-test-enabler  https://repo.lab.pl.alcatel-lucent.com/cemnova-helm-incubator/ca4ci-test-enabler-${cs_test_enabler_version}.tgz --set couchbase.name="cs-test-couchbase" --set is_kerberos_enabled="true""
				ssh -i $key -o 'StrictHostKeyChecking no' root@$IP "helm install --name=cs-test-couchbase  https://repo.lab.pl.alcatel-lucent.com/cemnova-helm-incubator/couchbase-${cs_test_couchbase_version}.tgz --set service.nodePort="${test_couchbase_port}""
				if [ ! -z "$TEST_CS_TEST_CHART" ];then
				ssh -i $key -o 'StrictHostKeyChecking no' root@$IP "helm delete --purge cs-test"
				fi
				ssh -i $key -o 'StrictHostKeyChecking no' root@$IP "helm install --name=cs-test  https://repo.lab.pl.alcatel-lucent.com/cemnova-helm-incubator/$CHART_NAME-$CHART_VERSION.tgz --set application.environment="test" --set app.testCouchbaseIP="${edge_node}" --set app.testCouchbasePort="${test_couchbase_port}" --set is_kerberos_enabled="true" --set=ingress.enabled="true""
				ssh -i $key -o 'StrictHostKeyChecking no' root@$IP "sleep 2m"
				ssh -i $key -o 'StrictHostKeyChecking no' root@$IP "helm ls"
        	'''
	}

	if(env.ciType == "etlconfigservice") {

		env.TEST_ETLCONFIGSERVICE = sh(returnStdout: true, script: 'ssh -i $key -o \'StrictHostKeyChecking no\' root@$IP "helm ls | grep "etlconfig-test" || exit 0"');
		env.TEST_ENABLER_CHART = sh(returnStdout: true, script: 'ssh -i $key -o \'StrictHostKeyChecking no\' root@$IP "helm ls | grep "etlconfig-test-enabler" || exit 0"');
		env.ETLSAMPLE_SOURCE_CHART = sh(returnStdout: true, script: 'ssh -i $key -o \'StrictHostKeyChecking no\' root@$IP "helm ls | grep "etltopology-etlconfigsample-source" || exit 0"');
		env.ETLSAMPLE_SINK_CHART = sh(returnStdout: true, script: 'ssh -i $key -o \'StrictHostKeyChecking no\' root@$IP "helm ls | grep "etltopology-etlconfigsample-sink" || exit 0"');

	        sh '''
    			echo "Inside etlconfigservice install function *****"

			if [[ ! -z "$TEST_ETLCONFIGSERVICE" ]];then
                ssh -i $key -o 'StrictHostKeyChecking no' root@$IP "helm delete --purge etlconfig-test";
				fi
				if [[ ! -z "$TEST_ENABLER_CHART" ]];then
                ssh -i $key -o 'StrictHostKeyChecking no' root@$IP "helm delete --purge etlconfig-test-enabler";
				fi
				if [[ ! -z "$ETLSAMPLE_SOURCE_CHART" ]];then
                ssh -i $key -o 'StrictHostKeyChecking no' root@$IP "helm delete --purge etltopology-etlconfigsample-source";
				fi
				if [[ ! -z "$ETLSAMPLE_SINK_CHART" ]];then
                ssh -i $key -o 'StrictHostKeyChecking no' root@$IP "helm delete --purge etltopology-etlconfigsample-sink";
				fi
				
				ssh -i $key -o 'StrictHostKeyChecking no' root@$IP "helm install --name=etlconfig-test-enabler  https://repo.lab.pl.alcatel-lucent.com/cemnova-helm-incubator/ca4ci-test-enabler-${etl_test_enabler_version}.tgz --set couchbase.name="etl-test-cb-couchbase" --set is_kerberos_enabled="true" --set ingress.enabled="true""

				ssh -i $key -o 'StrictHostKeyChecking no' root@$IP "helm install --name=etlconfig-test https://repo.lab.pl.alcatel-lucent.com/cemnova-helm-incubator/$CHART_NAME-$CHART_VERSION.tgz --set ingress.enabled="true""
				
				ssh -i $key -o 'StrictHostKeyChecking no' root@$IP "wget https://repo.lab.pl.alcatel-lucent.com/cemnova-helm-incubator/etltopology-${etltopology_test_version}.tgz"
				ssh -i $key -o 'StrictHostKeyChecking no' root@$IP "tar -xvf etltopology-${etltopology_test_version}.tgz"
				ssh -i $key -o 'StrictHostKeyChecking no' root@$IP "helm install --set adap.name=ETLSAMPLE --set topology.name=ETLSAMPLE --set connector.type=Source  --set configfile.name=source-connector.properties --set is_kerberos_enabled=yes --set application.environment="test" --set app.testCouchbaseIP="${edge_node}" --set image.replicaCount=1 --set timezone.localtimezone=Asia/Kolkata --set etlconfigservice.name=etlconfig-test-etlconfigservice ./etltopology -n etltopology-etlconfigsample-source"
				ssh -i $key -o 'StrictHostKeyChecking no' root@$IP "helm install --set adap.name=ETLSAMPLE --set topology.name=ETLSAMPLE --set connector.type=Sink  --set configfile.name=sink-connector.properties --set is_kerberos_enabled=yes --set application.environment="test" --set app.testCouchbaseIP="${edge_node}" --set image.replicaCount=1 --set timezone.localtimezone=Asia/Kolkata --set etlconfigservice.name=etlconfig-test-etlconfigservice ./etltopology -n etltopology-etlconfigsample-sink"

				ssh -i $key -o 'StrictHostKeyChecking no' root@$IP "sleep 3m"
				ssh -i $key -o 'StrictHostKeyChecking no' root@$IP "helm ls"
        	'''
	}

	if(env.ciType == "etltopology") {
		env.TEST_ENABLER_CHART = sh(returnStdout: true, script: 'ssh -i $key -o \'StrictHostKeyChecking no\' root@$IP "helm ls | grep "etl-test-enabler" || exit 0"');
		env.TEST_CACHE_CHART = sh(returnStdout: true, script: 'ssh -i $key -o \'StrictHostKeyChecking no\' root@$IP "helm ls | grep "etl-test-cache" || exit 0"');
		env.TEST_CONFIGMAP = sh(returnStdout: true, script: 'ssh -i $key -o \'StrictHostKeyChecking no\' root@$IP "helm ls | grep "etl-test-cache-cacheserviceagent-configuration" || exit 0"');
		env.TEST_COUCHBASE_CHART = sh(returnStdout: true, script: 'ssh -i $key -o \'StrictHostKeyChecking no\' root@$IP "helm ls | grep "etl-test-cb" || exit 0"');
		env.ETLSAMPLE_SINK_CHART = sh(returnStdout: true, script: 'ssh -i $key -o \'StrictHostKeyChecking no\' root@$IP "helm ls | grep "etltopology-etlsample-sink" || exit 0"');
		env.ETLSAMPLE_SOURCE_CHART = sh(returnStdout: true, script: 'ssh -i $key -o \'StrictHostKeyChecking no\' root@$IP "helm ls | grep "etltopology-etlsample-source" || exit 0"');
		env.TEST_ETLCONFIGSERVICE = sh(returnStdout: true, script: 'ssh -i $key -o \'StrictHostKeyChecking no\' root@$IP "helm ls | grep "etl-test-config" || exit 0"');
		env.ETLTOPOLOGY = sh(returnStdout: true, script: 'ssh -i $key -o \'StrictHostKeyChecking no\' root@$IP "helm ls | grep "etltopology" || exit 0"');
		env.SMS_SINK_CHART = sh(returnStdout: true, script: 'ssh -i $key -o \'StrictHostKeyChecking no\' root@$IP "helm ls | grep "etltopology-sms-sink" || exit 0"');
		env.SMS_SOURCE_CHART = sh(returnStdout: true, script: 'ssh -i $key -o \'StrictHostKeyChecking no\' root@$IP "helm ls | grep "etltopology-sms-source" || exit 0"');

	        sh '''
    			echo "Inside etltopology install function"
				if [[ ! -z "$TEST_CONFIGMAP" ]];then
				ssh -i $key -o 'StrictHostKeyChecking no' root@$IP "helm delete --purge etl-test-cache-cacheserviceagent-configuration"
				fi
				if [[ ! -z "$ETLSAMPLE_SOURCE_CHART" ]];then
                ssh -i $key -o 'StrictHostKeyChecking no' root@$IP "helm delete --purge etltopology-etlsample-source";
				fi
				if [[ ! -z "$ETLSAMPLE_SINK_CHART" ]];then
                ssh -i $key -o 'StrictHostKeyChecking no' root@$IP "helm delete --purge etltopology-etlsample-sink";
				fi
				if [[ ! -z "$TEST_ENABLER_CHART" ]];then
                ssh -i $key -o 'StrictHostKeyChecking no' root@$IP "helm delete --purge etl-test-enabler";
				fi
				if [[ ! -z "$TEST_CACHE_CHART" ]];then
                ssh -i $key -o 'StrictHostKeyChecking no' root@$IP "helm delete --purge etl-test-cache";
				fi
				if [[ ! -z "$TEST_ETLCONFIGSERVICE" ]];then
                ssh -i $key -o 'StrictHostKeyChecking no' root@$IP "helm delete --purge etl-test-config";
				fi
				if [[ ! -z "$ETLTOPOLOGY" ]];then
                ssh -i $key -o 'StrictHostKeyChecking no' root@$IP "helm delete --purge etltopology";
				fi
				if [[ ! -z "$SMS_SOURCE_CHART" ]];then
                ssh -i $key -o 'StrictHostKeyChecking no' root@$IP "helm delete --purge etltopology-sms-source";
				fi
				if [[ ! -z "$SMS_SINK_CHART" ]];then
                ssh -i $key -o 'StrictHostKeyChecking no' root@$IP "helm delete --purge etltopology-sms-sink";
				fi
				echo "Installing CS Configmap"
				ssh -i $key -o 'StrictHostKeyChecking no' root@$IP "helm install --name=etl-test-cache-cacheserviceagent-configuration  https://repo.lab.pl.alcatel-lucent.com/cemnova-helm-incubator/cacheserviceagent-configuration-${etl_test_cache_configuration_version}.tgz"
				ssh -i $key -o 'StrictHostKeyChecking no' root@$IP "sleep 1m"
				ssh -i $key -o 'StrictHostKeyChecking no' root@$IP "helm install --name=etl-test-config https://repo.lab.pl.alcatel-lucent.com/cemnova-helm-incubator/etlconfigservice-${etl_test_configservice_version}.tgz --set ingress.enabled="true""

				#ssh -i $key -o 'StrictHostKeyChecking no' root@$IP "helm install --name=etl-test-cb  https://repo.lab.pl.alcatel-lucent.com/cemnova-helm-incubator/couchbase-${etl_test_couchbase_version}.tgz --set service.nodePort="${etl_test_couchbase_port}" --set ingress.enabled="true""
				#ssh -i $key -o 'StrictHostKeyChecking no' root@$IP "sleep 1m"
	  			ssh -i $key -o 'StrictHostKeyChecking no' root@$IP "helm install --name=etl-test-cache  https://repo.lab.pl.alcatel-lucent.com/cemnova-helm-incubator/cacheserviceagent-${etl_test_cache_version}.tgz --set application.environment="etltest" --set app.testCouchbaseIP="${edge_node}" --set app.testCouchbasePort="${etl_test_couchbase_port}" --set is_kerberos_enabled="true" --set ingress.enabled="true""
				
				ssh -i $key -o 'StrictHostKeyChecking no' root@$IP "helm install --name=etl-test-enabler  https://repo.lab.pl.alcatel-lucent.com/cemnova-helm-incubator/ca4ci-test-enabler-${etl_test_enabler_version}.tgz --set couchbase.name="etl-test-cb-couchbase" --set is_kerberos_enabled="true" --set ingress.enabled="true""

				ssh -i $key -o 'StrictHostKeyChecking no' root@$IP "wget https://repo.lab.pl.alcatel-lucent.com/cemnova-helm-incubator/$CHART_NAME-$CHART_VERSION.tgz"
				ssh -i $key -o 'StrictHostKeyChecking no' root@$IP "tar -xvf $CHART_NAME-$CHART_VERSION.tgz"

				ssh -i $key -o 'StrictHostKeyChecking no' root@$IP "helm install --set adap.name=ETLSAMPLE --set topology.name=ETLSAMPLE --set connector.type=Source  --set configfile.name=source-connector.properties --set is_kerberos_enabled=yes --set application.environment="test" --set app.testCouchbaseIP="${edge_node}" --set image.replicaCount=1 --set timezone.localtimezone=Asia/Kolkata --set etlconfigservice.name=etl-test-config-etlconfigservice ./etltopology -n etltopology-etlsample-source"
				ssh -i $key -o 'StrictHostKeyChecking no' root@$IP "sleep 1m"
				ssh -i $key -o 'StrictHostKeyChecking no' root@$IP "helm install --set adap.name=ETLSAMPLE --set topology.name=ETLSAMPLE --set connector.type=Sink  --set configfile.name=sink-connector.properties --set is_kerberos_enabled=yes --set application.environment="test" --set app.testCouchbaseIP="${edge_node}" --set image.replicaCount=1 --set timezone.localtimezone=Asia/Kolkata --set etlconfigservice.name=etl-test-config-etlconfigservice ./etltopology -n etltopology-etlsample-sink"
				ssh -i $key -o 'StrictHostKeyChecking no' root@$IP "helm install --set adap.name=SMS --set topology.name=SMS --set connector.type=Source  --set configfile.name=source-connector.properties --set is_kerberos_enabled=yes --set application.environment="test" --set app.testCouchbaseIP="${edge_node}" --set image.replicaCount=1 --set timezone.localtimezone=UTC --set etlconfigservice.name=etl-test-config-etlconfigservice ./etltopology -n etltopology-sms-source"
				ssh -i $key -o 'StrictHostKeyChecking no' root@$IP "sleep 1m"
				ssh -i $key -o 'StrictHostKeyChecking no' root@$IP "helm install --set adap.name=SMS --set topology.name=SMS --set connector.type=Sink  --set configfile.name=sink-connector.properties --set is_kerberos_enabled=yes --set application.environment="test" --set app.testCouchbaseIP="${edge_node}" --set image.replicaCount=1 --set timezone.localtimezone=UTC --set etlconfigservice.name=etl-test-config-etlconfigservice ./etltopology -n etltopology-sms-sink"

				ssh -i $key -o 'StrictHostKeyChecking no' root@$IP "sleep 2m"
				ssh -i $key -o 'StrictHostKeyChecking no' root@$IP "helm ls"

        	'''
	}

	if(env.ciType == "webservice") {
			env.WS_TEST_ENABLER_CHART = sh(returnStdout: true, script: 'ssh -i $key -o \'StrictHostKeyChecking no\' root@$IP "helm ls | grep "ws-test-enabler" || exit 0"');
		    env.WS_TEST_UNI_CHART = sh(returnStdout: true, script: 'ssh -i $key -o \'StrictHostKeyChecking no\' root@$IP "helm ls | grep "ws-test-uni" || exit 0"');
		    env.WS_TEST_CUSTOM_UNI_CHART = sh(returnStdout: true, script: 'ssh -i $key -o \'StrictHostKeyChecking no\' root@$IP "helm ls |  grep "ws-test-cust-uni" || exit 0"');
		    env.CI_WS_REST_CHART = sh(returnStdout: true, script: 'ssh -i $key -o \'StrictHostKeyChecking no\' root@$IP "helm ls |  grep "ci-ws" || exit 0"');
			env.WEEK_START_UNIVERSE=sh(returnStdout: true, script: 'ssh -i $key -o \'StrictHostKeyChecking no\' root@$IP "helm ls |  grep "ws-test-week-start-day-uni" || exit 0"');
			env.WS_CI_CHART=sh(returnStdout: true, script: 'ssh -i $key -o \'StrictHostKeyChecking no\' root@$IP "helm ls |  grep "ci-websrv" || exit 0"');
			
	        sh '''
    			echo "Inside webservie install function"
    			
				if [ -n "$WS_TEST_ENABLER_CHART" ];then
				ssh -i $key -o 'StrictHostKeyChecking no' root@$IP "helm delete --purge ws-test-enabler || exit 0";
				fi
				
				if [ -n "$WS_TEST_UNI_CHART" ];then
				ssh -i $key -o 'StrictHostKeyChecking no' root@$IP "helm delete --purge ws-test-uni --no-hooks || exit 0";
				fi
				
				if [ -n "$WS_TEST_CUSTOM_UNI_CHART" ];then
				ssh -i $key -o 'StrictHostKeyChecking no' root@$IP "helm delete --purge ws-test-cust-uni --no-hooks || exit 0"
				fi 
				
				if [ -n "$WEEK_START_UNIVERSE" ];then
				ssh -i $key -o 'StrictHostKeyChecking no' root@$IP "helm delete --purge ws-test-week-start-day-uni --no-hooks || exit 0"
				fi

				if [ -n "$CI_WS_REST_CHART" ];then
				ssh -i $key -o 'StrictHostKeyChecking no' root@$IP "helm delete --purge ci-ws || exit 0"
				fi

				if [ -n "$WS_CI_CHART" ];then
				ssh -i $key -o 'StrictHostKeyChecking no' root@$IP "helm delete --purge ci-websrv || exit 0"
				fi

				ssh -i $key -o 'StrictHostKeyChecking no' root@$IP "sleep 2m"
				ssh -i $key -o 'StrictHostKeyChecking no' root@$IP "helm ls"

				echo "Installing webservice for testing"
				ssh -i $key -o 'StrictHostKeyChecking no' root@$IP "helm install --name ci-websrv https://repo.lab.pl.alcatel-lucent.com/cemnova-helm-incubator/$CHART_NAME-$CHART_VERSION.tgz  --set is_kerberos_enabled=true --set universe.releaseId=ci-websrv --set database.schemaName=cemod --set ingress.enabled="true""

				ssh -i $key -o 'StrictHostKeyChecking no' root@$IP "sleep 2m"
				ssh -i $key -o 'StrictHostKeyChecking no' root@$IP "helm ls"
				
				echo "Installing webservice test enabler"
				ssh -i $key -o 'StrictHostKeyChecking no' root@$IP "helm install --name=ws-test-enabler  https://repo.lab.pl.alcatel-lucent.com/cemnova-helm-incubator/ca4ci-test-enabler-${ws_test_enabler_version}.tgz --set application.environment="test" --set is_kerberos_enabled="true""

				ssh -i $key -o 'StrictHostKeyChecking no' root@$IP "helm ls"
				
				ssh -i $key -o 'StrictHostKeyChecking no' root@$IP "helm install -n ci-ws https://repo.lab.pl.alcatel-lucent.com/cemnova-helm-incubator/webservice-rest-${ws_test_rest_version}.tgz  --set=webservice-rest-websso.websso.service.name='ci-ws-webservice-rest' --set=webservice-rest-websso.websso.service.fullnameOverride='ci-ws-webservice-rest.default.svc.cluster.local' --set=webservice-rest-websso.websso.service.route='/ci-ws/webservice-rest' --set=webservice-rest-websso.websso.service.ckngOauth2Opts='--data config.cookie_path=/ci-ws'"
								
				ssh -i $key -o 'StrictHostKeyChecking no' root@$IP "kubectl get cm ci-ws-webservice-rest-app-config -o yaml | sed 's/ci-ws-webservice.default.svc/ci-websrv-webservice.default.svc/' | kubectl replace -f -"
				
				echo "Patching week start day."
				
				ssh -i $key -o 'StrictHostKeyChecking no' root@$IP "kubectl patch configmap ca4ci-ext-endpoints --type=json -p='[{\\"op\\": \\"replace\\", \\"path\\": \\"/data/STARTDAYOFTHEWEEK.PROPERTIES\\", \\"value\\": CEMOD_WEEK_START_DAY=sunday}]'"
				
				echo "Installing test universe"
				ssh -i $key -o 'StrictHostKeyChecking no' root@$IP "helm install --name=ws-test-uni  https://repo.lab.pl.alcatel-lucent.com/cemnova-helm-incubator/wsresttest-universe-${ws_test_universe_version}.tgz  --set universe.releaseId=ci-websrv"				
				echo "Installing custom test universe"
				ssh -i $key -o 'StrictHostKeyChecking no' root@$IP "helm install --name=ws-test-cust-uni  https://repo.lab.pl.alcatel-lucent.com/cemnova-helm-incubator/wsresttestcustom-universe-${ws_custom_universe_version}.tgz  --set universe.releaseId=ci-websrv --set app.isCustomUniverse=true"
				echo "Installing week start day test universe"
				ssh -i $key -o 'StrictHostKeyChecking no' root@$IP "helm install --name=ws-test-week-start-day-uni  http://repo.lab.pl.alcatel-lucent.com/cemnova-helm-incubator/spvoice-universe-${ws_sp_voice_universe_version}.tgz  --set universe.releaseId=ci-websrv"	
				
				ssh -i $key -o 'StrictHostKeyChecking no' root@$IP "helm ls"

        	'''
	}

	
	if(env.ciType == "exportservice") {
		env.EXPORT_TEST_ENABLER_CHART = sh(returnStdout: true, script: 'ssh -i $key -o \'StrictHostKeyChecking no\' root@$IP "helm ls | grep "export-test-enabler" || exit 0"');
		env.EXPORT_SERVICE_CHART= sh(returnStdout: true, script: 'ssh -i $key -o \'StrictHostKeyChecking no\' root@$IP "helm ls | grep "ci-exportservice" || exit 0"');
		

	        sh '''
    			echo "Inside exportservice test enabler install function"
				if [ -n "$EXPORT_TEST_ENABLER_CHART" ];then
                ssh -i $key -o 'StrictHostKeyChecking no' root@$IP "helm delete --purge export-test-enabler";
				fi
				echo "Inside exportservice helm installation function"
				if [ -n "$EXPORT_SERVICE_CHART" ];then
				ssh -i $key -o 'StrictHostKeyChecking no' root@$IP "helm delete --purge ci-exportservice --no-hooks";
				ssh -i $key -o 'StrictHostKeyChecking no' root@$IP "kubectl delete secrets ci-exportservice-exportservice-cmdb-mariadb-secrets";
				ssh -i $key -o 'StrictHostKeyChecking no' root@$IP "kubectl delete secrets ci-exportservice-exportservice-cmdb-repl-secrets";
				fi
				echo "Installing exportservice helm"
				
				
				ssh -i $key -o 'StrictHostKeyChecking no' root@$IP "helm install --name=ci-exportservice https://repo.lab.pl.alcatel-lucent.com/cemnova-helm-incubator/$CHART_NAME-$CHART_VERSION.tgz --set exportwebsso.websso.service.route="/ci-exportservice" --set persistence.size=1Gi --set is_kerberos_enabled="true" --set exportwebsso.websso.service.name="ci-exportservice" --set exportwebsso.websso.service.fullnameOverride="exportservice.default.svc.cluster.local" --set ingress.enabled="true""
				ssh -i $key -o 'StrictHostKeyChecking no' root@$IP "sleep 1m"
				
				
				echo "Installing exportservice test enabler"
				ssh -i $key -o 'StrictHostKeyChecking no' root@$IP "helm install --name=export-test-enabler  https://repo.lab.pl.alcatel-lucent.com/cemnova-helm-incubator/ca4ci-test-enabler-${export_test_enabler_version}.tgz --set application.environment="test" --set is_kerberos_enabled="true""
				ssh -i $key -o 'StrictHostKeyChecking no' root@$IP "sleep 1m"
				
				echo "Installation complete"
				ssh -i $key -o 'StrictHostKeyChecking no' root@$IP "helm ls"


        	'''
	}

	if(env.ciType == "portal-service"){
		env.PTL_SRV_CUSTOM = sh(returnStdout: true, script: 'ssh -i $key -o \'StrictHostKeyChecking no\' root@$IP "helm ls | grep "ci-ptl-srv" || exit 0"');
		env.PTL_SRV_DEFAULT = sh(returnStdout: true, script: 'ssh -i $key -o \'StrictHostKeyChecking no\' root@$IP "helm ls | grep "portal-service" || exit 0"');

		sh '''
		    if [ -n "$PTL_SRV_CUSTOM" ];then
				echo "Un-installing older version of Portal Service with release name ci-ptl-srv"
                ssh -i $key -o 'StrictHostKeyChecking no' root@$IP "helm delete --purge ci-ptl-srv";
				ssh -i $key -o 'StrictHostKeyChecking no' root@$IP "sleep 1m"
            fi
			
			if [ -n "$PTL_SRV_DEFAULT" ];then
				echo "Un-installing older version of Portal Service with release name portal-service"
                ssh -i $key -o 'StrictHostKeyChecking no' root@$IP "helm delete --purge portal-service";
				ssh -i $key -o 'StrictHostKeyChecking no' root@$IP "sleep 1m"
            fi
						
			echo "Installing Portal Service"
			ssh -i $key -o 'StrictHostKeyChecking no' root@$IP "helm install --name=ci-ptl-srv  https://repo.lab.pl.alcatel-lucent.com/cemnova-helm-incubator/$CHART_NAME-$CHART_VERSION.tgz  --set=webssoportalservice.websso.service.name='ci-ptl-srv-portal-service' --set=webssoportalservice.websso.service.fullnameOverride='ci-ptl-srv-portal-service.default.svc.cluster.local' --set=webssoportalservice.websso.service.route='/ci-ptl-srv/portal-service' --set=webssoportalservice.websso.service.ckngOauth2Opts='--data config.cookie_path=/ci-ptl-srv'"
			ssh -i $key -o 'StrictHostKeyChecking no' root@$IP "sleep 1m"
		'''
	}
	
	if(env.ciType == "portal-ui" || env.ciType == "cgf-service"){
		sh '''
			echo "Installing ${ciType} Charts"
            mapfile -t chartNames < ${WORKSPACE}/Portal/${ciType}/testcases/integration/conf/dependency.txt
			scp -i ${key} -o 'StrictHostKeyChecking no' -r ${WORKSPACE}/Portal/${ciType}/testcases/integration/conf/uninstall.sh root@$IP:/tmp/
			for (( idx=${#chartNames[@]}-1 ; idx>=0 ; idx-- )) ; do
				ssh -i $key -o 'StrictHostKeyChecking no' root@$IP "sh /tmp/uninstall.sh ${ciType}-${chartNames[idx]} || exit 0"
			done
			scp -i ${key} -o 'StrictHostKeyChecking no' -r ${WORKSPACE}/Portal/${ciType}/testcases/integration/conf/values.yaml root@$IP:/tmp/
			for chartName in "${chartNames[@]}" ; do
			    stableVersion=$(grep $chartName ${WORKSPACE}/devops_ci/collector/product/${RELEASE}/Cloudera/promotedHelmdependencies.txt | awk '{print $2}')
				ssh -i $key -o 'StrictHostKeyChecking no' root@$IP "helm install --name=${ciType}-${chartName} -f /tmp/values.yaml https://repo.lab.pl.alcatel-lucent.com/cemnova-helm-incubator-local/${chartName}-${stableVersion}.tgz"
			done
			sleep 120
			ssh -i $key -o 'StrictHostKeyChecking no' root@$IP "kubectl get all"
		'''
	}
}

def testhelm() {

	if(env.ciType == "wsrest") {
                sh '''
                        echo "Inside wsrest test function"
						ssh -i $key -o 'StrictHostKeyChecking no' root@$IP "helm delete --purge rs-test-uni --no-hooks || exit 0"
						ssh -i $key -o 'StrictHostKeyChecking no' root@$IP "helm delete --purge rs-test-std-uni --no-hooks || exit 0"
						ssh -i $key -o 'StrictHostKeyChecking no' root@$IP "helm delete --purge rs-test-cust-uni --no-hooks || exit 0";
						ssh -i $key -o 'StrictHostKeyChecking no' root@$IP "helm delete --purge rs-ci || exit 0"
                '''
        }
		if(env.ciType == "icecache") {
                sh '''
                        echo "Inside icecache function"
						ssh -i $key -o 'StrictHostKeyChecking no' root@$IP "helm delete --purge ice-universe --no-hooks || exit 0"
						ssh -i $key -o 'StrictHostKeyChecking no' root@$IP "helm delete --purge icecache || exit 0"
                '''
        }
	if(env.ciType == "webservice") {
                sh '''
                        echo "Inside webservice uninstall function"
						ssh -i $key -o 'StrictHostKeyChecking no' root@$IP "helm delete --purge ws-test-week-start-day-uni --no-hooks || exit 0"
						ssh -i $key -o 'StrictHostKeyChecking no' root@$IP "helm delete --purge ws-test-cust-uni --no-hooks || exit 0"
						ssh -i $key -o 'StrictHostKeyChecking no' root@$IP "helm delete --purge ws-test-uni --no-hooks || exit 0";
						ssh -i $key -o 'StrictHostKeyChecking no' root@$IP "helm delete --purge ci-ws || exit 0"
						ssh -i $key -o 'StrictHostKeyChecking no' root@$IP "helm delete --purge ci-websrv || exit 0"
	
                '''
        }
		
	if(env.ciType == "exportservice") {
                sh '''
                        echo "Inside exortservice uninstall function"
						ssh -i $key -o 'StrictHostKeyChecking no' root@$IP "helm delete --purge ci-exportservice --no-hooks || exit 0"
						ssh -i $key -o 'StrictHostKeyChecking no' root@$IP "kubectl delete secrets ci-exportservice-exportservice-cmdb-mariadb-secrets || exit 0"
						ssh -i $key -o 'StrictHostKeyChecking no' root@$IP "kubectl delete secrets ci-exportservice-exportservice-cmdb-repl-secrets || exit 0";
						ssh -i $key -o 'StrictHostKeyChecking no' root@$IP "helm delete --purge export-test-enabler --no-hooks || exit 0"
						
	
                '''
        }	
	if(env.ciType == "portal-ui" || env.ciType == "cgf-service"){
		sh '''
			echo "Cleaningup ${ciType} Charts"
			mapfile -t chartNames < ${WORKSPACE}/Portal/${ciType}/testcases/integration/conf/dependency.txt
			scp -i ${key} -o 'StrictHostKeyChecking no' -r ${WORKSPACE}/Portal/${ciType}/testcases/integration/conf/uninstall.sh root@$IP:/tmp/
			for (( idx=${#chartNames[@]}-1 ; idx>=0 ; idx-- )) ; do
				ssh -i $key -o 'StrictHostKeyChecking no' root@$IP "sh /tmp/uninstall.sh ${ciType}-${chartNames[idx]} || exit 0"
			done
			ssh -i $key -o 'StrictHostKeyChecking no' root@$IP "sh /tmp/uninstall.sh ${ciType} || exit 0"
			ssh -i $key -o 'StrictHostKeyChecking no' root@$IP "kubectl get all"
		'''
	}
}
