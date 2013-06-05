package vcs2couch.serialisation

import groovy.json.JsonBuilder
import vcs2couch.parsers.svn.Revision

@Category(Revision)
class SvnRevisionToJson {
  static String toJson(Revision revision) {
    new JsonBuilder('commit': revision).toPrettyString()
  }
}
