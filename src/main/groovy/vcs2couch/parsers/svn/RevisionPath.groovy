package vcs2couch.parsers.svn

import groovy.transform.Immutable

@Immutable final class RevisionPath {
    String path
    Action action
}
