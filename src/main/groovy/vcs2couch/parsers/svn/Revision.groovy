package vcs2couch.parsers.svn

import groovy.transform.Immutable
import vcs2couch.serialisation.SvnRevisionToJson

@Mixin(SvnRevisionToJson)
@Immutable final class Revision {
    String rev
    String author
    RevisionPath[] paths
    Date date
    String message
}
