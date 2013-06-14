package vcs2couch.couchdb

class CommitsCouchDB {
  @Delegate
  final CouchDB couch

  // The space at the start of these strings is important;
  // without it the request fails with reason "invalid UTF-8 JSON"
  static final String SUM_REDUCER = " function(key, values, rereduce) { return sum(values); }"
  static final String PATHS_MAPPER = """ function(doc){
                   doc.commit.paths.forEach(function(change){
                     emit(change.path,1);
                   });
                 }"""


  static CommitsCouchDB 'for'(String url, String dbName) {
    return new CommitsCouchDB(url, dbName)
  }

  CommitsCouchDB(String url, String dbName) {
    this(CouchDB.for(url,dbName))
  }

  CommitsCouchDB(CouchDB couch) {
    this.couch = couch
  }

  def getPathCounts() {
    /* This doesn't need to be clever about checking for existing
       design documents, Couch is smart about caching views when the
       map/reduce hasn't changed
     */
    createOrReplaceView('indexes', 'pathCounts',
      [
        map: PATHS_MAPPER,
        reduce: SUM_REDUCER
      ]
    )
    findByView('indexes', 'pathCounts')
  }
}
