package vcs2couch.parsers.svn

import spock.lang.Specification
import spock.lang.Unroll


class ActionSpec extends Specification {

  @Unroll("#x -> #action")
  def "should convert a valid action character into a value"() {
    expect:
    Action.fromChar(x) == action
    where:
    x   | action
    'A' | Action.ADDED
    'M' | Action.MODIFIED
    'D' | Action.DELETED
    'R' | Action.REPLACED
    'a' | Action.ADDED
    'm' | Action.MODIFIED
    'd' | Action.DELETED
    'r' | Action.REPLACED
  }

  def "an invalid action character will throw an exception"() {
    when:
    Action.fromChar('z')
    then:
    thrown IllegalArgumentException
  }

}