package vcs2couch.parsers.svn

import groovy.util.logging.Log

import javax.xml.stream.*

@Log
public class SvnHistoryParser {
  static final MAX_DAYS_HISTORY = 30
  private String username = null
  private String password = null
 
  public SvnHistoryParser() {this(null,null)}
  public SvnHistoryParser(username, password) {
    this.username=username
    this.password=password
  }
  
  def history(svnLogXml) {
    log.fine("Parsing the Subversion history XML")
    def result = parseRevisions(svnLogXml)
    result.sort {a,b -> a.rev <=> b.rev }
  }

  private def parseRevisions(svnLogXml) {
    def result = []
    log.info("Converting history XML to revisions:\n$svnLogXml")
    XMLStreamReader reader = XMLInputFactory.newInstance().createXMLStreamReader(new StringReader(svnLogXml));
    use (StaxCategory) {
      try {
        while (reader.hasNext()) {
          if (reader.startElement && reader.name() == 'logentry') {
            result << processLogEntry(reader)
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
