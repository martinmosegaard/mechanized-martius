// This script is run as part of initializing Jenkins.
// It creates a seed job that checks out a Job DSL script from GitHub.
import com.cloudbees.jenkins.plugins.sshcredentials.impl.*
import com.cloudbees.plugins.credentials.*
import com.cloudbees.plugins.credentials.common.*
import com.cloudbees.plugins.credentials.domains.*
import hudson.model.*
import hudson.plugins.git.*
import hudson.plugins.sshslaves.*
import hudson.security.ACL
import hudson.slaves.*
import hudson.triggers.TimerTrigger
import java.util.Collections
import java.util.List
import javaposse.jobdsl.plugin.*
import jenkins.model.*

println "Creating seed job"

def jobName = 'seed'
def project = new FreeStyleProject(Jenkins.instance, jobName)

List<BranchSpec> branches = Collections.singletonList(new BranchSpec('*/master'))
List<UserRemoteConfig> repos = Collections.singletonList(
    new UserRemoteConfig('https://github.com/martinmosegaard/mechanized-martius.git',
        '',
        '',
        'jenkins'))
GitSCM scm = new GitSCM(repos,
    branches,
    false,
    null, null, null, null);
project.setScm(scm)

def script = new ExecuteDslScripts.ScriptLocation(
    value = 'false', targets = 'jenkins/seed/seedDsl.groovy', scriptText = '')
def jobDslBuildStep = new ExecuteDslScripts(
    scriptLocation = script,
    ignoreExisting = false,
    removedJobAction = RemovedJobAction.DELETE,
    removedViewAction = RemovedViewAction.DELETE,
    lookupStrategy = LookupStrategy.JENKINS_ROOT,
    additionalClasspath = '')

project.getBuildersList().add(jobDslBuildStep)
project.save()
Jenkins.instance.reload()
