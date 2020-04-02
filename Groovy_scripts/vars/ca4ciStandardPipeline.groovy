import com.nokia.ca4ci.*
import com.nokia.ca4ci.build.*

def call(body) {

        def config = [:]
        body.resolveStrategy = Closure.DELEGATE_FIRST
        body.delegate = config
        body()

		RHEL6_NODE = 'rhel6'
		RHEL7_NODE = 'rhel7'
		TOOLS_NODE= 'cemodtools'
		env.TESTCONTAINERS_RYUK_DISABLED=true
		env.projectRepoUrl = "ssh://ca_cp@bhgerrit.ext.net.nokia.com:8282/CA4CI/"
		env.helmPromotedDependendecyFile = "devops_ci/collector/product/" + RELEASE + "/Cloudera/promotedHelmdependencies.txt"
		env.MASTERRELEASE = "7"
		env.masterHelmPromotedDependendecyFile = "devops_ci/collector/product/" + MASTERRELEASE + "/Cloudera/promotedHelmdependencies.txt"
		env.deploy_profile = "cemod-deploy-artifactory"
		env.exporter_path = "https://repo.lab.pl.alcatel-lucent.com/csf-generic-candidates/ncms-tools/1.0.84/exporter"
		env.componentRepoPath = config.componentRepoPath
		env.componentSparseCheckoutModule = config.componentSparseCheckoutModule
		env.componentName = config.componentName
		env.dependantComponents = config.dependantComponents
		env.packageRepository = config.packageRepository
		env.thirdpartyComponent = config.thirdpartyComponent
		env.deployToArtefactory = config.deployToArtefactory
		env.buildType = config.buildType
		env.buildThese = config.buildThese
		env.folderToBuild = config.folderToBuild
		env.ADAPTATION = config.ADAPTATION
		env.UNIVERSE = config.UNIVERSE
		env.NEW_UNIVERSE = config.NEW_UNIVERSE
		env.INSIGHTS_METADATA = config.INSIGHTS_METADATA
		env.BO_HOME = config.BO_HOME
		env.componentToBuild = env.componentRepoPath
		env.platformCollector = config.platformCollector
		env.cemNova = config.cemNova
		env.branch = config.branch
		env.RELEASE_DIR = config.RELEASE_DIR
		env.PROPERTY = config.PROPERTY
		env.publishRPM = config.publishRPM
		env.publishDocker = config.publishDocker
		env.PROMOTE = config.PROMOTE
		env.PROMOTEHELM = config.PROMOTEHELM
		env.NEXT_RELEASE = "8"
		env.CEMNOVA_RELEASE = "10"
		env.talkocheck = "false"
		env.cronTime = config.cronTime
		env.currentRelease = config.currentRelease
		env.imageNames = config.imageNames
		env.installJobName = config.installJobName
		env.publishHelm = config.publishHelm
		env.gerrit = config.gerrit
		env.featureBuild = config.featureBuild
		env.filePathPattern = config.filePathPattern
		env.forbiddenfilePathPattern = config.forbiddenfilePathPattern
		env.sonar_profile = config.sonar_profile
		env.dockerBuildPath = config.dockerBuildPath
		env.publishJarInfo = config.publishJarInfo
		env.projectType = config.projectType
		env.docker_security_scan = "false"
		env.sonar_branch = env.branch
		env.runTestSuite = config.runTestSuite
		env.promote_to_all = config.promote_to_all
		env.skip_csfp_helm = config.skip_csfp_helm
		env.skip_helm_folder = config.skip_helm_folder
					
		if(env.PROMOTE == "null") {
                        env.PROMOTE = "false"
                }
		if(env.PROMOTEHELM == "null") {
                        env.PROMOTEHELM = "false"
                }
		if(env.cronTime == "null") {
                        env.cronTime = "H 18 * * *"
                }
		if(env.publishRPM == "null") {
                        env.publishRPM = "true"
                }
		if(env.promote_to_all == "null") {
                        env.promote_to_all = "false"
                }
		if(env.publishHelm == "null") {
                        env.publishHelm = "true"
                }
		if(env.publishDocker == "null") {
			env.publishDocker = "true"
		}
		if(env.publishJarInfo == "null") {
                        env.publishJarInfo = "true"
                }
		if(env.currentRelease == "null") {
                        env.currentRelease = "7"
                }
		if(env.installJobName == "null") {
                        env.installJobName = ""
			env.INSTALL_CI = "false"	
                }else {
			env.INSTALL_CI = "true"
		}
		if(env.sonar_profile == "null") {
                        env.sonar_profile = "CEMoD_Profile_Java"
                }
		if(env.dockerBuildPath == "null") {
			env.dockerBuildPath = "/installation"
		}
		if(env.imageNames != "null" && env.publishDocker == "true") {
			env.docker_security_scan = "true"
                }
		if(env.componentSparseCheckoutModule != "null") {
			env.componentToBuild = env.componentSparseCheckoutModule
		}
		if(env.folderToBuild != "null") {
			env.componentToBuild = env.folderToBuild
		}
		if(env.platformCollector == "true" && env.PROMOTE_TO_PRODUCT == "true") {
			env.PROMOTE_TO_PRODUCT = "false"
			env.PROMOTE_TO_PLATFORM = "true"
		}
		if(env.cemNova == "true") {
                        env.projectRepoUrl = "ssh://ca_cp@bhgerrit.ext.net.nokia.com:8282/"
                }
		if(env.branch == "null") {
                        env.branch = "master"
			env.sonar_branch=""
        	}
		if(env.skip_csfp_helm == "null") {
                        env.skip_csfp_helm = "false"
                }
		if(env.skip_helm_folder == "null") {
                        env.skip_helm_folder = "false"
                }
		if(env.PROPERTY == "null") {
                        env.PROPERTY = ""
                }
		if(env.gerrit == "null") {
                        env.gerrit = "false"
                }
		if(env.featureBuild == "null") {
                        env.featureBuild = "false"
                }
		if(env.filePathPattern == "null") {
                        env.filePathPattern = "**"
                }
		if(env.forbiddenfilePathPattern == "null") {
                        env.forbiddenfilePathPattern = ""
                }
		if(env.runTestSuite == "true") {
            env.partitionKey="dt"
                if(env.partitionKey == "dt"){	
                    env.TEST_SUITES="CEI/cucumber-bin,CEI/cucumber-overridesubscriber-wifi,CEI/cucumber-set1,CEI/cucumber-set2,CEI/cucumber-set3,CEI/cucumber-setoverrideimsi,CQI/cucumber-set3,CQI/cucumber-set1,CQI/cucumber-set2,CQI/cucumber-set4,CQI/cucumber-radio,FL_OTT/cucumber,RADIO/cucumber,cucumber-setoverrideheavyusersubscriber,CEI/overrideexportwithparams"
				}
				else{
                    env.TEST_SUITES="CA4MN"
				}
				}
		
		if(env.gerrit == "true" || featureBuild == "true") {
                        env.PROMOTE = "false"
                        env.PROMOTEHELM = "false"
                        env.publishDocker = "false"
                        env.publishHelm = "false"
                        env.DOCKERBUILD = "false"
			env.DOCKERSECURITYSCAN = "false"
                        env.INSTALL = "false"
			env.PROMOTE_TO_PRODUCT = "false"
			env.PROMOTE_TO_PLATFORM = "false"
                        env.deploy_profile = "cemod-deploy-inprogress"
                }

		def label = "worker-${UUID.randomUUID().toString()}"
		podTemplate(label: label, inheritFrom: 'k8s-dind-clair', containers: [
		  containerTemplate(name: 'rhel6', alwaysPullImage:true, image: 'cemod-docker-releases.repo.lab.pl.alcatel-lucent.com/cemod_rhel6_18_image:latest', workingDir: '/home/jenkins', ttyEnabled: true, command: 'cat'),
		  containerTemplate(name: 'rhel7', alwaysPullImage:true, image: 'cemod-docker-releases.repo.lab.pl.alcatel-lucent.com/cemod_rhel7_analytics_image:latest', workingDir: '/home/jenkins', ttyEnabled: true, command: 'cat'),
		  containerTemplate(name: 'cemodtools', alwaysPullImage:true, image: 'cemod-docker-releases.repo.lab.pl.alcatel-lucent.com/cemod_tools_image:latest', workingDir: '/home/jenkins', ttyEnabled: true, command: 'cat')
		])
		{
			node(label) {
				deleteDir()

				def checkoutComponent = {String componentRepoNames, String sparseCheckoutPath -> 
					echo "checking out component started"
					if(sparseCheckoutPath == "null" || sparseCheckoutPath == null ) {
						echo "No sparse checkout configured so cloning component repo"
						def componentsToCheckout = componentRepoNames.split(',')
						for(componentRepoName in componentsToCheckout) {
							env.componentFullUrl = "$projectRepoUrl$componentRepoName"
							echo "checking out url : ${env.componentFullUrl}"
							if(env.gerrit == "false") {
								env.commitHash = checkout([$class: 'GitSCM', branches: [[name: branch]], doGenerateSubmoduleConfigurations: false, extensions: [[$class: 'CheckoutOption', timeout: 10], [$class: 'CloneOption', depth: 0, noTags: false, reference: '', shallow: false, timeout: 10],[$class: 'LocalBranch', localBranch: branch],[$class: 'RelativeTargetDirectory', relativeTargetDir: componentRepoName]], submoduleCfg: [], userRemoteConfigs: [[credentialsId: 'd0413804-f035-4d68-8a7c-22385074a91a', url: env.componentFullUrl]]]).GIT_COMMIT 
							} else {
								env.commitHash = checkout([$class: 'GitSCM', branches: [[name: '$GERRIT_BRANCH']], doGenerateSubmoduleConfigurations: false, extensions: [[$class: 'CheckoutOption', timeout: 10], [$class: 'CloneOption', depth: 0, noTags: false, reference: '', shallow: false, timeout: 10],[$class: 'RelativeTargetDirectory', relativeTargetDir: componentRepoName],[$class: 'BuildChooserSetting', buildChooser: [$class: 'GerritTriggerBuildChooser']]], submoduleCfg: [], userRemoteConfigs: [[credentialsId: 'd0413804-f035-4d68-8a7c-22385074a91a', name: componentRepoName, refspec: '$GERRIT_REFSPEC', url: env.componentFullUrl]]]).GIT_COMMIT
							}
		
							echo "commit hash is : ${env.commitHash}"
						}
					} else {
						env.componentFullUrl = "$projectRepoUrl$componentRepoNames"
						echo "Cloning with sparse checkout"
						if(env.gerrit == "false") {
							env.commithash = checkout([$class: 'GitSCM', branches: [[name: branch]], doGenerateSubmoduleConfigurations: false, extensions: [[$class: 'CheckoutOption', timeout: 10], [$class: 'CloneOption', depth: 0, noTags: false, reference: '', shallow: false, timeout: 10],[$class: 'LocalBranch', localBranch: branch],[$class: 'SparseCheckoutPaths', sparseCheckoutPaths: [[path: sparseCheckoutPath]]]], submoduleCfg: [], userRemoteConfigs: [[credentialsId: 'd0413804-f035-4d68-8a7c-22385074a91a', url: env.componentFullUrl]]])
						} else {
							env.commithash = checkout([$class: 'GitSCM', branches: [[name: '$GERRIT_BRANCH']], doGenerateSubmoduleConfigurations: false, extensions: [[$class: 'CheckoutOption', timeout: 10], [$class: 'CloneOption', depth: 0, noTags: false, reference: '', shallow: false, timeout: 10],[$class: 'BuildChooserSetting', buildChooser: [$class: 'GerritTriggerBuildChooser']],[$class: 'SparseCheckoutPaths', sparseCheckoutPaths: [[path: sparseCheckoutPath]]]], submoduleCfg: [], userRemoteConfigs: [[credentialsId: 'd0413804-f035-4d68-8a7c-22385074a91a', name: componentRepoNames, refspec: '$GERRIT_REFSPEC', url: env.componentFullUrl]]]).GIT_COMMIT
						}
					}
					echo "checking out component finished"
				}
			
				def checkoutCIRepo = {
						echo "checking out CI scripts started"
						checkout changelog: false, poll: false, scm: [$class: 'GitSCM', branches: [[name: '*/master']], doGenerateSubmoduleConfigurations: false, extensions: [
								[$class: 'RelativeTargetDirectory', relativeTargetDir: 'devops_ci'], 
								[$class: 'SparseCheckoutPaths', sparseCheckoutPaths: [[path: 'scripts/buildscripts'], [path: 'settings'], [path: 'collector']]], 
								[$class: 'CleanBeforeCheckout'], 
								[$class: 'ChangelogToBranch', options: [compareRemote: 'devops_ci', compareTarget: 'TEST1']],
								[$class: 'PathRestriction', excludedRegions: '.*', includedRegions: '']], submoduleCfg: [], userRemoteConfigs: [[credentialsId: 'd0413804-f035-4d68-8a7c-22385074a91a', url: 'ssh://ca_cp@bhgerrit.ext.net.nokia.com:8282/CA4CI/devops_ci']]]
						echo "checking out CI scripts finished"
				}
				
				
				def checkoutDependantComponents = {
					echo "checking out dependant components started  $dependantComponents"
					if(dependantComponents == null) {
						echo "No dependant components to checkout"
					} else {
						echo "started checking out dependencies..."
						def dependantList = dependantComponents.tokenize(',')
						dependantList.each{
							def dependantInfo = it.tokenize(':')
							if(dependantInfo.size() == 3) {
								checkout changelog: false, poll: false, scm: [$class: 'GitSCM', branches: [[name: dependantInfo[0]]], doGenerateSubmoduleConfigurations: false, extensions: [[$class: 'CheckoutOption', timeout: 10], [$class: 'CloneOption', depth: 0, noTags: false, reference: '', shallow: false, timeout: 10], [$class: 'RelativeTargetDirectory', relativeTargetDir: dependantInfo[1]], [$class: 'SparseCheckoutPaths', sparseCheckoutPaths: [[path: dependantInfo[2]]]], [$class: 'PathRestriction', excludedRegions: '.*', includedRegions: '']], submoduleCfg: [], userRemoteConfigs: [[credentialsId: 'd0413804-f035-4d68-8a7c-22385074a91a', url: projectRepoUrl + dependantInfo[1]]]]
							} else {
								echo "skipping checking out dependant info as information is insufficient"
							}
						}

						echo "checking out dependant components finished"
					}
				}
				
				def startBuild = {String profileName, String osName, String componentNameToBuild, String buildType, String deployToArtefactory,String thirdpartyComponent ->
						echo "Building component started"
						try {
							container(osName) {
								if(env.projectType == "python") {
                                                			sh "yum install -y python3 --disablerepo google-chrome"
			                                                sh "python3 --version"
                                        			}
								withCredentials([usernamePassword(credentialsId: 'cemod-artifactory', passwordVariable: 'OS_PASSWORD', usernameVariable: 'OS_USERNAME')]) {
									sh "sed -i s/OS_USERNAME/${OS_USERNAME}/ /nfsshare/nemesis/opt/apache-maven-3.5.3/conf/settings.xml"
									sh "sed -i s/OS_PASSWORD/${OS_PASSWORD}/ /nfsshare/nemesis/opt/apache-maven-3.5.3/conf/settings.xml"
									env.BUILD_PROFILE = profileName
									env.CSFP_SKIP = "true" 	
									if(env.skip_csfp_helm == "false") {
										def exitCode = sh script: 'find -name "helm-csfp" | egrep .', returnStatus: true
										env.CSFP_SKIP = exitCode != 0
										echo "csfp helm build skip $CSFP_SKIP"	
									}	
									env.HELM_FOLDER_SKIP = "true"
									if(env.skip_helm_folder == "false") {
										def exitCode1 = sh script: 'find -name "helm" | egrep .', returnStatus: true
	                                                                        env.HELM_FOLDER_SKIP = exitCode1 != 0
        	                                                                echo "helm folder build skip $HELM_FOLDER_SKIP"				
									}

									def ca4cibuild = new com.nokia.ca4ci.build.ca4cibuild()
									echo "Starting build using new library with $componentNameToBuild" 
									ca4cibuild.buildComponent(componentNameToBuild, buildType, deployToArtefactory, thirdpartyComponent)
								}
							}
							return "true"
						}catch(all) {
							println all
							return "false"
						} 
						echo "Building component finished"
				}
				
				def compileAndBuildRpm = {
					def FAILED = "false"
					def configuredProfiles = env.BUILD_PROFILE.split(' ')
					for(profile in  configuredProfiles) {
						env.RELEASE1 = env.RELEASE
						if (RELEASE != "0"){
							if(env.gerrit == "false" ){
							   if(env.featureBuild == "false"){
								if(env.branch == "master" || env.promote_to_all == "true"){
		                                                        def info1 = new com.nokia.ca4ci.setReleaseNumber()
        		                                                info1.setRelease(profile)
								}
							   }
							}
                                                }
						def osName = RHEL7_NODE
						if (profile == "Wandisco27"){
							osName = RHEL6_NODE
						}
						if (buildType == "ncmprofile"){
							osName = TOOLS_NODE
						}
						def buildStatus = startBuild(profile, osName, env.componentToBuild, env.buildType, env.deployToArtefactory, env.thirdpartyComponent)
						if ( buildStatus == "false"){
							echo "Build has failed. Please search for errors with a search key word as [ERROR]"
							currentBuild.result = 'FAILURE' 
						}
						env.RELEASE = env.RELEASE1
					}
				}
				
				def talkoCheck = {
					container('tools') {
					    sh '''
						#!/bin/sh +x
	   					if [ -f ${WORKSPACE}/talko_dependencies.txt ]; then
    							echo "talko check started"
    							docker run -v ${WORKSPACE}:/tmp neo-docker-candidates.repo.lab.pl.alcatel-lucent.com/talko-tool:latest -command legalCheck -talkoOutputToFile /tmp/talko_report.xml -mavenDependencyFiles /tmp/talko_dependencies.txt -verbose || true
    					    	else
	    					        echo "talko_dependencies.txt file doesnt exist so not performing talko check"
    						fi
					    '''
					    if (fileExists('talko_report.xml')) {
							def newFile =  sh(returnStdout: true, script: 'echo ${packageRepository//#}').trim()
							newFile = newFile + ".xml"	
							echo "newFile = $newFile"
 	 				                sh "cp talko_report.xml ${newFile}"
					                env.artifactoryrepo = "cemod-yum-candidates-local"
	                                                env.PUBLISH_DATE = sh(returnStdout: true, script: 'date +%y.%m.%d').trim()
	                                                echo "PUBLISH_DATE = $PUBLISH_DATE"
                                                        def publish = new com.nokia.ca4ci.publish()
                                                        publish.uploadAnyFile("${env.artifactoryrepo}/${env.BUILD_PROFILE}/TALKO/${RELEASE}/${env.PUBLISH_DATE}",newFile)
                	                                archiveArtifacts artifacts: 'talko_report.xml'
                        		    }
					}
				}
				
				def buildDockerImage = {
					container(RHEL7_NODE) {
							withCredentials([usernamePassword(credentialsId: 'cemod-artifactory', passwordVariable: 'OS_PASSWORD', usernameVariable: 'OS_USERNAME')]) {
							sh "sed -i s/OS_USERNAME/${OS_USERNAME}/ /nfsshare/nemesis/opt/apache-maven-3.5.3/conf/settings.xml"
							sh "sed -i s/OS_PASSWORD/${OS_PASSWORD}/ /nfsshare/nemesis/opt/apache-maven-3.5.3/conf/settings.xml"
							echo "Building docker image"
							def ca4cibuild = new com.nokia.ca4ci.build.ca4cibuild()
							ca4cibuild.buildDocker(componentToBuild)
						}
					}
				}
				
				def scanDockerForSecurityViolations = {
					dockerForScan = "cemod-docker-candidates.repo.lab.pl.alcatel-lucent.com/${env.imageNames}:latest"
					clairScanLocalImages(dockerForScan,'Unknown','',true)
				}
				
				def cleanUpInstallers = {
					container(RHEL7_NODE) {
						withCredentials([usernamePassword(credentialsId: 'cemod-artifactory', passwordVariable: 'OS_PASSWORD', usernameVariable: 'OS_USERNAME')]) {
							def ca4cibuild = new com.nokia.ca4ci.build.ca4cibuild()
							ca4cibuild.cleanUp()
						}
					}
				}
				
				try {
					timestamps {
						stage ('Clone') {
								script {
									echo "Cloning of code started"
									echo "component repo path : ${componentRepoPath}"
									echo "component sparse checkout path : ${componentSparseCheckoutModule}"
									checkoutComponent(componentRepoPath,componentSparseCheckoutModule)
									env.COMMIT = env.commitHash
									checkoutCIRepo()
									checkoutDependantComponents()
									echo "Cloning of code finished"
								}
								if(env.gerrit == "false") {
									def info = new com.nokia.ca4ci.gitdetails()
									info.getGITdetails(componentRepoPath)
	
									properties([
									parameters([
										string(name: 'PACKAGE_DIRECTORY', defaultValue: packageRepository , description: 'PACKAGE DIRECTORY in installers'),
										string(name: 'BUILD_PROFILE', defaultValue: 'Cloudera' , description: 'Build profiles'),
										string(name: 'RELEASE', defaultValue: currentRelease , description: 'Empty release variable'),
										booleanParam(name: 'CREATE_TAG', defaultValue: true, description: 'Set true to create tag info'),
										booleanParam(name: 'DOCKERBUILD', defaultValue: Boolean.valueOf(dockerbuild), description: 'Set true to build docker'),
										booleanParam(name: 'DOCKERSECURITYSCAN', defaultValue: docker_security_scan , description: 'Set true to enable security scan for docker'),
										booleanParam(name: 'publish', defaultValue: Boolean.valueOf(publish), description: 'Set true to publish artifacts'),
							  			booleanParam(name: 'PROMOTE_TO_PRODUCT', defaultValue: false, description: 'Set true to promote RPMS'),
										booleanParam(name: 'PROMOTE_HELM', defaultValue: false, description: 'Set true to promote helm and docker'),
										booleanParam(name: 'INSTALL', defaultValue: Boolean.valueOf(INSTALL), description: 'Set true to trigger installation'),
									]),
									disableConcurrentBuilds(),
									pipelineTriggers([cron(cronTime), pollSCM('H/2 * * * *')]),
									buildDiscarder(logRotator(daysToKeepStr: '30', artifactDaysToKeepStr: '0'))
									])
								} else {
									properties([
                                                                        parameters([
                                                                                string(name: 'PACKAGE_DIRECTORY', defaultValue: packageRepository , description: 'PACKAGE DIRECTORY in installers'),
                                                                                string(name: 'BUILD_PROFILE', defaultValue: 'Cloudera' , description: 'Build profiles'),
                                                                                string(name: 'RELEASE', defaultValue: currentRelease, description: 'Empty release variable'),
                                                                                booleanParam(name: 'publish', defaultValue: Boolean.valueOf(publish), description: 'Set true to publish artifacts'),
                                                                        ]),
																		disableConcurrentBuilds(),
                                                                        buildDiscarder(logRotator(daysToKeepStr: '30', artifactDaysToKeepStr: '0')),
                                                                        pipelineTriggers([
                                                                                  gerrit(customUrl: '',
                                                                                         gerritProjects: [[
                                                                                           branches:
                                                                                             [[compareType: 'PLAIN',
                                                                                               pattern: branch //Change for other branches
                                                                                             ]],
                                                                                           compareType: 'PLAIN',
                                                                                           disableStrictForbiddenFileVerification: false,
                                                                                           pattern: 'CA4CI/' + componentRepoPath,  //Your repo name here
						                                           filePaths:
                                                                                                [[ compareType: 'ANT',
                                                                                                        pattern: filePathPattern
                                                                                                ]],
											   forbiddenFilePaths : 
												[[ compareType: 'ANT',
                                                                                                        pattern: forbiddenfilePathPattern
                                                                                                ]],
                                                                                         ]],
                                                                                         serverName: 'nokia-bhgerrit',
                                                                                         triggerOnEvents:
                                                                                           [patchsetCreated(excludeDrafts: true,
                                                                                                            excludeNoCodeChange: true,
                                                                                                            excludeTrivialRebase: true)
                                                                                           ]
                                                                                        )
                                                                                ])
                                                                        ])
									env.publish = env.publishRPM
								}
						}
						stage ('Build RPM and deploy') {
							if(env.gerrit == "true") {
							   if(env.buildType == "contentpack") {
								sh '''
						        		chmod 777 devops_ci/scripts/buildscripts/list_modified_contentpacks.sh
								        devops_ci/scripts/buildscripts/list_modified_contentpacks.sh ${componentToBuild} $GERRIT_PATCHSET_REVISION
								        cat input.property
								'''
								def props = readProperties  file: 'input.property'
								env.buildthese = props['buildthese']
								env.ADAPTATION = props['ADAPTATION']
								env.UNIVERSE = props['UNIVERSE']
								env.NEW_UNIVERSE = props['NEW_UNIVERSE']
								echo "buildthese = $buildthese"
								echo "ADAPTATION = $ADAPTATION"
								echo "UNIVERSE = $UNIVERSE"
								if(env.packageRepository == "COMMON_ADAPTATIONS" || env.packageRepository == "COMMON_UNIVERSE") {
									env.componentToBuild = "ContentPacks/CQI"
									env.componentRepoPath = "ContentPacks/CQI"
								}
							    }
						        }
							compileAndBuildRpm()
						}

					if(env.gerrit == "false"){
					   if(env.featureBuild == "false") {
                        			if(env.talkocheck == "true") {
    						    stage ('Talko Check'){
    						        talkoCheck()
    						    }
    						}
					   }
                    			}

					if(env.gerrit == "false") {
						if(env.DOCKERBUILD == "true" && buildType != 'contentpack'){
							stage ('Build Docker image and deploy') {
								buildDockerImage()
							}
						}
						if(env.DOCKERSECURITYSCAN == "true") {
							stage ('Docker security scan') {
								scanDockerForSecurityViolations()
							}
						}
						if(env.INSTALL == "true") {
                                                        stage ('Trigger Installation job') {
								def triggerInstall = new com.nokia.ca4ci.build.ca4cibuild()
                                                        	triggerInstall.callInstallJob()
                                                        }
                                                }
						stage ('Clean up') {
							cleanUpInstallers()
							env.PROMOTE_TO_PRODUCT = env.PROMOTE
							env.PROMOTE_HELM = env.PROMOTEHELM
							env.INSTALL = env.INSTALL_CI
							env.publish = env.publishRPM
							script {
							     properties([
                                        				    parameters([
    										string(name: 'PACKAGE_DIRECTORY', defaultValue: packageRepository , description: 'PACKAGE DIRECTORY in installers'),
    										string(name: 'BUILD_PROFILE', defaultValue: 'Cloudera' , description: 'Build profiles'),
    										string(name: 'RELEASE', defaultValue: currentRelease, description: 'Empty release variable'),
    										booleanParam(name: 'CREATE_TAG', defaultValue: true, description: 'Set true to create tag info'),
										booleanParam(name: 'DOCKERBUILD', defaultValue: Boolean.valueOf(dockerbuild), description: 'Set true to build docker'),
										booleanParam(name: 'DOCKERSECURITYSCAN', defaultValue: docker_security_scan, description: 'Set true to enable security scan for docker'),
    										booleanParam(name: 'publish', defaultValue: Boolean.valueOf(publish), description: 'Set true to publish artifacts'),
    							  			booleanParam(name: 'PROMOTE_TO_PRODUCT', defaultValue: Boolean.valueOf(PROMOTE_TO_PRODUCT), description: 'Set true to promote RPMS'),
										booleanParam(name: 'PROMOTE_HELM', defaultValue: Boolean.valueOf(PROMOTE_HELM), description: 'Set true to promote helm'),
										booleanParam(name: 'INSTALL', defaultValue: Boolean.valueOf(INSTALL), description: 'Set true to triger installation'),
									    ]),
									    disableConcurrentBuilds(),
                                                                            pipelineTriggers([cron(cronTime), pollSCM('H/2 * * * *')]),
                                                                            buildDiscarder(logRotator(daysToKeepStr: '30', artifactDaysToKeepStr: '0'))

							     ])
							}
						}
					}
				    }
				} catch (err) {
					currentBuild.result = 'FAILURE'
					throw err
				}
				finally {
                        		junit allowEmptyResults: true, testResults: '**/target/surefire-reports/**/*.xml'
		                }
			}
		}

	}
