package vcs2couch.parsers.svn

import groovy.transform.Immutable

@Immutable final class Revision {
    String rev
    String author
    RevisionPath[] paths
    Date date
    String message
}
