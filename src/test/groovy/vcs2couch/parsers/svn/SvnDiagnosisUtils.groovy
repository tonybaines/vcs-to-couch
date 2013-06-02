package vcs2couch.parsers.svn

import groovy.util.logging.Log

@Log
class SvnDiagnosisUtils {

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
  
}
