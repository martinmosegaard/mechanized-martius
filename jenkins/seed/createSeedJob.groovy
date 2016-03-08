// This script is run as part of initializing Jenkins.
// It creates a seed job and copies the required DSL script to its workspace
import jenkins.model.*
import hudson.model.*
import hudson.slaves.*
import javaposse.jobdsl.plugin.*
import hudson.plugins.git.*
import java.util.Collections
import java.util.List
import hudson.security.ACL
import hudson.triggers.TimerTrigger
import com.cloudbees.plugins.credentials.*
import com.cloudbees.plugins.credentials.common.*
import com.cloudbees.plugins.credentials.domains.*
import com.cloudbees.jenkins.plugins.sshcredentials.impl.*
import hudson.plugins.sshslaves.*

println "Creating seed job"
new File("/usr/share/jenkins/seedJobConfig.xml").withInputStream { stream ->
  Jenkins.instance.createProjectFromXML("seed", stream)
}

def commands = [
    "mkdir -p /var/jenkins_home/workspace/seed",
    "cp -v /usr/share/jenkins/seedDsl.groovy /var/jenkins_home/workspace/seed/"
  ]
commands.each {
  println "Executing command ${it}"
  def process = it.execute()
  process.waitFor()

  def output = process.in.text
  println output
}

// Under construction -- don't use config.xml
println "Creating seed job 2"
def jobName = 'seed2'
def instance = Jenkins.getInstance()
def project = new FreeStyleProject(Jenkins.instance, jobName)

List<BranchSpec> branches = Collections.singletonList(new BranchSpec("*/master"))
List<UserRemoteConfig> repos = Collections.singletonList(
    new UserRemoteConfig("https://github.com/martinmosegaard/mechanized-martius.git",
        "",
        "",
        'jenkins'))
GitSCM scm = new GitSCM(repos,
        branches,
        false,
        null, null, null, null);
project.setScm(scm)
def jobDslBuildStep = new ExecuteDslScripts(
    scriptLocation=new ExecuteDslScripts.ScriptLocation(
        value = "false", targets="jenkins/seed/seedDsl.groovy", scriptText=""),
    ignoreExisting=false,
    removedJobAction=RemovedJobAction.DELETE,
    removedViewAction=RemovedViewAction.DELETE,
    lookupStrategy=LookupStrategy.JENKINS_ROOT,
    additionalClasspath='')

project.getBuildersList().add(jobDslBuildStep)
project.save()
Jenkins.instance.reload()
