package vcs2couch

import groovyx.net.http.AsyncHTTPBuilder
import groovyx.net.http.HTTPBuilder
import groovyx.net.http.RESTClient

import java.util.concurrent.Future

import static groovyx.net.http.ContentType.ANY
import static groovyx.net.http.ContentType.JSON

class CouchDB {
  private final RESTClient rest
  private final String dbName
  private final AsyncHTTPBuilder async

  static CouchDB 'for'(String url, String dbName) {
    return new CouchDB(url, dbName)
  }

  private CouchDB(String url, String dbName) {
    this.dbName = dbName
    this.rest = new RESTClient(url)
    this.async = new AsyncHTTPBuilder(uri: url)
    this.async.handler.failure = { resp ->
      return resp
    }
  }

  boolean databaseExists() {
    try {
      rest.head(path: dbName)
      return true
    }
    catch (ex) {
      return false
    }
  }

  def deleteDb(failOnError = false) {
    try {
      rest.delete(path: dbName)
    }
    catch (ex) {
      if (failOnError) throw ex
    }
  }

  def createDb() {
    rest.put(path: dbName)
  }

  def allDocuments(includeDocs=false) {
    def resp = rest.get(path: "$dbName/_all_docs", query: [include_docs: includeDocs], contentType: JSON.toString())

    return resp.data.rows
  }

  def recreateDb() {
    deleteDb()
    createDb()
  }

  /*
   * Using an groovyx.net.http.AsyncHTTPBuilder here is a huge time-saver, but need to think about
   * blocking and error handling
   * e.g.
   * (1..10000).each { i ->
   *   futureResponses << couch.insert(json)
   * }
   *
   * assert futureResponses.every { response ->
   *   response.get().success
   * }
   *
   * 20s vs. 90s for 10,000 documents inserted
   */

  Future insert(document) {
    async.post(path: dbName, requestContentType: JSON.toString(), body: document) { resp ->
      return resp
    }
  }

  def adHocQuery(jsQuery) {
    rest.post(path: "$dbName/_temp_view", query: [group: 'true'], contentType: JSON.toString(), requestContentType: JSON.toString(), body: jsQuery).data.rows
  }

  /**
   * Views are part of a collection inside of a Design Document
   * All operations are done in the context of creating/updating the
   * design doc with all of its views
   *  1. GET current design doc
   *  1a. Create a new one if necessary
   *  2. Update the existing 'views' property to add/remove/replace the required view
   */
  def createOrReplaceView(designDocName, view, mapper) {
    def designDoc
    try{
      designDoc = rest.get(path: "$dbName/_design/$designDocName", contentType: JSON.toString()).data
    } catch (ex) {
      designDoc = ['_id':"_design/$designDocName" as String, 'views': [:]]
    }

    println designDoc
    designDoc.views."$view" = mapper
    rest.put(path: "$dbName/_design/$designDocName", requestContentType: JSON.toString(), body: designDoc)
  }

  def findByView(designDoc, view, reduced=true) {
    rest.get(path: "$dbName/_design/$designDoc/_view/$view", query: [group: reduced], contentType: JSON.toString()).data
  }
}