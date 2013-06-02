package vcs2couch.parsers.svn;

public enum Action {
  ADDED('A'), DELETED('D'), MODIFIED('M'), REPLACED('R')
  
  private def actionChar
  Action(actionChar){
    this.actionChar = actionChar
  }
  
  static def fromChar(character) {
    def retVal = this.values().find { it.actionChar.equalsIgnoreCase(character) }
    if (retVal == null) throw new IllegalArgumentException("$character is not a valid action")
    retVal
  }
}
