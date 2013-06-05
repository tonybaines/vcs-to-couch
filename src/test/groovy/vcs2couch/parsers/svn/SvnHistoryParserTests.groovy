package vcs2couch.parsers.svn

import spock.lang.Specification

@Mixin(TestHelpers)
class SvnHistoryParserTests extends Specification {
  def parser = new SvnHistoryParser()
  def history

  def setup() {
    def repoPath = buildTestRepo()
    history = runCommand("svn log --xml --verbose $repoPath")
  }

  def "parses an XML history from a Subversion path"() {
    when:
    def history = parser.history(history)
    then:
    history.size == 6
    history[0].rev == '1'
    history[5].rev == '6'
    history[5].paths.contains new RevisionPath(action: Action.ADDED, path: '/branches')
  }
}