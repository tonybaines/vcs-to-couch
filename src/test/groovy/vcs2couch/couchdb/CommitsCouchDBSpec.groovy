package vcs2couch.couchdb

import org.hamcrest.Matchers
import spock.lang.Specification

class CommitsCouchDBSpec extends Specification {
  def couch = Mock(CouchDB)
  def commitsCouch = new CommitsCouchDB(couch)

  def "can be used as a CouchDB"() {
    when:
    commitsCouch.createDb()
    commitsCouch.deleteDb()

    then:
    1 * couch.createDb()
    1 * couch.deleteDb()
  }

  def "ensures that Commits-specific design documents are available"() {
    when:
    commitsCouch.pathCounts

    then:
    1 * couch.createOrReplaceView('indexes', 'pathCounts', Matchers.any(Object))
    1 * couch.findByView('indexes', 'pathCounts')
  }

}
