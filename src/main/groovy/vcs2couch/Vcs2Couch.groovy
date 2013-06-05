package vcs2couch

import vcs2couch.parsers.svn.SvnHistoryParser

class Vcs2Couch {
  private final Object historyParser

  static Vcs2Couch svn() {
    return new Vcs2Couch(new SvnHistoryParser())
  }
  private Vcs2Couch(historyParser) {
    this.historyParser = historyParser
  }

  Vcs2Couch importFrom(Reader reader) {
    return this
  }

  def into(CouchDB couchDB, String dbName) {
    //To change body of created methods use File | Settings | File Templates.
  }
}
