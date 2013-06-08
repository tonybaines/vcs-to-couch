package vcs2couch

import groovyx.net.http.AsyncHTTPBuilder
import groovyx.net.http.RESTClient
import static groovyx.net.http.ContentType.*

class CouchDB {
  private final RESTClient http
  private final String dbName
  private final AsyncHTTPBuilder async

  static CouchDB 'for'(String url, String dbName) {
    return new CouchDB(url, dbName)
  }

  private CouchDB(String url, String dbName) {
    this.dbName = dbName
    this.http = new RESTClient(url)
    this.async = new AsyncHTTPBuilder(uri: url)
  }

  boolean databaseExists() {
    try {
      http.head(path: dbName)
      return true
    }
    catch (ex) {
      return false
    }
  }

  def deleteDb(failOnError = false) {
    try {
      http.delete(path: dbName)
    }
    catch (ex) {
      if (failOnError) throw ex
    }
  }

  def createDb() {
    http.put(path: dbName)
  }

  def allDocuments() {
    def resp = http.get(path: "$dbName/_all_docs", contentType: JSON.toString())

    return resp.data.rows
  }

  def recreateDb() {
    deleteDb()
    createDb()
  }

  /*
   * Using an groovyx.net.http.AsyncHTTPBuilder here is a huge time-saver, but need to think about error handling
   *
   * 10s vs. 90s for 10,000 documents inserted
   */
  def insert(document) {
    async.post(path: dbName, requestContentType: JSON.toString(), body: document)
  }
}
