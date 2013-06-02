package vcs2couch.parsers.svn

import spock.lang.Specification

@Mixin(SvnDiagnosisUtils)
class SvnHistoryParserTests extends Specification {
  def parser = new SvnHistoryParser()
  def history

  def setup() {
    def repoDir = "${new File('.').absolutePath}/target/test/test-repo"
    // Workaround for Windows file paths
    repoDir = repoDir.replace('\\', '/').replaceFirst('[A-Z]\\:', '')
    def repoPath = buildTestRepo(repoDir)
    history = runCommand("svn log --xml --verbose $repoPath")
  }

  def "parses an XML history from a Subversion path"() {
    when:
    def history = parser.history(history)
    then:
    history.size == 6
    history[0].rev == '1'
    history[5].rev == '6'
//    history[5].paths, hasItemInArray(new RevisionPath(action: Action.ADDED, path: '/branches'))
  }

  def buildTestRepo(String repoDir) {
    new File(repoDir).deleteDir()
    new File(repoDir).mkdirs()
    runCommand("svnadmin create $repoDir")
    def repoPath = "file://$repoDir"
    runCommand("svn mkdir $repoPath/trunk -m'test'")
    runCommand("svn mkdir $repoPath/trunk/src -m'test'")
    runCommand("svn mkdir $repoPath/trunk/test -m'test'")
    runCommand("svn mkdir $repoPath/tags -m'test")
    runCommand("svn mkdir $repoPath/tags/release-1.0 -m'test")
    runCommand("svn mkdir $repoPath/branches -m'test")

    repoPath
  }

  def svn(cmd, directory = '.') {
    runCommand("svn $cmd")
  }
}