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
	
		env.RELEASE = "3"	
		env.projectRepoUrl = "ssh://ca_cp@bhgerrit.ext.net.nokia.com:8282/CA4CI/"
		env.helmPromotedDependendecyFile = "devops_ci/collector/product/" + RELEASE + "/Cloudera/promotedHelmdependencies.txt"
		env.deploy_profile = "cemod-deploy-artifactory"
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
		env.NEXT_RELEASE = "4"
		env.cronTime = config.cronTime
		env.currentRelease = config.currentRelease
		env.imageNames = config.imageNames
		env.installJobName = config.installJobName
		env.publishHelm = config.publishHelm
		env.gerrit = config.gerrit
		env.filePathPattern = config.filePathPattern
		env.forbiddenfilePathPattern = config.forbiddenfilePathPattern
		env.sonar_profile = config.sonar_profile
		env.dockerBuildPath = config.dockerBuildPath
				
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
		if(env.publishHelm == "null") {
                        env.publishHelm = "true"
                }
		if(env.publishDocker == "null") {
			env.publishDocker = "true"
		}
		if(env.currentRelease == "null") {
                        env.currentRelease = "3"
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
        	}
		if(env.PROPERTY == "null") {
                        env.PROPERTY = ""
                }
		if(env.gerrit == "null") {
                        env.gerrit = "false"
                }
		if(env.filePathPattern == "null") {
                        env.filePathPattern = "**"
                }
		if(env.forbiddenfilePathPattern == "null") {
                        env.forbiddenfilePathPattern = ""
                }
                if(env.gerrit == "true") {
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
		podTemplate(label: label, inheritFrom: 'k8s-dind', containers: [
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
				
				def commonList = [
	                                string(name: 'PACKAGE_DIRECTORY', defaultValue: packageRepository , description: 'PACKAGE DIRECTORY in installers'),
                                        string(name: 'BUILD_PROFILE', defaultValue: 'Cloudera' , description: 'Build profiles'),
                                        string(name: 'RELEASE', defaultValue: currentRelease , description: 'Empty release variable'),
                                        booleanParam(name: 'CREATE_TAG', defaultValue: true, description: 'Set true to create tag info'),
                                	booleanParam(name: 'publish', defaultValue: true, description: 'Set true to publish artifacts'),
                                        booleanParam(name: 'PROMOTE_TO_PRODUCT', defaultValue: false, description: 'Set true to promote RPMS'),
                                ]

                                def cemnovaParams = [
                                        booleanParam(name: 'DOCKERBUILD', defaultValue: false, description: 'Set true to build docker'),
                                        booleanParam(name: 'DOCKERSECURITYSCAN', defaultValue: false, description: 'Set true to enable security scan for docker'),
                                        booleanParam(name: 'PROMOTE_HELM', defaultValue: false, description: 'Set true to promote helm and docker'),
                                        booleanParam(name: 'INSTALL', defaultValue: false, description: 'Set true to trigger installation'),
                                ]

                                def otherProps = {
                                        disableConcurrentBuilds()
                                        pipelineTriggers([cron(cronTime), pollSCM('H/2 * * * *')])
                                        buildDiscarder(logRotator(daysToKeepStr: '30', artifactDaysToKeepStr: '0'))
                                }
				
				def startBuild = {String profileName, String osName, String componentNameToBuild, String buildType, String deployToArtefactory,String thirdpartyComponent ->
						echo "Building component started"
						try {
							container(osName) {
								withCredentials([usernamePassword(credentialsId: 'cemod-artifactory', passwordVariable: 'OS_PASSWORD', usernameVariable: 'OS_USERNAME')]) {
									sh "sed -i s/OS_USERNAME/${OS_USERNAME}/ /nfsshare/nemesis/opt/apache-maven-3.5.3/conf/settings.xml"
									sh "sed -i s/OS_PASSWORD/${OS_PASSWORD}/ /nfsshare/nemesis/opt/apache-maven-3.5.3/conf/settings.xml"
									env.BUILD_PROFILE = profileName
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
							if(env.gerrit == "false"){
	                                                        def info1 = new com.nokia.ca4ci.setReleaseNumber()
        	                                                info1.setRelease(profile)
							}
                                                }
						def osName = RHEL7_NODE
						if (profile == "Wandisco27"){
							osName = RHEL6_NODE
						}
						def buildStatus = startBuild(profile, osName, env.componentToBuild, env.buildType, env.deployToArtefactory, env.thirdpartyComponent)
						if ( buildStatus == "false"){
							echo "Build has failed. Please search for errors with a search key word as [ERROR]"
							currentBuild.result = 'FAILURE' 
						}
						env.RELEASE = env.RELEASE1
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
					container('cemodtools') {
					 env.docker_images = env.imageNames.replaceAll("\\s",",")
					 echo "docker_images = ${env.docker_images}"
					 sh  """
						   docker rm -f clair || true
						   docker run -dit --net=host --rm --name=clair -v /tmp:/tmp -v /var/run/docker.sock:/var/run/docker.sock csf-docker-delivered.repo.lab.pl.alcatel-lucent.com/tools/clair:latest bash
						   docker cp clair:/root/get_patch.py .
						   POST_GRES_DATA=`python get_patch.py`
						   echo \${POST_GRES_DATA}
						   docker exec clair /bin/sh -c "cp -R /root/clair  /tmp/; cd /tmp/clair/docker-compose-data; curl -# -O https://repo.lab.pl.alcatel-lucent.com/csf-generic-candidates/CSF-POSTGRES-DATA/\${POST_GRES_DATA}; tar -xzf \${POST_GRES_DATA}; cd /tmp/clair; docker-compose -f docker-compose.yml up -d; . /root/dockerImageScan.sh 'cemod-docker-candidates.repo.lab.pl.alcatel-lucent.com/${env.docker_images}:latest'"
						   docker cp clair:/tmp/clair/docker-compose-data/clairctl-reports/ .
						 """
						 publishHTML([allowMissing: false, alwaysLinkToLastBuild: true, keepAll: true, reportDir: 'clairctl-reports/html', reportFiles: 'analysis-*-latest.html', reportName: 'Docker Security Scan Report', reportTitles: 'Docker Security Scan'])
					  }
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
									checkoutCIRepo()
									checkoutDependantComponents()
									echo "Cloning of code finished"
								}
								if(env.gerrit == "false") {
									def info = new com.nokia.ca4ci.gitdetails()
									info.getGITdetails(componentRepoPath)

									properties([
										parameters(commonList + cemnovaParams),
										otherProps(),
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
							compileAndBuildRpm()
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
