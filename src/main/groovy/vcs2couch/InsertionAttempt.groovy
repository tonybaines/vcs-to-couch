package vcs2couch

import vcs2couch.parsers.svn.Revision
import java.util.concurrent.Future

final class InsertionAttempt {
  Revision revision
  Future responseFuture

  boolean getWasSuccessful() {
    responseFuture.get().success
  }

  boolean getFailed() { !wasSuccessful }

  @Override
  String toString() {
    "[${responseFuture.get().status}]: $revision"
  }
}
