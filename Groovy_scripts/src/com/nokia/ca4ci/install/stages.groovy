package com.nokia.ca4ci.install

def stages() {
	stage ('Clone') {
			script {
				echo "Cloning of code started"
				def ca4ciInstall = new com.nokia.ca4ci.install.ca4ciInstall()
				ca4ciInstall.checkoutCIRepo()
				ca4ciInstall.checkoutDependantComponents()
				echo "Cloning of code finished"
				sh '''
					chmod 600 ${key}
					mv ${ipFileName} ${WORKSPACE}/IP.txt
					mv ${hostFileName} ${WORKSPACE}/hosts
				'''
			}
    if(env.ci_type == "product") {
			properties([
				parameters([
					string(name: 'PACKAGE_DIRECTORY', defaultValue: ' ' , description: 'PACKAGE DIRECTORY in installers'),
					string(name: 'BUILD_PROFILE', defaultValue: 'Cloudera' , description: 'Build profiles'),
					string(name: 'RELEASE', defaultValue: '3' , description: 'Empty release variable'),
					string(name: 'PLATFORM_DISTRIBUTION', defaultValue: ' ' , description: 'parcels number'),
					booleanParam(name: 'SNAPSHOT_REVERT', defaultValue: true, description: 'Set true to revert the snapshot'),
					booleanParam(name: 'COPY_RPMS', defaultValue: true, description: 'Set true to copy the rpms'),
					booleanParam(name: 'IFW_UPDATE', defaultValue: true, description: 'Set true to update ip files'),
					booleanParam(name: 'UPDATE_CONTENT_PACKS', defaultValue: true, description: 'Set true to update content packs'),
					booleanParam(name: 'INSTALL', defaultValue: true, description: 'Set true to install the product'),
					booleanParam(name: 'COPY_DATA', defaultValue: true, description: 'Set true to copy data'),
					booleanParam(name: 'UPDATE_BOUNDARY_AND_RETENTION', defaultValue: true, description: 'Set true to load the data'),
					booleanParam(name: 'CEI_Parallel_insert_Workaround', defaultValue: true, description: 'Set true to load the data'),
					booleanParam(name: 'LOAD_DIMENSION_TABLES', defaultValue: true, description: 'Set true to load the data'),
					booleanParam(name: 'START_TOPOLOGY_AND_USAGE_LOADING', defaultValue: false, description: 'Set true to load the data'),
					booleanParam(name: 'START_TOPOLOGY_AND_USAGE_LOADING_WITH_REDUCED_DATASET', defaultValue: true, description: 'Set true to load the data'),
					booleanParam(name: 'START_DATA_AGGREGATION', defaultValue: true, description: 'Set true to load the data'),
					booleanParam(name: 'ENABLE_ADQM_AND_RELOAD_DATA', defaultValue: false, description: 'Set true to load the data'),
					booleanParam(name: 'DISABLE_ADQM', defaultValue: false, description: 'Set true to load the data'),
					booleanParam(name: 'ENABLE_TIMEZONE_FOR_TNP', defaultValue: false, description: 'Set true to load the data'),
					booleanParam(name: 'UPDATE_BOUNDARY_AND_RETENTION_FOR_TNP', defaultValue: false, description: 'Set true to load the data'),
					booleanParam(name: 'RUN_TNP_JOBS', defaultValue: false, description: 'Set true to load the data'),
					booleanParam(name: 'COPY_DATA_FOR_YESTERDAY', defaultValue: false, description: 'Set true to load the data'),
					
					
				
					
				]),
				disableConcurrentBuilds(),
				buildDiscarder(logRotator(daysToKeepStr: '30', artifactDaysToKeepStr: '0'))
			])
			
		}
		
		if(env.ci_type == "analytics") {
			properties([
				parameters([
					string(name: 'PACKAGE_DIRECTORY', defaultValue: packageDirectory , description: 'PACKAGE DIRECTORY in installers'),
					string(name: 'BUILD_PROFILE', defaultValue: 'Cloudera' , description: 'Build profiles'),
					string(name: 'RELEASE', defaultValue: '3' , description: 'Empty release variable'),
					string(name: 'PLATFORM_DISTRIBUTION', defaultValue: 'platformdistribution' , description: 'parcels number'),
					booleanParam(name: 'SNAPSHOT_REVERT', defaultValue: true, description: 'Set true to revert the snapshot'),
					booleanParam(name: 'COPY_RPMS', defaultValue: true, description: 'Set true to copy the rpms'),
					booleanParam(name: 'IFW_UPDATE', defaultValue: true, description: 'Set true to update ip files'),
					booleanParam(name: 'UPDATE_CONTENT_PACKS', defaultValue: true, description: 'Set true to update content packs'),
					booleanParam(name: 'INSTALL', defaultValue: true, description: 'Set true to install the product'),
					booleanParam(name: 'SOAPUITESTING', defaultValue: true, description: 'Set true to testing'),
	
					
				]),
				disableConcurrentBuilds(),
				buildDiscarder(logRotator(daysToKeepStr: '30', artifactDaysToKeepStr: '0'))
			])
			
		}
		
		if(env.ci_type == "cqi") {
			properties([
				parameters([
					string(name: 'PACKAGE_DIRECTORY', defaultValue: packageDirectory , description: 'PACKAGE DIRECTORY in installers'),
					string(name: 'BUILD_PROFILE', defaultValue: 'Cloudera' , description: 'Build profiles'),
					string(name: 'RELEASE', defaultValue: '3' , description: 'Empty release variable'),
					booleanParam(name: 'SNAPSHOT_REVERT', defaultValue: true, description: 'Set true to revert the snapshot'),
					booleanParam(name: 'COPY_RPMS', defaultValue: true, description: 'Set true to copy the rpms'),
					booleanParam(name: 'IFW_UPDATE', defaultValue: true, description: 'Set true to update ip files'),
					booleanParam(name: 'UPDATE_CONTENT_PACKS', defaultValue: true, description: 'Set true to update content packs'),
					booleanParam(name: 'INSTALL', defaultValue: true, description: 'Set true to install the product'),
					booleanParam(name: 'ROBO_RPM_INSTALL', defaultValue: true, description: 'Set true to testing'),
					booleanParam(name: 'Spare_Dim_Denorm_Update', defaultValue: true, description: 'Set true to testing'),
					booleanParam(name: 'Flexi_RPM_install', defaultValue: true, description: 'Set true to testing'),
					booleanParam(name: 'INSTALL_PABOT', defaultValue: true, description: 'Set true to testing'),
					booleanParam(name: 'LOAD_COMMON_DIMENSION', defaultValue: true, description: 'Set true to testing'),
					booleanParam(name: 'COPY_RESULT', defaultValue: true, description: 'Set true to testing'),
					booleanParam(name: 'TZ_ENABLE', defaultValue: true, description: 'Set true to testing'),
					booleanParam(name: 'ETL_WA', defaultValue: true, description: 'Set true to testing'),
					booleanParam(name: 'Trigger_TZ_Test_Suites', defaultValue: true, description: 'Set true to testing'),
					booleanParam(name: 'ROBO_COPY_RESULT', defaultValue: true, description: 'Set true to testing'),
					booleanParam(name: 'RPM_UNINSTALL', defaultValue: true, description: 'Set true to testing'),
					booleanParam(name: 'Spare_Dim_Denorm_Update', defaultValue: true, description: 'Set true to testing'),
					booleanParam(name: 'Flexi_RPM_install', defaultValue: true, description: 'Set true to testing'),
					
					
				]),
				disableConcurrentBuilds(),
				buildDiscarder(logRotator(daysToKeepStr: '30', artifactDaysToKeepStr: '0'))
			])
			
		}
		
		if(env.ci_type == "cei") {
			properties([
				parameters([
					string(name: 'PACKAGE_DIRECTORY', defaultValue: packageDirectory , description: 'PACKAGE DIRECTORY in installers'),
					string(name: 'BUILD_PROFILE', defaultValue: 'Cloudera' , description: 'Build profiles'),
					string(name: 'RELEASE', defaultValue: '3' , description: 'Empty release variable'),
					string(name: 'PLATFORM_DISTRIBUTION', defaultValue: 'platformdistribution' , description: 'parcels number'),
					booleanParam(name: 'SNAPSHOT_REVERT', defaultValue: true, description: 'Set true to revert the snapshot'),
					booleanParam(name: 'COPY_RPMS', defaultValue: true, description: 'Set true to copy the rpms'),
					booleanParam(name: 'IFW_UPDATE', defaultValue: true, description: 'Set true to update ip files'),
					booleanParam(name: 'UPDATE_CONTENT_PACKS', defaultValue: true, description: 'Set true to update content packs'),
					booleanParam(name: 'INSTALL', defaultValue: true, description: 'Set true to install the product'),
					booleanParam(name: 'CEI_COMMONDIMENSION', defaultValue: true, description: 'Set true to testing'),
					booleanParam(name: 'CEI_ROBO_TEST', defaultValue: true, description: 'Set true to testing'),
					booleanParam(name: 'CEI_UNV_ROBO_TEST', defaultValue: true, description: 'Set true to testing'),
					
				]),
				disableConcurrentBuilds(),
				buildDiscarder(logRotator(daysToKeepStr: '30', artifactDaysToKeepStr: '0'))
			])
			
		}
	}
	if(env.SNAPSHOT_REVERT == "true") {
		stage ('Revert Snapshot') {
			def ca4ciInstall = new com.nokia.ca4ci.install.ca4ciInstall()
			ca4ciInstall.revertSnapshot()
		}
	}
	if(env.COPY_RPMS == "true") {
		stage ('Copy RPMS') {
			container("cemodtools") {
				def ca4ciInstall = new com.nokia.ca4ci.install.ca4ciInstall()
				ca4ciInstall.copyRPMS()
			}
		}
	}
	if(env.IFW_UPDATE == "true") {
		stage ('IFW UPDATE') {
			def ca4ciInstall = new com.nokia.ca4ci.install.ca4ciInstall()
			ca4ciInstall.ifwUpdate()
		}
	}
	if(env.UPDATE_CONTENT_PACKS == "true") {
		stage ('Update ContenPacks') {
			def ca4ciInstall = new com.nokia.ca4ci.install.ca4ciInstall()
			ca4ciInstall.updateContentPacks()
		}
	}
	if(env.INSTALL == "true") {
		stage ('Install Product') {
			def ca4ciInstall = new com.nokia.ca4ci.install.ca4ciInstall()
			ca4ciInstall.installProduct()
		}
	}
	
		if(env.COPY_DATA == "true") {
		stage ('copy Data ') {
		container("cemodtools") {
			def productci = new com.nokia.ca4ci.testing.productci()
	        productci.copydataforproduct()
			}
		}
	}

	if(env.UPDATE_BOUNDARY_AND_RETENTION == "true") {
		stage ('Data loading') {
		container("cemodtools") {
			def productci = new com.nokia.ca4ci.testing.productci()
			productci.Update_Boundary_and_Retention()
			}
		}
	}
	if(env.CEI_Parallel_insert_Workaround == "true") {
		stage ('Data loading') {
		container("cemodtools") {
			def productci = new com.nokia.ca4ci.testing.productci()
			productci.CEI_Parallel_insert_Workaround()
			}
		}
	}
	if(env.LOAD_DIMENSION_TABLES == "true") {
		stage ('Data loading') {
		container("cemodtools") {
			def productci = new com.nokia.ca4ci.testing.productci()
			productci.Load_Dimensuon_Tables()
			}
		}
	}
	
	if(env.START_TOPOLOGY_AND_USAGE_LOADING_WITH_REDUCED_DATASET == "true") {
		stage ('Data loading') {
		container("cemodtools") {
			def productci = new com.nokia.ca4ci.testing.productci()
			productci.Start_Topology()
			}
		}
	}
	if(env.START_DATA_AGGREGATION == "true") {
		stage ('Data loading') {
		container("cemodtools") {
			def productci = new com.nokia.ca4ci.testing.productci()
			
			productci.Start_Data_Aggregation()
			}
		}
	}
	if(env.ENABLE_ADQM_AND_RELOAD_DATA == "true") {
		stage ('Data loading') {
		container("cemodtools") {
			def productci = new com.nokia.ca4ci.testing.productci()
			
			productci.Start_Data_Aggregation()
			}
		}
	}
	
	if(env.DISABLE_ADQM == "true") {
		stage ('Data loading') {
		container("cemodtools") {
			def productci = new com.nokia.ca4ci.testing.productci()
			
			productci.Start_Data_Aggregation()
			}
		}
	}
	
	if(env.ENABLE_TIMEZONE_FOR_TNP == "true") {
		stage ('Data loading') {
		container("cemodtools") {
			def productci = new com.nokia.ca4ci.testing.productci()
			
			productci.Start_Data_Aggregation()
			}
		}
	}
	if(env.UPDATE_BOUNDARY_AND_RETENTION_FOR_TNP == "true") {
		stage ('Data loading') {
		container("cemodtools") {
			def productci = new com.nokia.ca4ci.testing.productci()
			
			productci.Start_Data_Aggregation()
			}
		}
	}
	if(env.RUN_TNP_JOBS == "true") {
		stage ('Data loading') {
		container("cemodtools") {
			def productci = new com.nokia.ca4ci.testing.productci()
			
			productci.Start_Data_Aggregation()
			}
		}
	}
	if(env.COPY_DATA_FOR_YESTERDAY == "true") {
		stage ('Data loading') {
		container("cemodtools") {
			def productci = new com.nokia.ca4ci.testing.productci()
			
			productci.Start_Data_Aggregation()
			}
		}
	}
	
	if(env.SOAPUITESTING == "true") {
		stage ('TESTING') {
		container("cemodtools") {
			def analyticsci = new com.nokia.ca4ci.testing.analyticsci()
			analyticsci.soapuitesting()
			analyticsci.soapuireports()
			}
		}
	}
	
	if(env.CQI_COMMONDIMENSION == "true") {
		stage ('COMMONDIMENSION') {
		container("rhel7") {
			def analyticsci = new com.nokia.ca4ci.testing.cqici()
			analyticsci.commondimension()
			}
		}
	}
	if(env.CQI_ROBO_TEST == "true") {
		stage ('CQI_ROBO_TEST') {
		container("rhel7") {
			def analyticsci = new com.nokia.ca4ci.testing.cqici()
			analyticsci.testing()
			}
		}
	}
	if(env.CQI_UNV_ROBO_TEST == "true") {
		stage ('CQI_UNV_ROBO_TEST') {
		container("rhel7") {
			def analyticsci = new com.nokia.ca4ci.testing.cqici()
			analyticsci.testing()
			}
		}
	}
		if(env.CEI_COMMONDIMENSION == "true") {
		stage ('COMMONDIMENSION') {
		container("rhel7") {
			def analyticsci = new com.nokia.ca4ci.testing.cqici()
			analyticsci.commondimension()
			}
		}
	}
	if(env.CEI_ROBO_TEST == "true") {
		stage ('CQI_ROBO_TEST') {
		container("rhel7") {
			def analyticsci = new com.nokia.ca4ci.testing.cqici()
			analyticsci.testing()
			}
		}
	}
	if(env.CEI_UNV_ROBO_TEST == "true") {
		stage ('CQI_UNV_ROBO_TEST') {
		container("rhel7") {
			def analyticsci = new com.nokia.ca4ci.testing.cqici()
			analyticsci.testing()
			}
		}
	}
}

