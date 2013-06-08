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
    def commits = []
    parser.process(new StringReader(history)) { commit ->
      commits << commit
    }
    then:
    commits.size == 6
    commits[0].rev == '6'
    commits[0].paths.contains new RevisionPath(action: Action.ADDED, path: '/branches')
    commits[5].rev == '1'
  }
}