package vcs2couch

import spock.lang.Specification
import vcs2couch.parsers.svn.TestHelpers

@Mixin(TestHelpers)
class SvnLogToCouchE2ESpec extends Specification {
  def couch = CouchDB.for("http://localhost:5984/", "commits")
  def vcs2couch = Vcs2Couch.svn()

  def "an XML Subversion log is converted and inserted into couch"() {
    given: "A Subversion repository log in XML"
    def repoPath = buildTestRepo()
    def history = new StringReader(svn("log --xml --verbose $repoPath"))
    and: "A new couch database"
    couch.recreateDb()

    when: "The stream of log entries is read"
    def errors = vcs2couch.importFrom(history).into(couch)

    then: "CouchDB will be populated"
    couch.databaseExists()
    couch.allDocuments().size() == 6
    errors.empty
  }
}
