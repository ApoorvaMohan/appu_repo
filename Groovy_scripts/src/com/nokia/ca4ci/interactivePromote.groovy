package com.nokia.ca4ci

def interactivePromote(buildInfoPath) {

    def buildInfoFile = buildInfoPath + "/target/build-info.json"
    def artifactoryServer = Artifactory.newServer url: env.ARTIFACTORY_HTTPS_URL, credentialsId: 'cemod-artifactory'
    def server = Artifactory.newServer url: env.ARTIFACTORY_HTTPS_URL, credentialsId: 'cemod-artifactory'
    artifactoryServer.credentialsId = 'cemod-artifactory'
    def buildInfo = readJSON file: buildInfoFile
    def display = "Promote release candidate " + BUILD_PROFILE
     
    def promotionConfig = [
      'buildName'          : buildInfo.name,
      'buildNumber'        : buildInfo.number,
      'status'             : 'Released',
      'targetRepo'         : 'cemod-mvn-releases',
      'sourceRepo'         : 'cemod-mvn-candidates-local',
      'includeDependencies': false,
      'copy'               : true, // "copy" must be used because "move" requires delete permission
      'failFast'           : true
    ]
    
    Artifactory.addInteractivePromotion server: artifactoryServer, promotionConfig: promotionConfig, displayName: display
    //server.promote promotionConfig
}
