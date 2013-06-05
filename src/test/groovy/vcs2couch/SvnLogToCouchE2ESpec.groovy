package vcs2couch

import spock.lang.Specification

class SvnLogToCouchE2ESpec extends Specification {
  def "an XML Subversion log is converted and inserted into couch"() {
    given: "A Subversion repository log in XML"
    and: "A new couch database"

    when: "The stream of log entries is read"

    then: "CouchDB will be populated"

  }
}
