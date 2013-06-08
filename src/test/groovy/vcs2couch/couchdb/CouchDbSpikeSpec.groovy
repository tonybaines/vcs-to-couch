package vcs2couch.couchdb

import groovy.json.JsonSlurper
import org.apache.commons.lang.time.StopWatch
import spock.lang.Specification
import vcs2couch.CouchDB
import vcs2couch.parsers.svn.Action
import vcs2couch.parsers.svn.Revision
import vcs2couch.parsers.svn.RevisionPath

import java.util.concurrent.FutureTask

import static vcs2couch.parsers.svn.Action.*

class CouchDbSpikeSpec extends Specification {
  private static final String COUCH_URI = "http://localhost:5984/commits"

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
    def couch = CouchDB.for("http://localhost:5984/", "commits")
    couch.recreateDb()

    def stopwatch = new StopWatch()
    stopwatch.start()
    (1..10000).each { i ->
      couch.insert(nextCommit(i).toJson())
    }
    stopwatch.stop()

    println("Took $stopwatch")

    then:
    1 == 1
  }

  private Revision nextCommit(int i) {
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
