package vcs2couch

import spock.lang.Specification

class CouchDBSpec extends Specification{
  static final String TEST_DB_NAME = "testdb"
  def couch = CouchDB.for("http://localhost:5984/", TEST_DB_NAME)

  def setup() { couch.deleteDb(false) }
  def cleanup() { couch.deleteDb(false) }

  def "a database can be created"() {
    when: "a new database is created"
    couch.createDb()
    then:
    couch.databaseExists()
  }

  def "a database can be deleted"() {
    when: "a new database is created"
    couch.createDb()
    and: "then deleted"
    couch.deleteDb(true)
    then:
    !couch.databaseExists()
  }
}
