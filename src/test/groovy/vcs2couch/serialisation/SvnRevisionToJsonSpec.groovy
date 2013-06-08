package vcs2couch.serialisation

import groovy.json.JsonSlurper
import spock.lang.Specification
import vcs2couch.parsers.svn.Action
import vcs2couch.parsers.svn.Revision
import vcs2couch.parsers.svn.RevisionPath

@SuppressWarnings("GroovyVariableNotAssigned")
class SvnRevisionToJsonSpec extends Specification {
  def "will convert a svn.Revision into JSON"() {
    when:
    def revision = new Revision(
      rev: "1",
      author: "Bob",
      date: new Date(),
      message: "Changed the World",
      paths: [new RevisionPath(path: "/a/b/c.txt", action: Action.ADDED)])

    def json
    json = new JsonSlurper().parseText(revision.toJson())

    then:
    json.commit.rev == "1"
    json.commit.author == "Bob"
    json.commit.message == "Changed the World"
    json.commit.paths.size == 1

  }
}
