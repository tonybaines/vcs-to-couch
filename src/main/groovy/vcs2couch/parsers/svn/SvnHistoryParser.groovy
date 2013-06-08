package vcs2couch.parsers.svn

import groovy.util.logging.Log

import javax.xml.stream.*

@Log
public class SvnHistoryParser {

  def process(Reader svnLogXml, Closure eachCommit) {
    def result = []
    log.info("Converting SVN history XML to revisions")
    XMLStreamReader reader = XMLInputFactory.newInstance().createXMLStreamReader(new BufferedReader(svnLogXml))
    use (StaxCategory) {
      try {
        while (reader.hasNext()) {
          if (reader.startElement && reader.name() == 'logentry') {
            eachCommit.call(processLogEntry(reader))
          }
          reader.next()
        }
      } finally {
        reader?.close()
      }
    }
    log.fine("Finished parsing history revisions")
    result
  }
  
  private def processLogEntry(logentry) {
    def num = logentry.revision
    def (author, date, message, paths) = processLogEntryChildren(logentry)
    new Revision(rev: num, author: author, date: date, message: message, paths: paths)
  }
  
  private def processLogEntryChildren(reader) {
    def author, date = null
    def paths = []
    def message = ''
    // Iterate, but watch out for the end of the 'logentry' node
    while (reader.hasNext() && !reader.atTheEndOf('logentry')) {
      if (reader.startElement) {
        switch (reader.name()) {
          case 'author':
            author = reader.text()
            break
          case 'date':
            date = Date.parse("yyyy-MM-dd'T'HH:mm:ss.S'Z'", reader.text())
            break
          case 'msg':
            message = reader.text()
            break
          case 'paths':
            paths = parseLogEntryPaths(reader)
            break
        }
      }
      reader.next()
    }
    [author, date, message, paths]
  }
  
  def parseLogEntryPaths(reader) {
    def paths = []
    // Iterate, but watch out for the end of the 'paths' node
    while (reader.hasNext() && !reader.atTheEndOf('paths')) {
      if (reader.startElement) {
        if (reader.name() == 'path') paths << new RevisionPath(action: Action.fromChar(reader.action), path: reader.text())
      }
      reader.next()
    }
    paths
  }
}
