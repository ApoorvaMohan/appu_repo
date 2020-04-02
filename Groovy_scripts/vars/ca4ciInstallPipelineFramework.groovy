import com.nokia.ca4ci.install.*
import com.nokia.ca4ci.*

def call(body) {

        def config = [:]
        body.resolveStrategy = Closure.DELEGATE_FIRST
        body.delegate = config
        body()

		TOOLS_NODE= 'cemodtools'
		
		env.projectRepoUrl = "ssh://bhgerrit.ext.net.nokia.com:8282/CA4CI/"
		env.key = "ContinuousIntegration/buildscripts/install/oneclickinstallation/keys/crowd-key.pem"
		env.OS_VERSION = "CEMoD_OS_RHEL7U6_Build2.09_GFS_NODE_V1.1.qcow2"
		env.OS_PORTAL = "CEMoD_OS_RHEL7U6_Build2.09_NODES_V1.1.qcow2"
		env.machine_type = config.machine_type
		env.ci_type = config.ci_type
		env.dependantComponents = config.dependantComponents
		env.packageDirectory = config.packageDirectory
		env.templateFolder = config.templateFolder
		env.snapName = config.snapName
		env.ipFileName = config.ipFileName
		env.hostFileName = config.hostFileName
		env.vlabProjectName = config.vlabProjectName
		env.vlabLogName = config.vlabLogName
		env.vlabImageName = config.vlabImageName
		env.jenkins = config.jenkins
		env.contentPacks = config.contentPacks
		env.collect_package = config.collect_package
		env.project_name = config.project_name
		env.platformdistribution = config.platformdistribution
				
		if(env.vlabImageName == "null") {
                        env.vlabImageName = ""
                }
		if(env.vlabLogName == "null") {
                        env.vlabLogName = ""
                }
		if(env.jenkins == "null") {
                        env.jenkins = "local"
                }
				if(env.collect_package == "null") {
                        env.collect_package = "PRODUCT:latest"
                }
		if(env.platformdistribution == "null") {
                        env.platformdistribution = ""
                }
		if(env.ci_type == "null") {
                        env.ci_type = "project"
                }
		if(env.project_name == "v3290") {
			env.LOG_NAME = "--os-username a53gupta --os-password lDzJZtbyhRdOTqjcPhYUZuxMvBXnlXAu --os-tenant-name v3290_Analytics_CEMOD --os-auth-url https://10.75.237.100:13000/v2.0"
                        env.source_file = "/home/centos/v3290_Analytics_CEMOD-openrc.sh"
		}
		if(env.project_name == "v4390") {
			env.LOG_NAME = "--os-username a53gupta --os-password lDzJZtbyhRdOTqjcPhYUZuxMvBXnlXAu --os-tenant-name v4390_Analytics_CEMOD --os-auth-url https://10.75.237.100:13000/v2.0"
                        env.source_file = "/home/centos/v4390_Analytics_CEMoD-openrc.sh"
		}
		if(env.project_name == "v7985") {
			env.LOG_NAME = "--os-username a20423 --os-password OwpnCFwpEqoYJZeEtiNg --os-tenant-name v7986_DI_CEMoD --os-auth-url https://10.75.236.4:13000/v2.0"
                        env.source_file = "/home/centos/v7986_DI_CEMoD-openrc.sh"
		}
		if(env.project_name == "v7986") {
			env.LOG_NAME = "--os-username a20423 --os-password OwpnCFwpEqoYJZeEtiNg --os-tenant-name v7986_DI_CEMoD --os-auth-url https://10.75.236.4:13000/v2.0"
			env.source_file = "/home/centos/v7986_DI_CEMoD-openrc.sh"
		}
		
		if(env.jenkins == "local") {
			def label = "master"
			node(label) {
                                deleteDir()

                                try {
                                        timestamps {
                                                def callstages = new com.nokia.ca4ci.install.stages()
                                                callstages.stages()
                                        }
                                } catch (err) {
                                        currentBuild.result = 'FAILURE'
                                        throw err
                                }
                        }			
	
		}
		if(env.jenkins == "jenkinsFarm") {
			def label = "k8s-ansible-${cto.devops.jenkins.Utils.getTimestamp()}"
			podTemplate(label: label, inheritFrom: 'k8s-build', containers: [
		  	containerTemplate(name: 'cemodtools', alwaysPullImage:true, image: 'cemod-docker-releases.repo.lab.pl.alcatel-lucent.com/cemod_tools_image:latest', workingDir: '/home/jenkins', ttyEnabled: true, command: 'cat'),
			])
			{
				node(label) {
					deleteDir()
					try {
						timestamps {
							def callstages = new com.nokia.ca4ci.install.stages()
							callstages.stages()
						}
					} catch (err) {
						currentBuild.result = 'FAILURE'
						throw err
					}
				}
			}
		}

	}
