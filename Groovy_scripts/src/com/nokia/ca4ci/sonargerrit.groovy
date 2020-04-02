package com.nokia.ca4ci

def sonarToGerrit() {

	    def props = readProperties  file: 'my.properties'
            def path1 = props['path1']
            def path2 = props['path2']
            def path3 = props['path3']
            def path4 = props['path4']
            def path5 = props['path5']
            def path6 = props['path6']
            def path7 = props['path7']
            def path8 = props['path8']
            
            def parentPath1 = props['parentPath1']
            def parentPath2 = props['parentPath2']
            def parentPath3 = props['parentPath3']
            def parentPath4 = props['parentPath4']
            def parentPath5 = props['parentPath5']
            def parentPath6 = props['parentPath6']
            def parentPath7 = props['parentPath7']
            def parentPath8 = props['parentPath8']
            
            
            if (path7!=null){
	  sonarToGerrit category: 'Verified', changedLinesOnly: true, httpPassword: 'JEkinns', httpUsername: 'cn=ca_cemod,ou=SystemUsers,o=EEDIR', issueComment: '''<severity> SonarQube violation:

      <message>
	  Read more: <rule_url>''', issuesNotification: 'OWNER', issuesScore: '-1', newIssuesOnly: true, noIssuesNotification: 'NONE', noIssuesScore: '+1', noIssuesToPostText: 'SonarQube violations have not been found.', overrideCredentials: false, postScore: true, severity: 'MAJOR', someIssuesToPostText: '<total_count> SonarQube violations have been found.', sonarURL: '"http://10.142.139.149:9001"', subJobConfigs: [[projectPath: parentPath1 , sonarReportPath: path1],[projectPath: parentPath2 , sonarReportPath: path2],[projectPath: parentPath3 , sonarReportPath: path3],[projectPath: parentPath4 , sonarReportPath: path4],[projectPath: parentPath5 , sonarReportPath: path5],[projectPath: parentPath6, sonarReportPath: path6], [projectPath: parentPath7, sonarReportPath: path7]] 
}  else if  (path6!=null){
	  sonarToGerrit category: 'Verified', changedLinesOnly: true, httpPassword: 'JEkinns', httpUsername: 'cn=ca_cemod,ou=SystemUsers,o=EEDIR', issueComment: '''<severity> SonarQube violation:

      <message>
	  Read more: <rule_url>''', issuesNotification: 'OWNER', issuesScore: '-1', newIssuesOnly: true, noIssuesNotification: 'NONE', noIssuesScore: '+1', noIssuesToPostText: 'SonarQube violations have not been found.', overrideCredentials: false, postScore: true, severity: 'MAJOR', someIssuesToPostText: '<total_count> SonarQube violations have been found.', sonarURL: '"http://10.142.139.149:9001"', subJobConfigs: [[projectPath: parentPath1 , sonarReportPath: path1],[projectPath: parentPath2 , sonarReportPath: path2],[projectPath: parentPath3 , sonarReportPath: path3],[projectPath: parentPath4 , sonarReportPath: path4],[projectPath: parentPath5 , sonarReportPath: path5],[projectPath: parentPath6, sonarReportPath: path6]] 
}  else if  (path5!=null){
	  sonarToGerrit category: 'Verified', changedLinesOnly: true, httpPassword: 'JEkinns', httpUsername: 'cn=ca_cemod,ou=SystemUsers,o=EEDIR', issueComment: '''<severity> SonarQube violation:

      <message>
	  Read more: <rule_url>''', issuesNotification: 'OWNER', issuesScore: '-1', newIssuesOnly: true, noIssuesNotification: 'NONE', noIssuesScore: '+1', noIssuesToPostText: 'SonarQube violations have not been found.', overrideCredentials: false, postScore: true, severity: 'MAJOR', someIssuesToPostText: '<total_count> SonarQube violations have been found.', sonarURL: '"http://10.142.139.149:9001"', subJobConfigs: [[projectPath: parentPath1 , sonarReportPath: path1],[projectPath: parentPath2 , sonarReportPath: path2],[projectPath: parentPath3 , sonarReportPath: path3],[projectPath: parentPath4 , sonarReportPath: path4],[projectPath: parentPath5 , sonarReportPath: path5]] 
}else if  (path4!=null){
	  sonarToGerrit category: 'Verified', changedLinesOnly: true, httpPassword: 'JEkinns', httpUsername: 'cn=ca_cemod,ou=SystemUsers,o=EEDIR', issueComment: '''<severity> SonarQube violation:

      <message>
	  Read more: <rule_url>''', issuesNotification: 'OWNER', issuesScore: '-1', newIssuesOnly: true, noIssuesNotification: 'NONE', noIssuesScore: '+1', noIssuesToPostText: 'SonarQube violations have not been found.', overrideCredentials: false, postScore: true, severity: 'MAJOR', someIssuesToPostText: '<total_count> SonarQube violations have been found.', sonarURL: '"http://10.142.139.149:9001"', subJobConfigs: [[projectPath: parentPath1 , sonarReportPath: path1],[projectPath: parentPath2 , sonarReportPath: path2],[projectPath: parentPath3 , sonarReportPath: path3],[projectPath: parentPath4 , sonarReportPath: path4]] 
}else if  (path3!=null){
	  sonarToGerrit category: 'Verified', changedLinesOnly: true, httpPassword: 'JEkinns', httpUsername: 'cn=ca_cemod,ou=SystemUsers,o=EEDIR', issueComment: '''<severity> SonarQube violation:

      <message>
	  Read more: <rule_url>''', issuesNotification: 'OWNER', issuesScore: '-1', newIssuesOnly: true, noIssuesNotification: 'NONE', noIssuesScore: '+1', noIssuesToPostText: 'SonarQube violations have not been found.', overrideCredentials: false, postScore: true, severity: 'MAJOR', someIssuesToPostText: '<total_count> SonarQube violations have been found.', sonarURL: '"http://10.142.139.149:9001"', subJobConfigs: [[projectPath: parentPath1 , sonarReportPath: path1],[projectPath: parentPath2 , sonarReportPath: path2],[projectPath: parentPath3 , sonarReportPath: path3]]     
}else if  (path2!=null){
	  sonarToGerrit category: 'Verified', changedLinesOnly: true, httpPassword: 'JEkinns', httpUsername: 'cn=ca_cemod,ou=SystemUsers,o=EEDIR', issueComment: '''<severity> SonarQube violation:

      <message>
	  Read more: <rule_url>''', issuesNotification: 'OWNER', issuesScore: '-1', newIssuesOnly: true, noIssuesNotification: 'NONE', noIssuesScore: '+1', noIssuesToPostText: 'SonarQube violations have not been found.', overrideCredentials: false, postScore: true, severity: 'MAJOR', someIssuesToPostText: '<total_count> SonarQube violations have been found.', sonarURL: '"http://10.142.139.149:9001"', subJobConfigs: [[projectPath:parentPath1, sonarReportPath: path1],[projectPath: parentPath2, sonarReportPath: path2]]     
}else if  (path1!=null){
	  sonarToGerrit category: 'Verified', changedLinesOnly: true, httpPassword: 'JEkinns', httpUsername: 'cn=ca_cemod,ou=SystemUsers,o=EEDIR', issueComment: '''<severity> SonarQube violation:

      <message>
	  Read more: <rule_url>''', issuesNotification: 'OWNER', issuesScore: '-1', newIssuesOnly: true, noIssuesNotification: 'NONE', noIssuesScore: '+1', noIssuesToPostText: 'SonarQube violations have not been found.', overrideCredentials: false, postScore: true, severity: 'MAJOR', someIssuesToPostText: '<total_count> SonarQube violations have been found.', sonarURL: '"http://10.142.139.149:9001"', subJobConfigs: [[projectPath: parentPath1, sonarReportPath: path1]]     
}
}
