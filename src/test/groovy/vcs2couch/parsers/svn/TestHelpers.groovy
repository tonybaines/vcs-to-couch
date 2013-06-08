package vcs2couch.parsers.svn

import groovy.util.logging.Log

@Log
class TestHelpers {

  def String runCommand(command) {
    def stdOut = new StringBuffer()
    def stdErr = new StringBuffer()
    Process process = command.execute()
    // Windows blocking issues mean that at least one of
    // stderr/stdout must be processed when calling waitFor()
    def pump = Thread.start {
      process.err.eachLine{ stdErr.append it } }
    process.in.eachLine{ stdOut.append it }
    pump.join()
    process.waitFor()
    
    if (process.exitValue() > 0) {
      log.severe stdErr.toString()
      throw new IllegalArgumentException("Problem running ${command}: ${stdErr.toString()}")
    }
    stdOut.toString()
  }

  def buildTestRepo() {
    def repoDir = "${new File('.').absolutePath}/build/test/test-repo"
    // Workaround for Windows file paths
    repoDir = repoDir.replace('\\', '/').replaceFirst('[A-Z]\\:', '')

    new File(repoDir).deleteDir()
    new File(repoDir).mkdirs()
    runCommand("svnadmin create $repoDir")
    def repoPath = "file://$repoDir"
    runCommand("svn mkdir $repoPath/trunk -m test ")
    runCommand("svn mkdir $repoPath/trunk/src -m test ")
    runCommand("svn mkdir $repoPath/trunk/test -m test")
    runCommand("svn mkdir $repoPath/tags -m test")
    runCommand("svn mkdir $repoPath/tags/release-1.0 -m test")
    runCommand("svn mkdir $repoPath/branches -m test")

    repoPath
  }

  def svn(cmd, directory = '.') {
    runCommand("svn $cmd")
  }
  
}
