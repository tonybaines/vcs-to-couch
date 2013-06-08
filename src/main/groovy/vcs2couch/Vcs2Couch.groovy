package vcs2couch

import vcs2couch.parsers.svn.SvnHistoryParser
import vcs2couch.serialisation.SvnRevisionToJson

class Vcs2Couch {
  private Object historyParser
  private Reader source

  static Vcs2Couch svn() {
    return new Vcs2Couch(new SvnHistoryParser())
  }
  private Vcs2Couch(historyParser) {
    this.historyParser = historyParser
  }

  Vcs2Couch importFrom(Reader source) {
    this.source = source
    return this
  }

  def into(CouchDB couch) {
    historyParser.process(source) { commit ->
      use(SvnRevisionToJson) {
        couch.insert(commit.toJson())
      }
    }
  }
}
