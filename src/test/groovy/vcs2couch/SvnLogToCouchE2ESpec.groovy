package vcs2couch

import spock.lang.Specification
import spock.lang.Unroll
import vcs2couch.parsers.svn.TestHelpers

@Mixin(TestHelpers)
class SvnLogToCouchE2ESpec extends Specification {
  def couchDB = CouchDB.at("http://localhost:5984/")
  def vcs2couch = Vcs2Couch.svn()

  @Unroll
  def "an XML Subversion log is converted and inserted into couch"() {
    given: "A Subversion repository log in XML"
    def repoPath = buildTestRepo()
    def log = new StringReader(svn("log --xml --verbose $repoPath"))
    and: "A new couch database"
    couchDB.deleteDb "commits"

    when: "The stream of log entries is read"
    vcs2couch.importFrom(log).into(couchDB, "commits")

    then: "CouchDB will be populated"
    couchDB.databaseExists("commits")
    couchDB.allDocumentsIn("commits").size() == 10
  }
}
