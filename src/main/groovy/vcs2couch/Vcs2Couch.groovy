package vcs2couch

import vcs2couch.parsers.svn.SvnHistoryParser

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
    def responses = []
    historyParser.process(source) { commit ->
      responses << new InsertionAttempt(revision: commit, responseFuture: couch.insert(commit.toJson()))
    }

    return responses.grep { it.failed }
  }
}
