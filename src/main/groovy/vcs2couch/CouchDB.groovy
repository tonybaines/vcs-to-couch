package vcs2couch

import groovyx.net.http.RESTClient

class CouchDB {
  private final RESTClient http

  static CouchDB at(String url) {
    return new CouchDB(url)
  }

  private CouchDB(String url) {
    this.http = new RESTClient(url)
  }

  boolean databaseExists(String dbName) {
    try {
      http.head(path: dbName)
      return true
    }
    catch (ex) {
      return false
    }
  }

  def deleteDb(String dbName, failOnError = false) {
    try {
      http.delete(path: dbName)
    }
    catch (ex) {
      if (failOnError) throw ex
    }
  }

  def allDocumentsIn(String dbName) {
    []
  }
}
