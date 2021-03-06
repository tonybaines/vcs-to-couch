package vcs2couch.couchdb

import groovy.json.JsonSlurper
import org.apache.commons.lang.time.StopWatch
import spock.lang.Specification
import vcs2couch.parsers.svn.Revision
import vcs2couch.parsers.svn.RevisionPath

import static vcs2couch.parsers.svn.Action.*

class CouchDbSpikeSpec extends Specification {
  static final String COUCH_URI = "http://localhost:5984/commits"

  def "will get the contents of a database"() {
    when:
    def jsonText = COUCH_URI.toURL().text
    def commits = new JsonSlurper().parseText(jsonText)
    println jsonText
    then:
    commits.db_name == 'commits'
  }

  def "will add a document to a database"() {
    when:
    def couch = CommitsCouchDB.for("http://localhost:5984/", "commits")
    couch.recreateDb()

    def stopwatch = new StopWatch()
    def futureResponses = []
    stopwatch.start()
    (1..10000).each { i ->
      futureResponses << couch.insert(nextCommit(i).toJson())
    }
    stopwatch.split()
    println("Took $stopwatch to insert")

    assert futureResponses.every { response ->
      response.get().success
    }
    stopwatch.stop()
    println("Took $stopwatch to complete")

    then:
    couch.allDocuments().size() == 10000
  }

  def "can query a database for counts of occurrences using a map/reduce view"() {
    when:
    def couch = CommitsCouchDB.for("http://localhost:5984/", "commits")

    def pathCounts = couch.pathCounts

    pathCounts.each{
      println "${it.key} => ${it.value}"
    }
    then:
    1 == 1
  }

  static Revision nextCommit(int i) {
    new Revision(
      message: 'Changed the World',
      date: Date.newInstance(),
      rev: i.toString(),
      author: (i % 2 == 0) ? 'Bob' : 'Dave',
      paths: [
        new RevisionPath(
          action: ADDED,
          path: '/a/b/c.txt'
        ),
        new RevisionPath(
          action: ((i % 3 == 0) ? DELETED : MODIFIED),
          path: '/a/q.txt'
        ),
        new RevisionPath(
          action: ((i % 5 == 0) ? MODIFIED : REPLACED),
          path: '/a/b/z.txt'
        )
      ],
    )
  }
}
