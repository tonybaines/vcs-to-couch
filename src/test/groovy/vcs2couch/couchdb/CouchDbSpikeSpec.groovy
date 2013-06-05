package vcs2couch.couchdb

import groovy.json.JsonSlurper
import groovyx.net.http.HTTPBuilder
import spock.lang.Specification

import static groovyx.net.http.ContentType.JSON
import static groovyx.net.http.Method.POST

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
    HTTPBuilder http = new HTTPBuilder(COUCH_URI)

    http.request(POST, JSON) {
      body = ['commit':
        ['message': 'Changed the World',
          'date': '2014-06-03T07:44:58+0000',
          'rev': '1',
          'paths': [
            [
              'action': 'ADDED',
              'path': '/a/b/c.txt'
            ]
          ],
          'author': 'Bob'
        ]
      ]
    }

    then:
    1 == 1
  }
}
