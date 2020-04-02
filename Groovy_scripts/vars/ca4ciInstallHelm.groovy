import com.nokia.ca4ci.*
import com.nokia.ca4ci.build.*
import com.nokia.ca4ci.install.*

def call(body) {

		def config = [:]
		body.resolveStrategy = Closure.DELEGATE_FIRST
		body.delegate = config
		body()

		env.projectRepoUrl = "ssh://ca_cp@bhgerrit.ext.net.nokia.com:8282/CA4CI/"
		env.dependantComponents = config.dependantComponents
		env.branch = env.COMMIT_HASH
		env.currentRelease = config.currentRelease
		env.IP = config.IP
		env.labName = config.labName
		env.key = config.key
		env.testcaseFolder = config.testcaseFolder
		env.buildArgs = config.buildArgs
		env.ciType = config.ciType
		env.portal_test_users_config_version = config.portal_test_users_config_version
		env.portal_test_usecases_version = config.portal_test_usecases_version
		env.portal_simulator_version = config.portal_simulator_version
		env.cs_test_couchbase_version = config.cs_test_couchbase_version
		env.cs_test_enabler_version = config.cs_test_enabler_version
		env.test_couchbase_port = config.test_couchbase_port
		env.etltopology_test_version = config.etltopology_test_version
		env.etl_test_enabler_version = config.etl_test_enabler_version
		env.etl_test_couchbase_version = config.etl_test_couchbase_version
		env.etl_test_couchbase_port = config.etl_test_couchbase_port
		env.etl_test_cache_version = config.etl_test_cache_version
		env.etl_test_configservice_version = config.etl_test_configservice_version
		env.etl_test_cache_configuration_version = config.etl_test_cache_configuration_version
		env.edge_node = config.edge_node
		env.se_test_enabler_version = config.se_test_enabler_version
		env.se_test_db_version = config.se_test_db_version
		env.cs_test_db_version = config.cs_test_db_version
		env.rs_test_enabler_version = config.rs_test_enabler_version
		env.rs_test_universe_version = config.rs_test_universe_version
		env.rs_custom_universe_version = config.rs_custom_universe_version
		env.rs_webservice_version = config.rs_webservice_version
		env.rs_std_universe_version = config.rs_std_universe_version
		env.ws_test_enabler_version = config.ws_test_enabler_version
		env.ws_test_universe_version = config.ws_test_universe_version
		env.ws_custom_universe_version = config.ws_custom_universe_version
		env.ws_test_rest_version = config.ws_test_rest_version
		env.ws_sp_voice_universe_version = config.ws_sp_voice_universe_version
		env.cs_test_configuration_version = config.cs_test_configuration_version
		env.se_test_configuration_version = config.se_test_configuration_version
		env.ae_etl_test_enabler_version = config.ae_etl_test_enabler_version
		env.ae_etl_test_configservice_version = config.ae_etl_test_configservice_version
		env.ice_dimension_test_enabler_version  = config.ice_dimension_test_enabler_version 
		env.export_test_enabler_version=config.export_test_enabler_version
		env.NCM=config.NCM
		env.namespace = "ca4ci"
		env.exporter_path = "https://repo.lab.pl.alcatel-lucent.com/csf-generic-delivered/ncms-tools/1.0.60/exporter"
		env.helm_repo_url = " https://repo.lab.pl.alcatel-lucent.com/cemnova-helm-incubator/csfp/"

		env.NEXT_RELEASE = "5"
		env.CEMNOVA_RELEASE = "10"
		env.helmInstallParameter = config.helmInstallParameter		
	
		if(env.branch == "null") {
                        env.branch = "master"
                }
		if(env.NCM == "null") {
                        env.NCM = "false"
			env.helm_repo_url = " https://repo.lab.pl.alcatel-lucent.com/cemnova-helm-incubator/"
                }
		if(env.key == "null") {
			env.key = "labs/inventory/${labName}/privatekey.pem"
                }
		if(env.testcaseFolder == "null") {
                        env.testcaseFolder = "/testcases/integration"
                }
		if(env.buildArgs == "null") {
                       env.buildArgs = ""
                } 
		if(env.helmInstallParameter == "null") {
			env.helmInstallParameter = ""
		}

                def label = "worker-${UUID.randomUUID().toString()}"
                podTemplate(label: label, inheritFrom: 'k8s-dind', containers: [
                  containerTemplate(name: 'cemodtools', alwaysPullImage:true, image: 'cemod-docker-releases.repo.lab.pl.alcatel-lucent.com/cemod_tools_image:latest', workingDir: '/home/jenkins', ttyEnabled: true, command: 'cat'),
                  containerTemplate(name: 'rhel7', alwaysPullImage:true, image: 'cemod-docker-releases.repo.lab.pl.alcatel-lucent.com/cemod_rhel7_analytics_image:latest', workingDir: '/home/jenkins', ttyEnabled: true, command: 'cat')
                ])
                {
                        node(label) {
                                deleteDir()
				
                                try {
                                        timestamps {
                                                stage ('Clone') {
							script {
								container('cemodtools') {
									echo "Cloning of code started"
									def ca4ciInstall = new com.nokia.ca4ci.install.ca4ciInstall()
									ca4ciInstall.checkoutComponent()
									ca4ciInstall.checkoutCIRepo()
									ca4ciInstall.checkoutInventoryRepo()
									ca4ciInstall.checkoutTestParentRepo()
									ca4ciInstall.checkoutDependantComponents()
									echo "Cloning of code finished"
								}
							}

							properties([
								parameters([
									string(name: 'GIT_URL', defaultValue: '', description: 'Git Url'),
									string(name: 'COMMIT_HASH', defaultValue: '', description: 'Commit hash'),
									string(name: 'RELEASE', defaultValue: '7', description: 'Commit hash'),
									string(name: 'CHART_NAME', defaultValue: '', description: 'Image Name / chart name'),
									string(name: 'CHART_VERSION', defaultValue: '', description: 'Version of the chart'),
									string(name: 'DOCKER_BUILD_NAME', defaultValue: '', description: 'name of the helm build in artifactory'),
									string(name: 'DOCKER_BUILD_NUMBER', defaultValue: '', description: 'Build number'),
									string(name: 'IP', defaultValue: IP, description: 'IP'),
									booleanParam(name: 'INSTALL', defaultValue: true, description: 'Set true to update commit details'),
									booleanParam(name: 'TEST', defaultValue: true, description: 'Set true to update commit details'),
									booleanParam(name: 'PROMOTE', defaultValue: true, description: 'Set true to promote'),
									booleanParam(name: 'PROMOTE_TWICE', defaultValue: true, description: 'Set true to promote to next release'),
								]),
								disableConcurrentBuilds(),
								buildDiscarder(logRotator(daysToKeepStr: '30', artifactDaysToKeepStr: '0'))
							])
						}

						stage('Configure Maven') {
							  script {
								withCredentials([usernamePassword(credentialsId: 'cemod-artifactory', passwordVariable: 'OS_PASSWORD', usernameVariable: 'OS_USERNAME')]) {
								  sh "sed -i s/OS_USERNAME/${OS_USERNAME}/ devops_ci/settings/settings-resolverange.xml"
								  sh "sed -i s/OS_PASSWORD/${OS_PASSWORD}/ devops_ci/settings/settings-resolverange.xml"
								  sh 'sudo cp -r ${WORKSPACE}/devops_ci/settings/settings-resolverange.xml $WORKSPACE/settings.xml'
								}
							  }
						}

                        if(env.INSTALL == "true") {
							stage ('Install') {
								container('cemodtools') {
									script {
										 env.propertyfileName = "sut" + labName + ".properties"
									     	 env.propertyfile = WORKSPACE + "/common-test-definitions/sut-properties/src/main/resources/" + propertyfileName
									     	 def props1 = readProperties  file: propertyfile
					                                         env.prop = props1['sut.bcmt.edge.dns'] 
					                                         echo "prop = $prop"
										 sh "mvn --version && java -version"
											sh '''#!/bin/sh +x
											cd $WORKSPACE
											ls
											chmod 600 $key
											echo "IP=$IP"
											if [ "$prop" != "null" ]; then
												echo $prop >> /etc/hosts
												cat /etc/hosts
											fi
											echo $CHART_NAME > ${WORKSPACE}/charname.txt
											cat ${WORKSPACE}/charname.txt
											cut -d ' ' --output-delimiter=$'\n' -f 1- ${WORKSPACE}/charname.txt > ${WORKSPACE}/chartname.txt
											echo "CHART NAMES are listed below"
											echo "-----------------------------------"
											cat ${WORKSPACE}/chartname.txt
											echo "-----------------------------------"
											CHART_FILE="${WORKSPACE}/chartname.txt"
											file="app_list.yaml"
											rm -rf $file
echo "---
HELM_CHARTS:" >> $file
											while IFS= read -r CHART_NAME
                                                                                        do
												echo "CHART_NAME is ${CHART_NAME}"
												if [ "$NCM" = 'true' ]; then
echo " - name: $CHART_NAME
   version: $CHART_VERSION
   namespace: ca4ci" >> $file
        							                                        cat $file
													wget $exporter_path
													chmod 777 exporter
													./exporter --tar --clean --name customer-insights-profile-${BUILD_NUMBER} --multi 1000 app_list.yaml --repo $helm_repo_url
													pwd
													ls
													#ssh -i $key -no 'StrictHostKeyChecking no' cloud-user@$IP "pwd"
													#scp -i $key customer-insights-profile-${BUILD_NUMBER}.tgz cloud-user@$IP:/tmp/	
													ssh -i $key -no 'StrictHostKeyChecking no' cloud-user@$IP "sudo -i helm delete --purge test-$CHART_NAME"
													ssh -i $key -no 'StrictHostKeyChecking no' cloud-user@$IP "sudo -i helm install --name=$CHART_NAME  $helm_repo_url/$CHART_NAME-$CHART_VERSION.tgz --namespace=$namespace --name=test-$CHART_NAME $helmInstallParameter"
													ssh -i $key -no 'StrictHostKeyChecking no' cloud-user@$IP "sudo -i helm ls  --namespace=$namespace"
											else
													# Reading each CHART_NAME
													echo "CHART_NAME is ${CHART_NAME}"
													ssh -i $key -no 'StrictHostKeyChecking no' root@$IP "helm delete --purge $CHART_NAME"
													#ssh -i $key -no 'StrictHostKeyChecking no' root@$IP "sleep 1m"
													ssh -i $key -no 'StrictHostKeyChecking no' root@$IP "helm install --name=$CHART_NAME  $helm_repo_url/$CHART_NAME-$CHART_VERSION.tgz $helmInstallParameter"
													ssh -i $key -no 'StrictHostKeyChecking no' root@$IP "helm ls"
											fi
												done <"$CHART_FILE"
										'''
										if(env.ciType != "null") {
                       									//Execute component specific commands	
											echo "executing project specific install commands"
											def execute = new com.nokia.ca4ci.testing.execute()
											execute.installhelm()
                								}
									}
								}
							}
						}

						if(env.TEST == "true") {
							stage ('Test') {
								container('cemodtools') {
									try {
										script {
											echo "Executing test cases"
											if(env.CHART_NAME == "portal-ui") {
												sh '''
													cd ${folderToBuild}
                                                                                                        echo "buildArgs = $buildArgs"
													Xvfb :0 >& /dev/null &
													export DISPLAY=:0
                                                                                                        mvn clean verify -Dsut=$labName -s $WORKSPACE/settings.xml ${buildArgs}
												'''
													def publish = new com.nokia.ca4ci.publish()
										                        publish.uploadAnyFile("cemod-yum-candidates-local/Cloudera/portal-ui-tests/${RELEASE}","${WORKSPACE}/Portal/portal-ui/testcases/portal-ui-tests/target/portal-ui-tests-*.zip")	
                									} else {
												sh '''
													cd ${folderToBuild}
													echo "buildArgs = $buildArgs"
													mvn clean verify -Dsut=$labName -s $WORKSPACE/settings.xml ${buildArgs}
												'''
											}
											if(env.ciType != "null") {
                                                                                        	//Execute component specific commands
	                                                                                        echo "executing project specific test commands"
        	                                                                                def execute = new com.nokia.ca4ci.testing.execute()
                	                                                                        execute.testhelm()
                        	                                                        }
											publishHTML(target: [
                                                                                        	reportName : 'Serenity',
	                                                                                        reportDir:   folderToBuild + '/target/site/serenity',
        	                                                                                reportFiles: 'index.html',
                	                                                                        keepAll:     true,
                        	                                                                alwaysLinkToLastBuild: true,
                                	                                                        allowMissing: false
                                        	                                        ])	
											archiveArtifacts artifacts: folderToBuild + '/target/site/serenity/summary.txt'
										}
									}catch (err) {
										if(env.ciType != "null") {
                                                                                                //Execute component specific commands
                                                                                                echo "executing project specific test commands"
                                                                                                def execute = new com.nokia.ca4ci.testing.execute()
                                                                                                execute.testhelm()
                                                                                }
										publishHTML(target: [
                                                                                                reportName : 'Serenity',
                                                                                                reportDir:   folderToBuild + '/target/site/serenity',
                                                                                                reportFiles: 'index.html',
                                                                                                keepAll:     true,
                                                                                                alwaysLinkToLastBuild: true,
                                                                                                allowMissing: false
                                                                                ])
										archiveArtifacts artifacts: folderToBuild + '/target/site/serenity/summary.txt'
										currentBuild.result = 'FAILURE'
					                                        throw err
									}
								}
							}
						}
						if(env.INSTALL == "true") {
							stage ('UnInstall') {
								container('cemodtools') {
									script {
											sh '''#!/bin/sh +x
											chmod 600 $key
											echo "IP=$IP"
											ssh -i $key -o 'StrictHostKeyChecking no' root@$IP "helm delete --purge $CHART_NAME"
											ssh -i $key -o 'StrictHostKeyChecking no' root@$IP "helm ls"
										'''
									}
								}
							}
						}

						if(env.PROMOTE == "true") {
							stage ('Promote') {
									script {
									    echo "inside Promote stage"
									    env.imageNames = env.CHART_NAME
									    env.VERSION = env.CHART_VERSION
									    def promote = new com.nokia.ca4ci.promote()
									    promote.promoteHelm()	
									    if( env.PROMOTE_TWICE == 'true' ){
                        							env.ORIG_RELEASE = env.RELEASE
							                        env.RELEASE = env.NEXT_RELEASE
								                promote.promoteHelm()
										env.RELEASE = env.CEMNOVA_RELEASE
										promote.promoteHelm()
							                        env.RELEASE = env.ORIG_RELEASE
							                    }					
									}
							}
						}
                                        }
                                } catch (err) {
                                        currentBuild.result = 'FAILURE'
                                        throw err
                                }
                        }
                }

        }

